package com.example.demo.orchestrator.infra;

import com.example.demo.orchestrator.domain.test.TestSuite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestSuiteRepository extends JpaRepository<TestSuite, Long> {
}
