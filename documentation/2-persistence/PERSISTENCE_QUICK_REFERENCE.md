# Persistence Layer Quick Reference

Quick lookup guide for entities and repositories.

---

## Entities Overview

### Test Definition Entities

| Entity | Table | Type | Key Features |
|--------|-------|------|--------------|
| **ProjectEntity** | `projects` | Aggregate Root | Many-to-Many with TestSuites, JSON variables |
| **TestSuiteEntity** | `test_suites` | Aggregate Root | Owns TestCases, JSON variables |
| **TestCaseEntity** | `test_cases` | Abstract (SINGLE_TABLE) | Discriminator: REST_API, SOAP_API, E2E |
| **RestApiTestEntity** | `test_cases` | Concrete | JSON request, owns assertions |
| **SoapApiTestEntity** | `test_cases` | Concrete | JSON request, owns assertions |
| **E2eTestEntity** | `test_cases` | Concrete | Owns ordered E2eSteps |
| **E2eStepEntity** | `e2e_steps` | Owned | JSON request, extractors, assertions |
| **AssertionEntity** | `assertions` | Owned | Type, target, expected value |

### Execution Result Entities

| Entity | Table | Type | Key Features |
|--------|-------|------|--------------|
| **TestSuiteRunEntity** | `test_suite_runs` | Aggregate Root | Owns TestCaseRuns, status/result |
| **TestCaseRunEntity** | `test_case_runs` | Abstract (SINGLE_TABLE) | Discriminator: API_TEST, E2E_TEST |
| **ApiTestRunEntity** | `test_case_runs` | Concrete | Response data, response time, owns assertion results |
| **E2eTestRunEntity** | `test_case_runs` | Concrete | Owns ordered E2eStepRuns |
| **E2eStepRunEntity** | `e2e_step_runs` | Owned | Response data, extracted values, assertions |
| **AssertionResultEntity** | `assertion_results` | Owned | Passed/failed, actual vs expected |

---

## Repositories Overview

### ProjectRepository (11 methods)

**Core Operations**
```java
Optional<ProjectEntity> findByName(String name)
List<ProjectEntity> findByNameContainingIgnoreCase(String searchTerm)
boolean existsByName(String name)
```

**With Relationships**
```java
Optional<ProjectEntity> findByIdWithTestSuites(Long id)  // JOIN FETCH
List<ProjectEntity> findByTestSuiteId(Long testSuiteId)
```

**Analytics**
```java
long countProjects()
List<ProjectEntity> search(String searchTerm)
```

---

### TestSuiteRepository (10 methods)

**Core Operations**
```java
Optional<TestSuiteEntity> findByName(String name)
boolean existsByName(String name)
```

**With Relationships**
```java
Optional<TestSuiteEntity> findByIdWithTestCases(Long id)  // JOIN FETCH
List<TestSuiteEntity> findByProjects_Id(Long projectId)
Optional<TestSuiteEntity> findByTestCaseId(Long testCaseId)
```

**Special Queries**
```java
List<TestSuiteEntity> findOrphanedTestSuites()  // Not linked to any project
List<TestSuiteEntity> search(String searchTerm)
long countTestCasesInSuite(Long testSuiteId)
```

---

### TestSuiteRunRepository (15 methods)

**History Queries**
```java
List<TestSuiteRunEntity> findByTestSuiteId(Long testSuiteId)
List<TestSuiteRunEntity> findLatestRunsByTestSuite(Long testSuiteId, Pageable)
Optional<TestSuiteRunEntity> findLatestRunByTestSuite(Long testSuiteId)
```

**Filter by Status/Result**
```java
List<TestSuiteRunEntity> findByStatus(RunStatus status)
List<TestSuiteRunEntity> findByResult(RunResult result)
List<TestSuiteRunEntity> findByResultAndCreatedAtAfter(RunResult, Instant)
```

**Analytics**
```java
List<Object[]> getRunStatistics(Long testSuiteId)
Double getSuccessRate(Long testSuiteId)
Double getAverageDuration(Long testSuiteId)
long countCompletedRuns(Long testSuiteId)
```

**Maintenance**
```java
int deleteByCreatedAtBefore(Instant beforeDate)
List<TestSuiteRunEntity> findStuckRuns(RunStatus, Instant threshold)
```

---

### TestCaseRepository (13 methods)

**Filter by Type**
```java
List<TestCaseEntity> findByType(Class<? extends TestCaseEntity> type)
List<RestApiTestEntity> findAllRestApiTests()
List<SoapApiTestEntity> findAllSoapApiTests()
List<E2eTestEntity> findAllE2eTests()
```

**Search**
```java
List<TestCaseEntity> searchByName(String searchTerm)
List<TestCaseEntity> search(String searchTerm)
```

**Analytics**
```java
long countByType(Class<? extends TestCaseEntity> type)
long countAllTestCases()
List<Object[]> getTestTypeDistribution()  // [Type, Count]
```

**By Suite**
```java
List<TestCaseEntity> findByTestSuiteId(Long testSuiteId)
List<RestApiTestEntity> findRestApiTestsByTestSuiteId(Long testSuiteId)
List<E2eTestEntity> findE2eTestsByTestSuiteId(Long testSuiteId)
```

---

### TestCaseRunRepository (17 methods)

**History**
```java
List<TestCaseRunEntity> findByTestCaseId(Long testCaseId)
List<TestCaseRunEntity> findRecentByTestCaseId(Long testCaseId, Pageable)
```

**Filter by Type**
```java
List<TestCaseRunEntity> findByType(Class<? extends TestCaseRunEntity> type)
List<ApiTestRunEntity> findAllApiTestRuns()
List<E2eTestRunEntity> findAllE2eTestRuns()
```

**Analytics**
```java
Double getSuccessRate(Long testCaseId)
List<Object[]> findSlowestTests(Pageable)              // [Id, Name, AvgDuration]
List<Object[]> findMostFailingTests(Pageable)         // [Id, Name, FailCount]
List<Object[]> getOverallRunStatistics()              // [Result, Count]
```

**Counts**
```java
long countCompletedRuns(Long testCaseId)
long countSuccessfulRuns(Long testCaseId)
long countFailedRuns(Long testCaseId)
```

**Performance**
```java
Double getAverageApiResponseTime()
List<ApiTestRunEntity> findSlowApiTests(Long thresholdMs)
List<Object[]> getAssertionStatistics()                // [Passed, Count]
```

---

## Common Patterns

### Creating Entities

```java
// Create project
ProjectEntity project = new ProjectEntity("Project Name", "Description");
project.getVariables().put("key", "value");
projectRepository.save(project);

// Create test suite
TestSuiteEntity suite = new TestSuiteEntity("Suite Name", "Description");
testSuiteRepository.save(suite);

// Link them
project.addTestSuite(suite);  // Handles bidirectional relationship
projectRepository.save(project);
```

### Loading with Relationships

```java
// Load project with test suites (avoids N+1)
Optional<ProjectEntity> project = projectRepository.findByIdWithTestSuites(id);

// Load test suite with test cases (avoids N+1)
Optional<TestSuiteEntity> suite = testSuiteRepository.findByIdWithTestCases(id);
```

### Recording Execution

```java
// Create run
TestSuiteRunEntity run = new TestSuiteRunEntity(testSuite, RunStatus.IN_PROGRESS);
run.setStartedAt(Instant.now());

// Create test case run
ApiTestRunEntity apiRun = new ApiTestRunEntity(testCaseId, name, RunStatus.IN_PROGRESS);
apiRun.setActualStatusCode(200);
apiRun.setResponseTimeMs(250L);
apiRun.setResult(RunResult.SUCCESS);

// Add to suite run
run.addTestCaseRun(apiRun);

// Complete and save
run.setCompletedAt(Instant.now());
run.setStatus(RunStatus.COMPLETED);
run.setResult(RunResult.SUCCESS);
testSuiteRunRepository.save(run);
```

### Analytics Queries

```java
// Success rate
Double successRate = testSuiteRunRepository.getSuccessRate(testSuiteId);

// Recent failures
Instant lastWeek = Instant.now().minus(7, ChronoUnit.DAYS);
List<TestSuiteRunEntity> failures = testSuiteRunRepository
    .findByResultAndCreatedAtAfter(RunResult.FAILURE, lastWeek);

// Slowest tests
List<Object[]> slow = testCaseRunRepository.findSlowestTests(PageRequest.of(0, 10));
for (Object[] row : slow) {
    Long id = (Long) row[0];
    String name = (String) row[1];
    Double avgDuration = (Double) row[2];
}
```

---

## Enums

### RunStatus
```java
NOT_STARTED     // Run created but not started
IN_PROGRESS     // Currently executing
COMPLETED       // Finished (check result for success/failure)
```

### RunResult
```java
SUCCESS         // All tests passed
FAILURE         // At least one test failed
CANCELLED       // Run was cancelled
```

### AssertionType
```java
STATUS_EQUALS               // HTTP status code
HEADER_EQUALS              // Response header value
BODY_CONTAINS              // Response body contains text
JSONPATH_EQUALS            // JSONPath expression equals value
JSONPATH_EXISTS            // JSONPath expression exists
JSON_SCHEMA_VALID          // JSON schema validation
XPATH_EQUALS               // XPath expression equals value
XPATH_EXISTS               // XPath expression exists
XSD_VALID                  // XML schema validation
RESPONSE_TIME_LESS_THAN    // Response time threshold
REGEX_MATCH                // Regex pattern match
```

---

## Relationships Summary

### Test Definitions
```
Project (Many) ←→ (Many) TestSuite
                     ↓ (One-to-Many)
                  TestCase (Abstract)
                     ├─ RestApiTest → Assertions
                     ├─ SoapApiTest → Assertions
                     └─ E2eTest → E2eSteps → Assertions
```

### Execution Results
```
TestSuiteRun → TestSuite (Many-to-One, no cascade)
   ↓ (One-to-Many)
TestCaseRun (Abstract)
   ├─ ApiTestRun → AssertionResults
   └─ E2eTestRun → E2eStepRuns → AssertionResults
```

---

## JSON Fields

| Entity | Field | Type | Purpose |
|--------|-------|------|---------|
| ProjectEntity | variables | Map<String,String> | Project variables |
| TestSuiteEntity | variables | Map<String,String> | Suite variables |
| RestApiTestEntity | requestJson | String | Full REST request |
| SoapApiTestEntity | requestJson | String | Full SOAP request |
| E2eStepEntity | httpRequestJson | String | HTTP request |
| E2eStepEntity | extractorsJson | String | Data extractors |
| ApiTestRunEntity | actualResponseHeadersJson | String | Response headers |
| E2eStepRunEntity | actualResponseHeadersJson | String | Response headers |
| E2eStepRunEntity | extractedValuesJson | String | Extracted values |

---

## Cascade Strategies

| Relationship | Cascade | Orphan Removal |
|--------------|---------|----------------|
| Project → TestSuite | PERSIST, MERGE | ❌ No (shared) |
| TestSuite → TestCase | ALL | ✅ Yes (owned) |
| TestCase → Assertion | ALL | ✅ Yes (owned) |
| E2eTest → E2eStep | ALL | ✅ Yes (owned) |
| E2eStep → Assertion | ALL | ✅ Yes (owned) |
| TestSuiteRun → TestSuite | None | ❌ No (history) |
| TestSuiteRun → TestCaseRun | ALL | ✅ Yes (owned) |
| TestCaseRun → AssertionResult | ALL | ✅ Yes (owned) |
| E2eTestRun → E2eStepRun | ALL | ✅ Yes (owned) |

---

## Database Tables

| Table | Type | Rows Estimated |
|-------|------|----------------|
| projects | Definition | 10s |
| test_suites | Definition | 100s |
| project_test_suites | Join | 100s |
| test_cases | Definition (SINGLE_TABLE) | 1,000s |
| e2e_steps | Definition | 1,000s |
| assertions | Definition | 10,000s |
| test_suite_runs | History | 100,000s |
| test_case_runs | History (SINGLE_TABLE) | 1,000,000s |
| e2e_step_runs | History | 1,000,000s |
| assertion_results | History | 10,000,000s |

---

## Performance Tips

1. **Use JOIN FETCH for relationships**
   ```java
   findByIdWithTestSuites(id)  // Instead of findById()
   ```

2. **Use pagination for large results**
   ```java
   findLatestRunsByTestSuite(id, PageRequest.of(0, 20))
   ```

3. **Clean up old execution history**
   ```java
   Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
   testSuiteRunRepository.deleteByCreatedAtBefore(thirtyDaysAgo);
   ```

4. **Add indexes in production**
   ```sql
   CREATE INDEX idx_test_suite_runs_suite_created
   ON test_suite_runs(test_suite_id, created_at DESC);
   ```

5. **Use specific queries instead of loading full entities**
   ```java
   getRunStatistics(id)  // Returns Object[] instead of full entities
   ```

---

*Quick Reference Version: 1.0*
*Last Updated: 2025-11-09*
