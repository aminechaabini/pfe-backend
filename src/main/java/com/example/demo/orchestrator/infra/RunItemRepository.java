package com.example.demo.orchestrator.infra;

import com.example.demo.orchestrator.domain.run.RunItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunItemRepository extends JpaRepository<RunItem, Long> {
}
