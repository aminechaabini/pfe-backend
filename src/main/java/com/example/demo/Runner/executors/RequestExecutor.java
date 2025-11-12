package com.example.demo.Runner.strategies;

import com.example.demo.shared.events.RunRequest;

public interface RunStrategy {
    void execute(RunRequest request);
}

