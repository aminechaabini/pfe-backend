package com.example.demo.orchestrator.infra;

import com.example.demo.orchestrator.domain.run.Run;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunRepository extends JpaRepository<Run, Long> {

}
