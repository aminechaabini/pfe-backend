package com.example.demo.orchestrator.infra;

import com.example.demo.orchestrator.domain.test.APITest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface APITestRepository extends JpaRepository<APITest, Long> {
}
