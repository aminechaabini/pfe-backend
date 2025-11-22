package com.example.demo.core.api;

import com.example.demo.core.app.service.TestService;
import com.example.demo.core.domain.test.TestCase;
import com.example.demo.core.domain.test.api.RestApiTest;
import com.example.demo.core.domain.test.api.SoapApiTest;
import com.example.demo.core.domain.test.assertion.Assertion;
import com.example.demo.core.domain.test.e2e.E2eStep;
import com.example.demo.core.domain.test.e2e.E2eTest;
import com.example.demo.core.presentation.rest.dto.CreateE2eTestRequest;
import com.example.demo.core.presentation.rest.dto.CreateRestTestRequest;
import com.example.demo.core.presentation.rest.dto.CreateSoapTestRequest;
import com.example.demo.core.presentation.rest.dto.TestCaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API for Test management (REST, SOAP, E2E).
 *
 * Endpoints:
 * - POST   /api/suites/{suiteId}/tests/rest       - Create REST test
 * - POST   /api/suites/{suiteId}/tests/soap       - Create SOAP test
 * - POST   /api/suites/{suiteId}/tests/e2e        - Create E2E test
 * - GET    /api/tests/{id}                        - Get test by ID
 * - GET    /api/suites/{suiteId}/tests            - List tests in suite
 * - DELETE /api/tests/{id}                        - Delete test
 * - POST   /api/tests/{id}/assertions             - Add assertion
 * - DELETE /api/tests/{id}/assertions             - Remove assertion
 * - POST   /api/tests/{id}/steps                  - Add E2E step
 * - DELETE /api/tests/{id}/steps/{index}          - Remove E2E step
 */
@RestController
@RequestMapping("/api")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    // ========================================================================
    // CREATE TEST ENDPOINTS
    // ========================================================================

    /**
     * Create a REST API test.
     *
     * POST /api/suites/{suiteId}/tests/rest
     */
    @PostMapping("/suites/{suiteId}/tests/rest")
    public ResponseEntity<TestCaseResponse> createRestTest(
            @PathVariable Long suiteId,
            @RequestBody CreateRestTestRequest request) {

        RestApiTest test = testService.createRestTest(
                suiteId,
                request.name(),
                request.description(),
                request.request(),
                request.assertions()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toTestCaseResponse(test));
    }

    /**
     * Create a SOAP API test.
     *
     * POST /api/suites/{suiteId}/tests/soap
     */
    @PostMapping("/suites/{suiteId}/tests/soap")
    public ResponseEntity<TestCaseResponse> createSoapTest(
            @PathVariable Long suiteId,
            @RequestBody CreateSoapTestRequest request) {

        SoapApiTest test = testService.createSoapTest(
                suiteId,
                request.name(),
                request.description(),
                request.request(),
                request.assertions()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toTestCaseResponse(test));
    }

    /**
     * Create an E2E test.
     *
     * POST /api/suites/{suiteId}/tests/e2e
     */
    @PostMapping("/suites/{suiteId}/tests/e2e")
    public ResponseEntity<TestCaseResponse> createE2eTest(
            @PathVariable Long suiteId,
            @RequestBody CreateE2eTestRequest request) {

        E2eTest test = testService.createE2eTest(
                suiteId,
                request.name(),
                request.description(),
                request.steps()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toTestCaseResponse(test));
    }

    // ========================================================================
    // READ TEST ENDPOINTS
    // ========================================================================

    /**
     * Get test by ID (polymorphic - returns specific type).
     *
     * GET /api/tests/{id}
     */
    @GetMapping("/tests/{id}")
    public ResponseEntity<TestCaseResponse> getTest(@PathVariable Long id) {
        TestCase test = testService.getTest(id);
        return ResponseEntity.ok(toTestCaseResponse(test));
    }

    /**
     * Get all tests in a suite.
     *
     * GET /api/suites/{suiteId}/tests
     */
    @GetMapping("/suites/{suiteId}/tests")
    public ResponseEntity<List<TestCaseResponse>> getSuiteTests(@PathVariable Long suiteId) {
        List<TestCase> tests = testService.getSuiteTests(suiteId);
        List<TestCaseResponse> responses = tests.stream()
                .map(this::toTestCaseResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // ========================================================================
    // DELETE TEST ENDPOINTS
    // ========================================================================

    /**
     * Delete a test.
     *
     * DELETE /api/tests/{id}
     */
    @DeleteMapping("/tests/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
        testService.deleteTest(id);
        return ResponseEntity.noContent().build();
    }

    // ========================================================================
    // ASSERTION MANAGEMENT (API TESTS)
    // ========================================================================

    /**
     * Add assertion to API test.
     *
     * POST /api/tests/{id}/assertions
     */
    @PostMapping("/tests/{id}/assertions")
    public ResponseEntity<Void> addAssertion(
            @PathVariable Long id,
            @RequestBody Assertion assertion) {

        testService.addAssertion(id, assertion);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove assertion from API test.
     *
     * DELETE /api/tests/{id}/assertions
     */
    @DeleteMapping("/tests/{id}/assertions")
    public ResponseEntity<Void> removeAssertion(
            @PathVariable Long id,
            @RequestBody Assertion assertion) {

        testService.removeAssertion(id, assertion);
        return ResponseEntity.noContent().build();
    }

    // ========================================================================
    // STEP MANAGEMENT (E2E TESTS)
    // ========================================================================

    /**
     * Add step to E2E test.
     *
     * POST /api/tests/{id}/steps
     */
    @PostMapping("/tests/{id}/steps")
    public ResponseEntity<Void> addStep(
            @PathVariable Long id,
            @RequestBody E2eStep step) {

        testService.addStep(id, step);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove step from E2E test.
     *
     * DELETE /api/tests/{id}/steps/{index}
     */
    @DeleteMapping("/tests/{id}/steps/{index}")
    public ResponseEntity<Void> removeStep(
            @PathVariable Long id,
            @PathVariable int index) {

        testService.removeStep(id, index);
        return ResponseEntity.noContent().build();
    }

    // ========================================================================
    // MAPPERS (Domain -> DTO)
    // ========================================================================

    private TestCaseResponse toTestCaseResponse(TestCase test) {
        String testType = switch (test) {
            case RestApiTest r -> "REST";
            case SoapApiTest s -> "SOAP";
            case E2eTest e -> "E2E";
            default -> "UNKNOWN";
        };

        int assertionCount = 0;
        int stepCount = 0;

        if (test instanceof RestApiTest rest) {
            assertionCount = rest.getAssertionCount();
        } else if (test instanceof SoapApiTest soap) {
            assertionCount = soap.getAssertionCount();
        } else if (test instanceof E2eTest e2e) {
            stepCount = e2e.getStepCount();
        }

        return new TestCaseResponse(
                test.getId(),
                test.getName(),
                test.getDescription(),
                testType,
                assertionCount,
                stepCount,
                test.getCreatedAt(),
                test.getUpdatedAt()
        );
    }
}
