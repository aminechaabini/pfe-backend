package com.example.demo.project.infra;

import com.example.demo.project.domain.run.RunItem;

import java.util.List;
import java.util.Optional;

public interface RunItemRepository {
    RunItem save(RunItem runItem);
    Optional<RunItem> findById(Long id);
    List<RunItem> findAll();
    void deleteById(Long id);
}
