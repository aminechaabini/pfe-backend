package com.example.demo.project.infra;

import com.example.demo.project.domain.project.Project;
import com.example.demo.project.domain.run.Run;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RunRepository extends JpaRepository<Run, Long> {

}
