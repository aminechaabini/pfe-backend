package com.example.demo.orchestrator.app.service;

import com.example.demo.orchestrator.domain.test.RESTAPITest;
import com.example.demo.orchestrator.domain.test.TestSuite;
import com.example.demo.orchestrator.dto.test.CreateRestApiTestRequest;
import com.example.demo.orchestrator.dto.test.UpdateRestApiTestRequest;
import com.example.demo.orchestrator.infra.RESTAPITestRepository;
import com.example.demo.orchestrator.infra.TestSuiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestService {
    private final TestSuiteRepository testSuiteRepository;
    private final RESTAPITestRepository restApiTestRepository;

    public TestService(TestSuiteRepository testSuiteRepository, RESTAPITestRepository restApiTestRepository) {
        this.testSuiteRepository = testSuiteRepository;
        this.restApiTestRepository = restApiTestRepository;
    }

    public RESTAPITest create(CreateRestApiTestRequest request) {
        TestSuite suite = testSuiteRepository.findById(request.suiteId()).get();
        RESTAPITest test = new RESTAPITest(
            suite,
            request.name(),
            request.description(),
            request.httpMethod(),
            request.url(),
            request.headersJson(),
            request.queryJson(),
            request.body(),
            request.assertionsJson()
        );
        return restApiTestRepository.save(test);
    }

    public Optional<RESTAPITest> findById(Long id) {
        return restApiTestRepository.findById(id);
    }

    public List<RESTAPITest> findAll() {
        return restApiTestRepository.findAll();
    }

    public RESTAPITest update(Long id, UpdateRestApiTestRequest request) {
        RESTAPITest test = restApiTestRepository.findById(id).get();

        if (request.name() != null) {
            test.setName(request.name());
        }
        if (request.description() != null) {
            test.setDescription(request.description());
        }
        if (request.httpMethod() != null) {
            test.setHttpMethod(request.httpMethod());
        }
        if (request.url() != null) {
            test.setUrl(request.url());
        }
        if (request.headersJson() != null) {
            test.setHeadersJson(request.headersJson());
        }
        if (request.queryJson() != null) {
            test.setQueryJson(request.queryJson());
        }
        if (request.body() != null) {
            test.setBody(request.body());
        }
        if (request.assertionsJson() != null) {
            test.setAssertionsJson(request.assertionsJson());
        }

        return restApiTestRepository.save(test);
    }

    public boolean delete(Long id) {
        Optional<RESTAPITest> existing = restApiTestRepository.findById(id);
        if (existing.isEmpty()) return false;
        restApiTestRepository.deleteById(id);
        return true;
    }
}
