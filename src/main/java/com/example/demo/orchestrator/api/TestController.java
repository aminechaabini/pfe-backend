package com.example.demo.orchestrator.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.orchestrator.domain.test.RESTAPITest;
import com.example.demo.orchestrator.dto.test.CreateRestApiTestRequest;
import com.example.demo.orchestrator.dto.test.RestApiTestResponse;
import com.example.demo.orchestrator.dto.test.UpdateRestApiTestRequest;
import com.example.demo.orchestrator.app.mapper.test.RestApiTestMapper;
import com.example.demo.orchestrator.app.service.TestService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/projects/{projectId}/suites/{suiteId}/tests")
public class TestController {
    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @PostMapping()
    public ResponseEntity<RestApiTestResponse> create(@RequestBody CreateRestApiTestRequest request) {
        return ResponseEntity.ok(RestApiTestMapper.toResponse(testService.create(request)));
    }

    @GetMapping("/{testId}")
    public ResponseEntity<RestApiTestResponse> findById(@PathVariable("testId") Long testId) {
        Optional<RESTAPITest> test = testService.findById(testId);
        if (test.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(RestApiTestMapper.toResponse(test.get()));
    }

    @GetMapping()
    public ResponseEntity<List<RestApiTestResponse>> findAll() {
        List<RESTAPITest> tests = testService.findAll();
        List<RestApiTestResponse> responses = tests.stream()
                .map(RestApiTestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{testId}")
    public ResponseEntity<RestApiTestResponse> update(@PathVariable("testId") Long testId, @RequestBody UpdateRestApiTestRequest request) {
        return ResponseEntity.ok(RestApiTestMapper.toResponse(testService.update(testId, request)));
    }

    @DeleteMapping("/{testId}")
    public ResponseEntity<Void> delete(@PathVariable("testId") Long testId) {
        if (testService.delete(testId) == false) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(null);
    }
}
