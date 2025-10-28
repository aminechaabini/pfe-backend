package com.example.demo.Runner;

import com.example.demo.Runner.strategies.RestRunStrategy;
import com.example.demo.Runner.strategies.RunStrategy;
import com.example.demo.Runner.strategies.SoapRunStrategy;
import com.example.demo.shared.events.AssertionResult;
import com.example.demo.shared.events.Protocol;
import com.example.demo.shared.events.RunRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Runner {

    private final Map<String, RunStrategy> strategies = new HashMap<>();

    public Runner() {
        strategies.put("REST", new RestRunStrategy());
        strategies.put("SOAP", new SoapRunStrategy());
    }

    public void run(RunRequest runRequest) {

        RunStrategy strategy = strategies.get(runRequest.protocol());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported protocol: " + runRequest.protocol());
        }
        strategy.execute(runRequest);

    }
}
