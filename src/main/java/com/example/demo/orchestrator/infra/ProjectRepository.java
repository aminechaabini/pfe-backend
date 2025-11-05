package com.example.demo.orchestrator.infra;

import com.example.demo.orchestrator.persistence.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Project> findByNameIgnoreCase(String name);
}
