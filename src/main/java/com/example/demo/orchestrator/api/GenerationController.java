package com.example.demo.orchestrator.api;

import com.example.demo.llm_adapter.Generator;
import com.example.demo.orchestrator.app.service.GenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/generations")

public class GenerationController {

    GenerationService generationService;

    public GenerationController(GenerationService generationService){
        this.generationService = generationService;
    }


    @GetMapping("/previewTests")
    public ResponseEntity<TestPreviewsDTO> generateTestPreviews(@PathVariable Long projectId, @PathVariable Long endpointId){
        return ResponseEntity.ok(generationService.generateTestPreviews(projectId, endpointId));
    }

    @GetMapping("/generateTests")
    public ResponseEntity<List<TestsResp>> generateTests(@RequestBody GenerateTestsRequestDTO){
        return ResponseEntity.ok(generationService.generateTests(GenerateTestsRequestDTO));
    }
}
