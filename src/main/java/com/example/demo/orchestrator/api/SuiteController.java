package com.example.demo.orchestrator.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.orchestrator.persistence.test.TestSuite;
import com.example.demo.orchestrator.dto.suite.CreateTestSuiteRequest;
import com.example.demo.orchestrator.dto.suite.TestSuiteResponse;
import com.example.demo.orchestrator.dto.suite.UpdateTestSuiteRequest;
import com.example.demo.orchestrator.app.mapper.suite.SuiteMapper;
import com.example.demo.orchestrator.app.service.TestSuiteService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/projects/{projectId}/suites")
public class SuiteController {
    private final TestSuiteService testSuiteService;

    public SuiteController(TestSuiteService testSuiteService) {
        this.testSuiteService = testSuiteService;
    }

    @PostMapping()
    public ResponseEntity<TestSuiteResponse> create(@RequestBody CreateTestSuiteRequest request) {
        return ResponseEntity.ok(SuiteMapper.toResponse(testSuiteService.create(request)));
    }

    @GetMapping("/{suiteId}")
    public ResponseEntity<TestSuiteResponse> findById(@PathVariable("suiteId") Long suiteId) {
        Optional<TestSuite> suite = testSuiteService.findById(suiteId);
        if (suite.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(SuiteMapper.toResponse(suite.get()));
    }

    @GetMapping()
    public ResponseEntity<List<TestSuiteResponse>> findAll() {
        List<TestSuite> suites = testSuiteService.findAll();
        List<TestSuiteResponse> responses = suites.stream()
                .map(SuiteMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{suiteId}")
    public ResponseEntity<TestSuiteResponse> update(@PathVariable("suiteId") Long suiteId, @RequestBody UpdateTestSuiteRequest request) {
        return ResponseEntity.ok(SuiteMapper.toResponse(testSuiteService.update(suiteId, request)));
    }

    @DeleteMapping("/{suiteId}")
    public ResponseEntity<Void> delete(@PathVariable("suiteId") Long suiteId) {
        if (testSuiteService.delete(suiteId) == false) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(null);
    }

}
