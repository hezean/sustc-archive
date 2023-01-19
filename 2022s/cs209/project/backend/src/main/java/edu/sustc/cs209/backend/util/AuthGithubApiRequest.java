package edu.sustc.cs209.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.elasticsearch.core.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@EnableAsync
public class AuthGithubApiRequest {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final PoolingHttpClientConnectionManager clientPool = new PoolingHttpClientConnectionManager();
    private static final Lock changeTokLock = new ReentrantLock();

    static {
        clientPool.setMaxTotal(20);
        clientPool.setDefaultMaxPerRoute(2);
    }

    private static final String[] tokens = new String[]{
            "token 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",
            "token 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",
            "token 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",
            "token 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",
            "token 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",
            "token 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",
    };

    private static int lastUsedTok = 0;

    @Async
    public <T> Future<T> get(String url,
                             @Nullable Map<String, String> headers,
                             Class<T> expectType) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet req = new HttpGet(url);
        req.addHeader("Authorization", tokens[lastUsedTok]);

        changeTokLock.lock();
        lastUsedTok++;
        lastUsedTok %= tokens.length;
        changeTokLock.unlock();

        if (headers != null) {
            for (var k : headers.keySet()) {
                req.addHeader(k, headers.get(k));
            }
        }
        T res;
        try (CloseableHttpResponse resp = httpClient.execute(req)) {
            res = mapper.readValue(resp.getEntity().getContent(), expectType);
        }
        return new AsyncResult<>(res);
    }

    @Async
    public Future<byte[]> get(String url,
                              @Nullable Map<String, String> headers) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet req = new HttpGet(url);
        req.addHeader("Authorization", tokens[lastUsedTok]);

        changeTokLock.lock();
        lastUsedTok++;
        lastUsedTok %= tokens.length;
        changeTokLock.unlock();

        if (headers != null) {
            for (var k : headers.keySet()) {
                req.addHeader(k, headers.get(k));
            }
        }
        try (CloseableHttpResponse resp = httpClient.execute(req)) {
            return new AsyncResult<>(resp.getEntity().getContent().readAllBytes());
        }
    }
}
