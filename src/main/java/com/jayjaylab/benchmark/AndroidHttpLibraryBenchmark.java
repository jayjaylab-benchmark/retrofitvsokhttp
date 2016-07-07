package com.jayjaylab.benchmark;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by jjkim on 2016. 7. 7..
 */
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
