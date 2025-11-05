package com.example.demo.orchestrator.infra;

import com.example.demo.orchestrator.persistence.test.RESTAPITest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RESTAPITestRepository extends JpaRepository<RESTAPITest, Long> {
}
