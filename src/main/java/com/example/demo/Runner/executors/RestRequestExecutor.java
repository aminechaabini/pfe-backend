package com.example.demo.Runner.strategies;

import com.example.demo.Runner.JsonAssertionEngine;
import com.example.demo.shared.events.AssertionResult;
import com.example.demo.shared.events.RunRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class RestRunStrategy implements RunStrategy{

    public static final java.net.http.HttpClient CLIENT = java.net.http.HttpClient.newHttpClient();
    public void execute(RunRequest runRequest) {

        String[] headers = runRequest.httpRequest().headers().entrySet().stream()
                .flatMap(e -> java.util.stream.Stream.of(e.getKey(), e.getValue()))
                .toArray(String[]::new);

        byte[] body = runRequest.httpRequest().body();

        boolean isNoBodyMethod = switch (runRequest.httpRequest().method().toUpperCase()) {
            case "GET", "DELETE", "HEAD", "OPTIONS", "TRACE" -> true;
            default -> false; // POST, PUT, PATCH, etc.
        };

        boolean hasBody = body != null && body.length > 0;


        HttpRequest.BodyPublisher publisher = isNoBodyMethod
                ? HttpRequest.BodyPublishers.noBody()
                : (hasBody
                ? HttpRequest.BodyPublishers.ofByteArray(body)
                : HttpRequest.BodyPublishers.noBody());


        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(runRequest.httpRequest().url()))
                .headers(headers)
                .method(runRequest.httpRequest().method(), publisher)
                .build();

        HttpResponse<String> getRes = null;
        try {
            getRes = CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(getRes.statusCode());
        System.out.println(getRes.body());

        List<AssertionResult> results =  JsonAssertionEngine.runAll(runRequest.assertions(), getRes.body(), getRes.statusCode());
        System.out.print(results.toString());

    }
}
