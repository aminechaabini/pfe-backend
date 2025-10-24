package com.example.demo.project.infra;

import com.example.demo.project.domain.project.Project;
import com.example.demo.project.domain.test.RESTAPITest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RESTAPITestRepository extends JpaRepository<RESTAPITest, Long> {
}
