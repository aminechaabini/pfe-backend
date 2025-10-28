package com.example.demo.orchestrator.api;

import com.example.demo.llm_adapter.Generator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/generations")

public class GenerationController {

    Generator generator;

    public GenerationController(Generator generator){
        this.generator = generator;
    }

    @GetMapping
    public String get(){
        return generator.getResponse();
    }
}
