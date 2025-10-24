package com.example.demo.Runner.domain;

public record TestResult(
  boolean passed,
  int statusCode,
  Long latency,
  String failureReason,      // null if passed
  byte[] responseBody
) {}