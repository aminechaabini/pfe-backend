//package com.example.demo.core.app.service;
//
//import com.example.demo.ai.old.Generator;
//import com.example.demo.core.domain.test.TestCase;
//import org.springframework.stereotype.Service;
//
//@Service
//public class GenerationService {
//
//    private Generator generator;
//    private ContextGatherer contextGatherer;
//
//    private GenerationService(Generator generator){
//        this.generator = generator;
//    }
//
//    public List<TestPreview> generateTestPreviews(Long projectId, Long endpointId){
//        context = contexGatherer.gatherContext(projectId, endpointId);
//        List<TestPreview> previews = Generator.generatePreviews(context);
//        return previews;
//    }
//
//    public TestSuite generateTests(GenerateTestsRequestDTO){
//        context = contexGatherer.gatherContext(projectId, endpointId);
//        TestCase test = Generator.generateTest(context);
//        return test;
//    }
//}
