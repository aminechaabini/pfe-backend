package com.example.demo.project.infra;

import com.example.demo.project.domain.test.RESTAPITest;

import java.util.List;
import java.util.Optional;

public interface RESTAPITestRepository {
    RESTAPITest save(RESTAPITest restApiTest);
    Optional<RESTAPITest> findById(Long id);
    List<RESTAPITest> findAll();
    void deleteById(Long id);
}
