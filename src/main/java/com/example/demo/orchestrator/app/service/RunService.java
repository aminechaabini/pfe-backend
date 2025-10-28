package com.example.demo.orchestrator.app.service;


import com.example.demo.Runner.Runner;
import com.example.demo.orchestrator.domain.run.Run;
import com.example.demo.orchestrator.dto.run.TriggerRunRequest;
import com.example.demo.orchestrator.infra.RunRepository;
import com.example.demo.orchestrator.mq.RunRequestPublisher;
import com.example.demo.orchestrator.mq.RunRequested;
import com.example.demo.shared.events.AssertionSpec;
import com.example.demo.shared.events.HttpRequest;
import com.example.demo.shared.events.Protocol;
import com.example.demo.shared.events.RunRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RunService {

    private final RunRepository runRepository;
    private final RunRequestPublisher publisher;

    public RunService(RunRepository runRepository, RunRequestPublisher publisher) {
        this.runRepository = runRepository;
        this.publisher = publisher;
    }

    public String trigger(TriggerRunRequest request){
        // save run record to db
        //publisher.publish(new RunRequested("runId ", "pId", "suiteId"));
        Runner r = new Runner();
        r.run(new RunRequest(
                "run_todos_1",
                new HttpRequest(
                        "REST", // matches HttpRequest(String protocol, ...)
                        "GET",
                        "https://jsonplaceholder.typicode.com/todos/1",
                        Map.of("Accept", "application/json"),
                        new byte[0] // byte[] body; empty for GET
                ),
                Protocol.REST, // matches RunRequest field type
                List.of(
                        // Status must be 200
                        new AssertionSpec("statusEquals", "", "200"),

                        // JSONPath checks based on /todos/1 example
                        new AssertionSpec("jsonPathEquals", "$.userId", "1"),
                        new AssertionSpec("jsonPathEquals", "$.id", "1"),
                        new AssertionSpec("jsonPathExists", "$.title", null),
                        new AssertionSpec("jsonPathEquals", "$.completed", "false")
                )));
        return "OK";
    }

    public Optional<Run> findById(Long runId){
        return runRepository.findById(runId);
    }

    public List<Run> findAll(){
        return runRepository.findAll();
    }
}
