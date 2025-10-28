package com.example.demo.orchestrator.api;

import com.example.demo.orchestrator.app.service.RunService;
import com.example.demo.orchestrator.dto.run.TriggerRunRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/runs")
public class RunController{

    private final RunService runService;

    public RunController(RunService runService){
        this.runService = runService;
    }
    @PostMapping("/{runId}:trigger")
    public ResponseEntity<String> trigger(@RequestBody TriggerRunRequest request){
        runService.trigger(request);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/{runId}")
    public ResponseEntity<String> findById(@PathVariable("runId") Long runId){
        return ResponseEntity.ok("OK");
    }

    @GetMapping()
    public ResponseEntity<String> findAll(){
        return ResponseEntity.ok("OK");
    }
}