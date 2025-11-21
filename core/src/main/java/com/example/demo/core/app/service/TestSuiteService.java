package com.example.demo.core.app.service;

import com.example.demo.core.persistence.test.TestSuite;
import com.example.demo.core.dto.suite.CreateTestSuiteRequest;
import com.example.demo.core.dto.suite.UpdateTestSuiteRequest;
import com.example.demo.core.infrastructure.persistence.jpa.TestSuiteRepository;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class TestSuiteService {
    private final TestSuiteRepository testSuiteRepository;

    public TestSuiteService(TestSuiteRepository testSuiteRepository) {

        this.testSuiteRepository = testSuiteRepository;
    }

    public TestSuite create(CreateTestSuiteRequest request) {
        TestSuite testSuite;
        if (request.getDescription() == null) {
            testSuite = TestSuite.create(projectRepository.findById(request.projectId()).get(), request.getName());
        }
        else{
            testSuite = TestSuite.create(projectRepository.findById(request.projectId()).get(), request.getName(), request.getDescription());
        }
        return testSuiteRepository.save(testSuite);


    }

    public Optional<TestSuite> findById (Long id){
        return testSuiteRepository.findById(id);
    }

    public List<TestSuite> findAll(){
        return testSuiteRepository.findAll();
    }

    public TestSuite update(Long id, UpdateTestSuiteRequest request){
        TestSuite testSuite = testSuiteRepository.findById(id).get();

        if (request.name() != null) {
            testSuite.rename(request.name());
        }
        if (request.description() != null) {
            testSuite.changeDescription(request.description());
        }

        return testSuiteRepository.save(testSuite);
    }

    public boolean delete(Long id) {
        Optional<TestSuite> existing = testSuiteRepository.findById(id);
        if (existing.isEmpty()) return false;
        testSuiteRepository.deleteById(id);
        return true;
    }

}
