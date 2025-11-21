package com.example.demo.runner.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages accumulated variables across E2E steps.
 * Immutable-style API for thread safety and clear data flow.
 *
 * Each step creates a new context by merging extracted variables,
 * preventing accidental mutations and making variable flow explicit.
 */
public class VariableContext {

    private final Map<String, String> variables;

    /**
     * Create a new context with initial variables.
     *
     * @param initialVariables starting variables (project + suite level)
     */
    public VariableContext(Map<String, String> initialVariables) {
        this.variables = new HashMap<>(initialVariables);
    }

    /**
     * Get current variables as immutable map.
     *
     * @return copy of current variables
     */
    public Map<String, String> getVariables() {
        return Map.copyOf(variables);
    }

    /**
     * Create new context with additional variables merged in.
     * Existing variables are preserved, new variables override if same key.
     *
     * @param newVariables variables to merge (extracted from step)
     * @return new VariableContext with merged variables
     */
    public VariableContext merge(Map<String, String> newVariables) {
        Map<String, String> merged = new HashMap<>(this.variables);
        merged.putAll(newVariables);
        return new VariableContext(merged);
    }
}
