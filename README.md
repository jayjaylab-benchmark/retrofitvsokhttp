# retrofitvsokhttp
This micro benchmark compares performance of retrofit2 and okhttp3.
Under MAC OS X yosemite version 10.10.5(14F27), 2.7GHz Intel core i5, 8GB 1867MHz DD3, 
Measured code fragments includes sending a GET request to "https://api.github.com/users/octocat/repos" resource and unmarshalling the response json string to the 
corresponding POJO class.

### MicroBenchmark Result #1(2016-07-07T22:41:40+09:00)
```
Benchmark                                  Mode  Cnt     Score     Error  Units
AndroidHttpLibraryBenchmark.testOkhttp3      ss   10  2389.107 ± 550.925  ms/op
AndroidHttpLibraryBenchmark.testRetrofit2    ss   10  2476.903 ± 327.647  ms/op
```
### MicroBenchmark Result #2(2016-07-08T00:47:12+09:00)
```
Benchmark                                  Mode  Cnt     Score     Error  Units
AndroidHttpLibraryBenchmark.testOkhttp3      ss   10  2472.586 ± 245.455  ms/op
AndroidHttpLibraryBenchmark.testRetrofit2    ss   10  2920.377 ± 760.955  ms/op
```
### MicroBenchmark Result #3(2016-07-08T12:39:23+09:00)
```
Benchmark                                  Mode  Cnt     Score      Error  Units
AndroidHttpLibraryBenchmark.testOkhttp3      ss   10  2427.628 ±  556.801  ms/op
AndroidHttpLibraryBenchmark.testRetrofit2    ss   10  3110.812 ± 1659.656  ms/op
```

# Result Explanation 
I think what you choose is your responsibility. Retrofit2 provides a simple client interface for Restful API. Okhttp3 provides the whole bunch of HTTP funtionailty. As Jake Wharton mentioned[[subreddit]](https://redd.it/4rpdlv), this benchmark runs on Hotspot JVM not on Dalvik or ART which may show different symptom on Dalvik or ART. But I wonder pattern of the result will be changed if it's run on Dalvik or ART. Um I don't think it's likely to change much. So in my case I will choose Okhttp3 though it requires I add some more classe for optimizations.
** Anyways this benchmark must run on Dalvik or ART for sure. This benchmark only suggests a time from creating the first request, network cost, receving the response and unmarshalling json to POJO not 2nd or next requests. **

## Micro Benchmarking source codes
```java
public class AndroidHttpLibraryBenchmark {
    @org.openjdk.jmh.annotations.State(Scope.Benchmark)
    public static class State {
        public Retrofit retrofit;
        public OkHttpClient okHttpClient;
        public ObjectMapper objectMapper;

        @Setup(Level.Trial)
        public void setUp() {
            System.out.println("########## setUp() ##########");
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
            okHttpClient = new OkHttpClient();
            objectMapper = new ObjectMapper();
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            System.out.println("########## tearDown() ##########");
            retrofit = null;
            objectMapper = null;
            okHttpClient = null;
        }
    }

    @Benchmark
    @BenchmarkMode({Mode.SingleShotTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testRetrofit2(State state, Blackhole blackhole) {
        GitHubService service = state.retrofit.create(GitHubService.class);
        Call<List<Repo>> repos = service.listRepos("octocat");
        List<Repo> result = null;

        try {
            retrofit2.Response<List<Repo>> response = repos.execute();
            result = response.body();
        } catch(Exception e) {
            e.printStackTrace();
        }

        blackhole.consume(result);
    }

    @Benchmark
    @BenchmarkMode({Mode.SingleShotTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testOkhttp3(State state, Blackhole blackhole) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("api.github.com")
                .addPathSegment("users")
                .addPathSegment("octocat")
                .addPathSegment("repos")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();
        List<Repo> result = null;

        try {
            Response response = state.okHttpClient.newCall(request).execute();
            result = state.objectMapper.readValue(response.body().charStream(),
                    new TypeReference<List<Repo>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }

        blackhole.consume(result);
    }
}
```


### MicroBenchmark Result Details #1(2016-07-07T22:41:40+09:00)
```
# JMH 1.12 (released 97 days ago, please consider updating!)
# VM version: JDK 1.8.0_45, VM 25.45-b02
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/bin/java
# VM options: <none>
# Warmup: <none>
# Measurement: 1 iterations, single-shot each
# Timeout: 10 min per iteration
# Threads: 1 thread
# Benchmark mode: Single shot invocation time
# Benchmark: com.jayjaylab.benchmark.AndroidHttpLibraryBenchmark.testOkhttp3

# Run progress: 0.00% complete, ETA 00:00:00
# Fork: 1 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2471.418 ms/op

# Run progress: 5.00% complete, ETA 00:00:58
# Fork: 2 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2387.632 ms/op

# Run progress: 10.00% complete, ETA 00:00:54
# Fork: 3 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2614.256 ms/op

# Run progress: 15.00% complete, ETA 00:00:52
# Fork: 4 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2409.545 ms/op

# Run progress: 20.00% complete, ETA 00:00:48
# Fork: 5 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2587.648 ms/op

# Run progress: 25.00% complete, ETA 00:00:46
# Fork: 6 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
1992.963 ms/op

# Run progress: 30.00% complete, ETA 00:00:41
# Fork: 7 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2496.987 ms/op

# Run progress: 35.00% complete, ETA 00:00:39
# Fork: 8 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2441.967 ms/op

# Run progress: 40.00% complete, ETA 00:00:36
# Fork: 9 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
1579.302 ms/op

# Run progress: 45.00% complete, ETA 00:00:32
# Fork: 10 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2909.348 ms/op


Result "testOkhttp3":
  N = 10
  mean =   2389.107 ±(99.9%) 550.925 ms/op

  Histogram, ms/op:
    [1000.000, 1125.000) = 0 
    [1125.000, 1250.000) = 0 
    [1250.000, 1375.000) = 0 
    [1375.000, 1500.000) = 0 
    [1500.000, 1625.000) = 1 
    [1625.000, 1750.000) = 0 
    [1750.000, 1875.000) = 0 
    [1875.000, 2000.000) = 1 
    [2000.000, 2125.000) = 0 
    [2125.000, 2250.000) = 0 
    [2250.000, 2375.000) = 0 
    [2375.000, 2500.000) = 5 
    [2500.000, 2625.000) = 2 
    [2625.000, 2750.000) = 0 
    [2750.000, 2875.000) = 0 

  Percentiles, ms/op:
      p(0.0000) =   1579.302 ms/op
     p(50.0000) =   2456.693 ms/op
     p(90.0000) =   2879.839 ms/op
     p(95.0000) =   2909.348 ms/op
     p(99.0000) =   2909.348 ms/op
     p(99.9000) =   2909.348 ms/op
     p(99.9900) =   2909.348 ms/op
     p(99.9990) =   2909.348 ms/op
     p(99.9999) =   2909.348 ms/op
    p(100.0000) =   2909.348 ms/op


# JMH 1.12 (released 97 days ago, please consider updating!)
# VM version: JDK 1.8.0_45, VM 25.45-b02
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/bin/java
# VM options: <none>
# Warmup: <none>
# Measurement: 1 iterations, single-shot each
# Timeout: 10 min per iteration
# Threads: 1 thread
# Benchmark mode: Single shot invocation time
# Benchmark: com.jayjaylab.benchmark.AndroidHttpLibraryBenchmark.testRetrofit2

# Run progress: 50.00% complete, ETA 00:00:29
# Fork: 1 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2797.613 ms/op

# Run progress: 55.00% complete, ETA 00:00:27
# Fork: 2 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2366.259 ms/op

# Run progress: 60.00% complete, ETA 00:00:24
# Fork: 3 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2663.821 ms/op

# Run progress: 65.00% complete, ETA 00:00:21
# Fork: 4 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2508.463 ms/op

# Run progress: 70.00% complete, ETA 00:00:18
# Fork: 5 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2507.066 ms/op

# Run progress: 75.00% complete, ETA 00:00:15
# Fork: 6 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2525.488 ms/op

# Run progress: 80.00% complete, ETA 00:00:12
# Fork: 7 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2501.223 ms/op

# Run progress: 85.00% complete, ETA 00:00:09
# Fork: 8 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
1967.030 ms/op

# Run progress: 90.00% complete, ETA 00:00:06
# Fork: 9 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2527.677 ms/op

# Run progress: 95.00% complete, ETA 00:00:03
# Fork: 10 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2404.388 ms/op


Result "testRetrofit2":
  N = 10
  mean =   2476.903 ±(99.9%) 327.647 ms/op

  Histogram, ms/op:
    [1900.000, 1950.000) = 0 
    [1950.000, 2000.000) = 1 
    [2000.000, 2050.000) = 0 
    [2050.000, 2100.000) = 0 
    [2100.000, 2150.000) = 0 
    [2150.000, 2200.000) = 0 
    [2200.000, 2250.000) = 0 
    [2250.000, 2300.000) = 0 
    [2300.000, 2350.000) = 0 
    [2350.000, 2400.000) = 1 
    [2400.000, 2450.000) = 1 
    [2450.000, 2500.000) = 0 
    [2500.000, 2550.000) = 5 
    [2550.000, 2600.000) = 0 
    [2600.000, 2650.000) = 0 
    [2650.000, 2700.000) = 1 
    [2700.000, 2750.000) = 0 

  Percentiles, ms/op:
      p(0.0000) =   1967.030 ms/op
     p(50.0000) =   2507.764 ms/op
     p(90.0000) =   2784.234 ms/op
     p(95.0000) =   2797.613 ms/op
     p(99.0000) =   2797.613 ms/op
     p(99.9000) =   2797.613 ms/op
     p(99.9900) =   2797.613 ms/op
     p(99.9990) =   2797.613 ms/op
     p(99.9999) =   2797.613 ms/op
    p(100.0000) =   2797.613 ms/op


# Run complete. Total time: 00:01:00

Benchmark                                  Mode  Cnt     Score     Error  Units
AndroidHttpLibraryBenchmark.testOkhttp3      ss   10  2389.107 ± 550.925  ms/op
AndroidHttpLibraryBenchmark.testRetrofit2    ss   10  2476.903 ± 327.647  ms/op
```

### MicroBenchmark Result Details #2(2016-07-08T00:47:12+09:00)
```
# JMH 1.12 (released 98 days ago, please consider updating!)
# VM version: JDK 1.8.0_45, VM 25.45-b02
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/bin/java
# VM options: <none>
# Warmup: <none>
# Measurement: 1 iterations, single-shot each
# Timeout: 10 min per iteration
# Threads: 1 thread
# Benchmark mode: Single shot invocation time
# Benchmark: com.jayjaylab.benchmark.AndroidHttpLibraryBenchmark.testOkhttp3

# Run progress: 0.00% complete, ETA 00:00:00
# Fork: 1 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2627.537 ms/op

# Run progress: 5.00% complete, ETA 00:01:08
# Fork: 2 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2532.760 ms/op

# Run progress: 10.00% complete, ETA 00:01:00
# Fork: 3 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2043.908 ms/op

# Run progress: 15.00% complete, ETA 00:00:52
# Fork: 4 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2405.736 ms/op

# Run progress: 20.00% complete, ETA 00:00:49
# Fork: 5 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2526.413 ms/op

# Run progress: 25.00% complete, ETA 00:00:46
# Fork: 6 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2542.041 ms/op

# Run progress: 30.00% complete, ETA 00:00:43
# Fork: 7 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2569.559 ms/op

# Run progress: 35.00% complete, ETA 00:00:40
# Fork: 8 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2452.220 ms/op

# Run progress: 40.00% complete, ETA 00:00:36
# Fork: 9 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2529.902 ms/op

# Run progress: 45.00% complete, ETA 00:00:33
# Fork: 10 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2495.779 ms/op


Result "testOkhttp3":
  N = 10
  mean =   2472.586 ±(99.9%) 245.455 ms/op

  Histogram, ms/op:
    [2000.000, 2050.000) = 1 
    [2050.000, 2100.000) = 0 
    [2100.000, 2150.000) = 0 
    [2150.000, 2200.000) = 0 
    [2200.000, 2250.000) = 0 
    [2250.000, 2300.000) = 0 
    [2300.000, 2350.000) = 0 
    [2350.000, 2400.000) = 0 
    [2400.000, 2450.000) = 1 
    [2450.000, 2500.000) = 2 
    [2500.000, 2550.000) = 4 
    [2550.000, 2600.000) = 1 
    [2600.000, 2650.000) = 1 

  Percentiles, ms/op:
      p(0.0000) =   2043.908 ms/op
     p(50.0000) =   2528.158 ms/op
     p(90.0000) =   2621.739 ms/op
     p(95.0000) =   2627.537 ms/op
     p(99.0000) =   2627.537 ms/op
     p(99.9000) =   2627.537 ms/op
     p(99.9900) =   2627.537 ms/op
     p(99.9990) =   2627.537 ms/op
     p(99.9999) =   2627.537 ms/op
    p(100.0000) =   2627.537 ms/op


# JMH 1.12 (released 98 days ago, please consider updating!)
# VM version: JDK 1.8.0_45, VM 25.45-b02
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/bin/java
# VM options: <none>
# Warmup: <none>
# Measurement: 1 iterations, single-shot each
# Timeout: 10 min per iteration
# Threads: 1 thread
# Benchmark mode: Single shot invocation time
# Benchmark: com.jayjaylab.benchmark.AndroidHttpLibraryBenchmark.testRetrofit2

# Run progress: 50.00% complete, ETA 00:00:30
# Fork: 1 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
3276.400 ms/op

# Run progress: 55.00% complete, ETA 00:00:28
# Fork: 2 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
4067.664 ms/op

# Run progress: 60.00% complete, ETA 00:00:26
# Fork: 3 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2508.256 ms/op

# Run progress: 65.00% complete, ETA 00:00:22
# Fork: 4 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2553.624 ms/op

# Run progress: 70.00% complete, ETA 00:00:19
# Fork: 5 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
3037.822 ms/op

# Run progress: 75.00% complete, ETA 00:00:16
# Fork: 6 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
3019.701 ms/op

# Run progress: 80.00% complete, ETA 00:00:13
# Fork: 7 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2614.698 ms/op

# Run progress: 85.00% complete, ETA 00:00:09
# Fork: 8 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2450.501 ms/op

# Run progress: 90.00% complete, ETA 00:00:06
# Fork: 9 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2536.680 ms/op

# Run progress: 95.00% complete, ETA 00:00:03
# Fork: 10 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
3138.425 ms/op


Result "testRetrofit2":
  N = 10
  mean =   2920.377 ±(99.9%) 760.955 ms/op

  Histogram, ms/op:
    [2000.000, 2250.000) = 0 
    [2250.000, 2500.000) = 1 
    [2500.000, 2750.000) = 4 
    [2750.000, 3000.000) = 0 
    [3000.000, 3250.000) = 3 
    [3250.000, 3500.000) = 1 
    [3500.000, 3750.000) = 0 
    [3750.000, 4000.000) = 0 
    [4000.000, 4250.000) = 1 
    [4250.000, 4500.000) = 0 
    [4500.000, 4750.000) = 0 

  Percentiles, ms/op:
      p(0.0000) =   2450.501 ms/op
     p(50.0000) =   2817.199 ms/op
     p(90.0000) =   3988.538 ms/op
     p(95.0000) =   4067.664 ms/op
     p(99.0000) =   4067.664 ms/op
     p(99.9000) =   4067.664 ms/op
     p(99.9900) =   4067.664 ms/op
     p(99.9990) =   4067.664 ms/op
     p(99.9999) =   4067.664 ms/op
    p(100.0000) =   4067.664 ms/op


# Run complete. Total time: 00:01:05

Benchmark                                  Mode  Cnt     Score     Error  Units
AndroidHttpLibraryBenchmark.testOkhttp3      ss   10  2472.586 ± 245.455  ms/op
AndroidHttpLibraryBenchmark.testRetrofit2    ss   10  2920.377 ± 760.955  ms/op
```

### MicroBenchmark Result Details #3(2016-07-08T12:39:23+09:00)
```
# JMH 1.12 (released 98 days ago, please consider updating!)
# VM version: JDK 1.8.0_45, VM 25.45-b02
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/bin/java
# VM options: <none>
# Warmup: <none>
# Measurement: 1 iterations, single-shot each
# Timeout: 10 min per iteration
# Threads: 1 thread
# Benchmark mode: Single shot invocation time
# Benchmark: com.jayjaylab.benchmark.AndroidHttpLibraryBenchmark.testOkhttp3

# Run progress: 0.00% complete, ETA 00:00:00
# Fork: 1 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2863.598 ms/op

# Run progress: 5.00% complete, ETA 00:01:10
# Fork: 2 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2484.737 ms/op

# Run progress: 10.00% complete, ETA 00:01:00
# Fork: 3 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2517.306 ms/op

# Run progress: 15.00% complete, ETA 00:00:55
# Fork: 4 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2320.270 ms/op

# Run progress: 20.00% complete, ETA 00:00:51
# Fork: 5 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2662.495 ms/op

# Run progress: 25.00% complete, ETA 00:00:47
# Fork: 6 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
1460.156 ms/op

# Run progress: 30.00% complete, ETA 00:00:42
# Fork: 7 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2500.058 ms/op

# Run progress: 35.00% complete, ETA 00:00:39
# Fork: 8 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2494.404 ms/op

# Run progress: 40.00% complete, ETA 00:00:36
# Fork: 9 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2487.372 ms/op

# Run progress: 45.00% complete, ETA 00:00:33
# Fork: 10 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2485.887 ms/op


Result "testOkhttp3":
  N = 10
  mean =   2427.628 ±(99.9%) 556.801 ms/op

  Histogram, ms/op:
    [1000.000, 1125.000) = 0 
    [1125.000, 1250.000) = 0 
    [1250.000, 1375.000) = 0 
    [1375.000, 1500.000) = 1 
    [1500.000, 1625.000) = 0 
    [1625.000, 1750.000) = 0 
    [1750.000, 1875.000) = 0 
    [1875.000, 2000.000) = 0 
    [2000.000, 2125.000) = 0 
    [2125.000, 2250.000) = 0 
    [2250.000, 2375.000) = 1 
    [2375.000, 2500.000) = 4 
    [2500.000, 2625.000) = 2 
    [2625.000, 2750.000) = 1 
    [2750.000, 2875.000) = 1 

  Percentiles, ms/op:
      p(0.0000) =   1460.156 ms/op
     p(50.0000) =   2490.888 ms/op
     p(90.0000) =   2843.487 ms/op
     p(95.0000) =   2863.598 ms/op
     p(99.0000) =   2863.598 ms/op
     p(99.9000) =   2863.598 ms/op
     p(99.9900) =   2863.598 ms/op
     p(99.9990) =   2863.598 ms/op
     p(99.9999) =   2863.598 ms/op
    p(100.0000) =   2863.598 ms/op


# JMH 1.12 (released 98 days ago, please consider updating!)
# VM version: JDK 1.8.0_45, VM 25.45-b02
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/bin/java
# VM options: <none>
# Warmup: <none>
# Measurement: 1 iterations, single-shot each
# Timeout: 10 min per iteration
# Threads: 1 thread
# Benchmark mode: Single shot invocation time
# Benchmark: com.jayjaylab.benchmark.AndroidHttpLibraryBenchmark.testRetrofit2

# Run progress: 50.00% complete, ETA 00:00:30
# Fork: 1 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
3331.477 ms/op

# Run progress: 55.00% complete, ETA 00:00:28
# Fork: 2 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2482.829 ms/op

# Run progress: 60.00% complete, ETA 00:00:24
# Fork: 3 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2525.349 ms/op

# Run progress: 65.00% complete, ETA 00:00:21
# Fork: 4 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2536.720 ms/op

# Run progress: 70.00% complete, ETA 00:00:18
# Fork: 5 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2512.128 ms/op

# Run progress: 75.00% complete, ETA 00:00:15
# Fork: 6 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2509.455 ms/op

# Run progress: 80.00% complete, ETA 00:00:12
# Fork: 7 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2515.939 ms/op

# Run progress: 85.00% complete, ETA 00:00:09
# Fork: 8 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
2529.191 ms/op

# Run progress: 90.00% complete, ETA 00:00:06
# Fork: 9 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
5603.492 ms/op

# Run progress: 95.00% complete, ETA 00:00:03
# Fork: 10 of 10
Iteration   1: ########## setUp() ##########
########## tearDown() ##########
4561.543 ms/op


Result "testRetrofit2":
  N = 10
  mean =   3110.812 ±(99.9%) 1659.656 ms/op

  Histogram, ms/op:
    [2000.000, 2250.000) = 0 
    [2250.000, 2500.000) = 1 
    [2500.000, 2750.000) = 6 
    [2750.000, 3000.000) = 0 
    [3000.000, 3250.000) = 0 
    [3250.000, 3500.000) = 1 
    [3500.000, 3750.000) = 0 
    [3750.000, 4000.000) = 0 
    [4000.000, 4250.000) = 0 
    [4250.000, 4500.000) = 0 
    [4500.000, 4750.000) = 1 
    [4750.000, 5000.000) = 0 
    [5000.000, 5250.000) = 0 
    [5250.000, 5500.000) = 0 
    [5500.000, 5750.000) = 1 

  Percentiles, ms/op:
      p(0.0000) =   2482.829 ms/op
     p(50.0000) =   2527.270 ms/op
     p(90.0000) =   5499.298 ms/op
     p(95.0000) =   5603.492 ms/op
     p(99.0000) =   5603.492 ms/op
     p(99.9000) =   5603.492 ms/op
     p(99.9900) =   5603.492 ms/op
     p(99.9990) =   5603.492 ms/op
     p(99.9999) =   5603.492 ms/op
    p(100.0000) =   5603.492 ms/op


# Run complete. Total time: 00:01:07

Benchmark                                  Mode  Cnt     Score      Error  Units
AndroidHttpLibraryBenchmark.testOkhttp3      ss   10  2427.628 ±  556.801  ms/op
AndroidHttpLibraryBenchmark.testRetrofit2    ss   10  3110.812 ± 1659.656  ms/op
```
