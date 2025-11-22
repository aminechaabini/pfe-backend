package com.example.demo.core.infrastructure.persistence.repository;

import com.example.demo.core.domain.run.TestCaseRun;
import com.example.demo.core.domain.run.TestCaseRunRepository;
import com.example.demo.core.infrastructure.persistence.entity.run.TestCaseRunEntity;
import com.example.demo.core.infrastructure.persistence.mapper.TestCaseRunMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Implementation of TestCaseRunRepository domain interface.
 *
 * Adapter that translates between domain objects and persistence entities.
 */
@Repository
public class TestCaseRunRepositoryAdapter implements TestCaseRunRepository {

    private final com.example.demo.core.infrastructure.persistence.jpa.TestCaseRunRepository jpaRepository;
    private final TestCaseRunMapper mapper;

    public TestCaseRunRepositoryAdapter(
            com.example.demo.core.infrastructure.persistence.jpa.TestCaseRunRepository jpaRepository,
            TestCaseRunMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public TestCaseRun save(TestCaseRun run) {
        TestCaseRunEntity entity;

        if (run.getId() == null) {
            // New run - create entity
            entity = mapper.toEntity(run);
        } else {
            // Existing run - update entity
            entity = jpaRepository.findById(run.getId())
                    .orElseThrow(() -> new IllegalArgumentException("TestCaseRun not found: " + run.getId()));
            mapper.updateEntityFromDomain(entity, run);
        }

        TestCaseRunEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<TestCaseRun> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
