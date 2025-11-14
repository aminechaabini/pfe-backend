package com.example.demo.orchestrator.app.service;

import com.example.demo.orchestrator.app.mapper.definition.E2eTestMapper;
import com.example.demo.orchestrator.app.mapper.definition.RestApiTestMapper;
import com.example.demo.orchestrator.app.mapper.definition.SoapApiTestMapper;
import com.example.demo.orchestrator.app.mapper.definition.TestCaseMapper;
import com.example.demo.orchestrator.app.service.exception.EntityNotFoundException;
import com.example.demo.orchestrator.domain.test.TestCase;
import com.example.demo.orchestrator.domain.test.api.RestApiTest;
import com.example.demo.orchestrator.domain.test.api.SoapApiTest;
import com.example.demo.orchestrator.domain.test.assertion.Assertion;
import com.example.demo.orchestrator.domain.test.e2e.E2eStep;
import com.example.demo.orchestrator.domain.test.e2e.E2eTest;
import com.example.demo.orchestrator.domain.test.request.RestRequest;
import com.example.demo.orchestrator.domain.test.request.SoapRequest;
import com.example.demo.orchestrator.persistence.entity.test.*;
import com.example.demo.orchestrator.persistence.repository.TestCaseRepository;
import com.example.demo.orchestrator.persistence.repository.TestSuiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing test cases (MVP version).
 *
 * Responsibilities:
 * - Test case CRUD operations (REST, SOAP, E2E)
 * - Assertion management for API tests
 * - Step management for E2E tests
 *
 * MVP Methods (10 total):
 * - createRestTest, createSoapTest, createE2eTest
 * - getTest, getSuiteTests, deleteTest
 * - addAssertion, removeAssertion
 * - addStep, removeStep
 */
@Service
@Transactional
public class TestService {

    private final TestCaseRepository testCaseRepository;
    private final TestSuiteRepository testSuiteRepository;
    private final RestApiTestMapper restApiTestMapper;
    private final SoapApiTestMapper soapApiTestMapper;
    private final E2eTestMapper e2eTestMapper;
    private final TestCaseMapper testCaseMapper;

    public TestService(
            TestCaseRepository testCaseRepository,
            TestSuiteRepository testSuiteRepository,
            RestApiTestMapper restApiTestMapper,
            SoapApiTestMapper soapApiTestMapper,
            E2eTestMapper e2eTestMapper,
            TestCaseMapper testCaseMapper) {
        this.testCaseRepository = testCaseRepository;
        this.testSuiteRepository = testSuiteRepository;
        this.restApiTestMapper = restApiTestMapper;
        this.soapApiTestMapper = soapApiTestMapper;
        this.e2eTestMapper = e2eTestMapper;
        this.testCaseMapper = testCaseMapper;
    }

    // ========================================================================
    // CREATE TEST OPERATIONS
    // ========================================================================

    /**
     * Create a REST API test.
     *
     * @param suiteId test suite ID
     * @param name test name
     * @param description test description
     * @param request REST request configuration
     * @param assertions list of assertions
     * @return created REST API test
     * @throws EntityNotFoundException if test suite not found
     */
    public RestApiTest createRestTest(
            Long suiteId,
            String name,
            String description,
            RestRequest request,
            List<Assertion> assertions) {

        // Verify suite exists
        if (!testSuiteRepository.existsById(suiteId)) {
            throw new EntityNotFoundException("TestSuite", suiteId);
        }

        // Create domain object
        RestApiTest test = new RestApiTest(name, description);
        test.setRequest(request);
        if (assertions != null) {
            assertions.forEach(test::addAssertion);
        }

        // Convert to entity and save
        RestApiTestEntity entity = restApiTestMapper.toEntity(test);
        entity.setTestSuiteId(suiteId);
        RestApiTestEntity saved = testCaseRepository.save(entity);

        // Convert back to domain
        return restApiTestMapper.toDomain(saved);
    }

    /**
     * Create a SOAP API test.
     *
     * @param suiteId test suite ID
     * @param name test name
     * @param description test description
     * @param request SOAP request configuration
     * @param assertions list of assertions (must be XML-compatible)
     * @return created SOAP API test
     * @throws EntityNotFoundException if test suite not found
     */
    public SoapApiTest createSoapTest(
            Long suiteId,
            String name,
            String description,
            SoapRequest request,
            List<Assertion> assertions) {

        // Verify suite exists
        if (!testSuiteRepository.existsById(suiteId)) {
            throw new EntityNotFoundException("TestSuite", suiteId);
        }

        // Create domain object
        SoapApiTest test = new SoapApiTest(name, description);
        test.setRequest(request);
        if (assertions != null) {
            assertions.forEach(test::addAssertion); // Will validate XML-only
        }

        // Convert to entity and save
        SoapApiTestEntity entity = soapApiTestMapper.toEntity(test);
        entity.setTestSuiteId(suiteId);
        SoapApiTestEntity saved = testCaseRepository.save(entity);

        // Convert back to domain
        return soapApiTestMapper.toDomain(saved);
    }

    /**
     * Create an E2E test.
     *
     * @param suiteId test suite ID
     * @param name test name
     * @param description test description
     * @param steps list of E2E steps
     * @return created E2E test
     * @throws EntityNotFoundException if test suite not found
     */
    public E2eTest createE2eTest(
            Long suiteId,
            String name,
            String description,
            List<E2eStep> steps) {

        // Verify suite exists
        if (!testSuiteRepository.existsById(suiteId)) {
            throw new EntityNotFoundException("TestSuite", suiteId);
        }

        // Create domain object
        E2eTest test = new E2eTest(name, description);
        if (steps != null) {
            steps.forEach(test::addStep);
        }

        // Convert to entity and save
        E2eTestEntity entity = e2eTestMapper.toEntity(test);
        entity.setTestSuiteId(suiteId);
        E2eTestEntity saved = testCaseRepository.save(entity);

        // Convert back to domain
        return e2eTestMapper.toDomain(saved);
    }

    // ========================================================================
    // READ TEST OPERATIONS
    // ========================================================================

    /**
     * Get a test by ID (polymorphic - returns the specific test type).
     *
     * @param testId test ID
     * @return test case (RestApiTest, SoapApiTest, or E2eTest)
     * @throws EntityNotFoundException if test not found
     */
    @Transactional(readOnly = true)
    public TestCase getTest(Long testId) {
        TestCaseEntity entity = testCaseRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("TestCase", testId));

        return testCaseMapper.toDomain(entity);
    }

    /**
     * Get all tests in a suite (mixed types).
     *
     * @param suiteId test suite ID
     * @return list of all tests in the suite
     * @throws EntityNotFoundException if suite not found
     */
    @Transactional(readOnly = true)
    public List<TestCase> getSuiteTests(Long suiteId) {
        // Verify suite exists
        if (!testSuiteRepository.existsById(suiteId)) {
            throw new EntityNotFoundException("TestSuite", suiteId);
        }

        List<TestCaseEntity> entities = testCaseRepository.findByTestSuiteId(suiteId);

        return entities.stream()
                .map(testCaseMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ========================================================================
    // DELETE TEST OPERATIONS
    // ========================================================================

    /**
     * Delete a test.
     *
     * @param testId test ID
     * @throws EntityNotFoundException if test not found
     */
    public void deleteTest(Long testId) {
        TestCaseEntity entity = testCaseRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("TestCase", testId));

        testCaseRepository.delete(entity);
    }

    // ========================================================================
    // ASSERTION MANAGEMENT (API TESTS ONLY)
    // ========================================================================

    /**
     * Add an assertion to an API test (REST or SOAP).
     *
     * @param testId test ID
     * @param assertion assertion to add
     * @throws EntityNotFoundException if test not found
     * @throws IllegalArgumentException if test is not an API test
     */
    public void addAssertion(Long testId, Assertion assertion) {
        TestCaseEntity entity = testCaseRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("TestCase", testId));

        // Only API tests support assertions
        if (!(entity instanceof RestApiTestEntity || entity instanceof SoapApiTestEntity)) {
            throw new IllegalArgumentException("Only API tests support assertions. Test " + testId + " is type: " + entity.getClass().getSimpleName());
        }

        // Convert to domain, add assertion, save back
        TestCase domain = testCaseMapper.toDomain(entity);

        if (domain instanceof RestApiTest restTest) {
            restTest.addAssertion(assertion);
            RestApiTestEntity updated = restApiTestMapper.toEntity(restTest);
            testCaseRepository.save(updated);
        } else if (domain instanceof SoapApiTest soapTest) {
            soapTest.addAssertion(assertion); // Will validate XML-only
            SoapApiTestEntity updated = soapApiTestMapper.toEntity(soapTest);
            testCaseRepository.save(updated);
        }
    }

    /**
     * Remove an assertion from an API test.
     *
     * @param testId test ID
     * @param assertion assertion to remove
     * @throws EntityNotFoundException if test not found
     * @throws IllegalArgumentException if test is not an API test
     */
    public void removeAssertion(Long testId, Assertion assertion) {
        TestCaseEntity entity = testCaseRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("TestCase", testId));

        // Only API tests support assertions
        if (!(entity instanceof RestApiTestEntity || entity instanceof SoapApiTestEntity)) {
            throw new IllegalArgumentException("Only API tests support assertions");
        }

        // Convert to domain, remove assertion, save back
        TestCase domain = testCaseMapper.toDomain(entity);

        if (domain instanceof RestApiTest restTest) {
            restTest.removeAssertion(assertion);
            RestApiTestEntity updated = restApiTestMapper.toEntity(restTest);
            testCaseRepository.save(updated);
        } else if (domain instanceof SoapApiTest soapTest) {
            soapTest.removeAssertion(assertion);
            SoapApiTestEntity updated = soapApiTestMapper.toEntity(soapTest);
            testCaseRepository.save(updated);
        }
    }

    // ========================================================================
    // STEP MANAGEMENT (E2E TESTS ONLY)
    // ========================================================================

    /**
     * Add a step to an E2E test.
     *
     * @param testId E2E test ID
     * @param step step to add
     * @throws EntityNotFoundException if test not found
     * @throws IllegalArgumentException if test is not an E2E test
     */
    public void addStep(Long testId, E2eStep step) {
        TestCaseEntity entity = testCaseRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("TestCase", testId));

        // Only E2E tests support steps
        if (!(entity instanceof E2eTestEntity)) {
            throw new IllegalArgumentException("Only E2E tests support steps. Test " + testId + " is type: " + entity.getClass().getSimpleName());
        }

        // Convert to domain, add step, save back
        E2eTest e2eTest = e2eTestMapper.toDomain((E2eTestEntity) entity);
        e2eTest.addStep(step);
        E2eTestEntity updated = e2eTestMapper.toEntity(e2eTest);
        testCaseRepository.save(updated);
    }

    /**
     * Remove a step from an E2E test by index.
     *
     * @param testId E2E test ID
     * @param stepIndex index of step to remove (0-based)
     * @throws EntityNotFoundException if test not found
     * @throws IllegalArgumentException if test is not an E2E test
     * @throws IndexOutOfBoundsException if step index is invalid
     */
    public void removeStep(Long testId, int stepIndex) {
        TestCaseEntity entity = testCaseRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("TestCase", testId));

        // Only E2E tests support steps
        if (!(entity instanceof E2eTestEntity)) {
            throw new IllegalArgumentException("Only E2E tests support steps");
        }

        // Convert to domain, remove step, save back
        E2eTest e2eTest = e2eTestMapper.toDomain((E2eTestEntity) entity);
        e2eTest.removeStep(stepIndex);
        E2eTestEntity updated = e2eTestMapper.toEntity(e2eTest);
        testCaseRepository.save(updated);
    }
}
