package com.example.demo.Runner.old_files_for_reference;

import com.example.demo.Runner.assertion_validator.AssertionValidator;
import com.example.demo.Runner.builder.RequestBuilder;
import com.example.demo.Runner.executors.RequestExecutor;
import com.example.demo.shared.events.*;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Runner executes API and E2E tests.
 * Handles HTTP requests, assertions, and variable extraction.
 */
public class Runner {

    private RequestBuilder requestBuilder;
    private RequestExecutor requestExecutor;
    private AssertionValidator assertionValidator;

    public RunResult run(ApiRunRequest apiRunRequest){
        HttpRequest request = requestBuilder.build(apiRunRequest.httpRequest(), apiRunRequest.variables());
        HttpResponse<String> response= requestExecutor.execute(request);
        assertionValidator.validate(apiRunRequest.assertions())


    }
}
