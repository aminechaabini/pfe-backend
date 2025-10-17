package com.example.demo.project.infra;

import com.example.demo.project.domain.run.Run;

import java.util.List;
import java.util.Optional;

public interface RunRepository {
    Run save(Run run);
    Optional<Run> findById(Long id);
    List<Run> findAll();
    void deleteById(Long id);
}
