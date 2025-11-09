# Service Layer & MapStruct - Implementation Checklist

**Use this checklist to track your implementation progress.**

---

## Phase 1: Setup & Configuration ⏱️ 2-3 days

### Day 1: Dependencies & Foundation

- [ ] **Add MapStruct dependencies** to `build.gradle.kts`
  - [ ] `mapstruct:1.5.5.Final` (implementation)
  - [ ] `mapstruct-processor:1.5.5.Final` (annotationProcessor)
  - [ ] Configure compiler args: `-Amapstruct.defaultComponentModel=spring`

- [ ] **Create MapStructConfig.java**
  ```
  Location: mapper/config/MapStructConfig.java
  ```
  - [ ] Set `componentModel = SPRING`
  - [ ] Set `unmappedTargetPolicy = ERROR`
  - [ ] Set null handling strategies

- [ ] **Create JsonSerializationMapper.java**
  ```
  Location: mapper/helper/JsonSerializationMapper.java
  ```
  - [ ] Configure ObjectMapper with JavaTimeModule
  - [ ] Implement `toJson(Object)`
  - [ ] Implement `fromJson(String, Class<T>)`
  - [ ] Implement `fromJsonList(String, Class<T>)`
  - [ ] Add error handling with JsonMappingException

- [ ] **Test compilation**
  - [ ] Run `./gradlew build`
  - [ ] Verify no errors
  - [ ] Check `build/generated/sources/annotationProcessor` exists

---

## Phase 2: Simple Mappers ⏱️ 3-4 days

### Day 2-3: Basic Mappers (No Dependencies)

- [ ] **AssertionMapper** (Simplest)
  ```
  Location: mapper/definition/AssertionMapper.java
  Complexity: ⭐☆☆☆☆ (Very Easy)
  ```
  - [ ] Create interface with `@Mapper(config = MapStructConfig.class)`
  - [ ] Add `toEntity(Assertion)` method
  - [ ] Add `toDomain(AssertionEntity)` method
  - [ ] Add list methods
  - [ ] **Write unit test**: `AssertionMapperTest.java`
    - [ ] Test domain → entity
    - [ ] Test entity → domain
    - [ ] Test null handling
    - [ ] Test list mapping

### Day 3-4: Domain Root Mappers

- [ ] **ProjectMapper** (Factory Method + Circular Ref)
  ```
  Location: mapper/definition/ProjectMapper.java
  Complexity: ⭐⭐⭐⭐☆ (Hard)
  ```
  - [ ] Create interface with `@Mapper`
  - [ ] Add `@Mapping(target = "testSuites", ignore = true)` to avoid circular ref
  - [ ] Create `ProjectMapperDecorator.java`
    - [ ] Implement `toDomain()` using `Project.create()` factory method
    - [ ] Copy variables with `forEach(project::setVariable)`
    - [ ] Implement `toEntity()` creating new entity
    - [ ] Implement `updateEntityFromDomain()`
  - [ ] Add `@DecoratedWith(ProjectMapperDecorator.class)` to interface
  - [ ] **Write unit tests**: `ProjectMapperTest.java`
    - [ ] Test factory method usage
    - [ ] Test variables copy
    - [ ] Test ID handling
    - [ ] Test null description

- [ ] **TestSuiteMapper** (Similar to Project)
  ```
  Location: mapper/definition/TestSuiteMapper.java
  Complexity: ⭐⭐⭐⭐☆ (Hard)
  ```
  - [ ] Create interface
  - [ ] Ignore `projects` and `testCases` in mappings
  - [ ] Create `TestSuiteMapperDecorator.java`
    - [ ] Use TestSuite constructor (not factory)
    - [ ] Handle testCases collection
    - [ ] Copy variables
  - [ ] **Write unit tests**

---

## Phase 3: Complex Mappers ⏱️ 4-5 days

### Day 5-6: Polymorphic Test Case Mappers

- [ ] **TestCaseMapper** (Polymorphic)
  ```
  Location: mapper/definition/TestCaseMapper.java
  Complexity: ⭐⭐⭐⭐⭐ (Very Hard)
  ```
  - [ ] Create interface using `ApiTestMapper` and `E2eTestMapper`
  - [ ] Add `@SubclassMapping` for RestApiTest → RestApiTestEntity
  - [ ] Add `@SubclassMapping` for SoapApiTest → SoapApiTestEntity
  - [ ] Add `@SubclassMapping` for E2eTest → E2eTestEntity
  - [ ] Add reverse mappings
  - [ ] **Write unit tests** for each subclass

- [ ] **ApiTestMapper** (JSON Serialization)
  ```
  Location: mapper/definition/ApiTestMapper.java
  Complexity: ⭐⭐⭐⭐☆ (Hard)
  ```
  - [ ] Create interface using `AssertionMapper`
  - [ ] Add methods for RestApiTest
  - [ ] Add methods for SoapApiTest
  - [ ] Create `ApiTestMapperDecorator.java`
    - [ ] Serialize HttpRequest to JSON in `toEntity()`
    - [ ] Deserialize JSON to RestRequest/SoapRequest in `toDomain()`
    - [ ] Handle assertions collection
  - [ ] **Write unit tests**
    - [ ] Test JSON serialization/deserialization
    - [ ] Test with complex HttpRequest objects
    - [ ] Test assertion mapping

### Day 7-8: E2E Mappers

- [ ] **E2eTestMapper**
  ```
  Location: mapper/definition/E2eTestMapper.java
  Complexity: ⭐⭐⭐☆☆ (Medium)
  ```
  - [ ] Create interface using `E2eStepMapper`
  - [ ] Create decorator for steps handling
  - [ ] **Write unit tests**

- [ ] **E2eStepMapper** (Complex JSON)
  ```
  Location: mapper/definition/E2eStepMapper.java
  Complexity: ⭐⭐⭐⭐☆ (Hard)
  ```
  - [ ] Create interface
  - [ ] Create `E2eStepMapperDecorator.java`
    - [ ] Serialize `httpRequest` to JSON
    - [ ] Serialize `extractorItems` to JSON
    - [ ] Map assertions collection
    - [ ] Deserialize with TypeReference for generics
  - [ ] **Write unit tests**
    - [ ] Test HTTP request JSON
    - [ ] Test extractors JSON
    - [ ] Test assertions
    - [ ] Test order preservation

---

## Phase 4: Run Mappers ⏱️ 3-4 days

### Day 9-10: Execution Result Mappers

- [ ] **TestSuiteRunMapper**
  ```
  Location: mapper/run/TestSuiteRunMapper.java
  Complexity: ⭐⭐⭐☆☆ (Medium)
  ```
  - [ ] Create interface
  - [ ] Map TestSuiteRun ↔ TestSuiteRunEntity
  - [ ] Handle testCaseRuns collection
  - [ ] **Write unit tests**

- [ ] **TestCaseRunMapper** (Polymorphic)
  ```
  Location: mapper/run/TestCaseRunMapper.java
  Complexity: ⭐⭐⭐⭐☆ (Hard)
  ```
  - [ ] Add `@SubclassMapping` for ApiTestRun
  - [ ] Add `@SubclassMapping` for E2eTestRun
  - [ ] **Write unit tests**

- [ ] **ApiTestRunMapper**
  ```
  Location: mapper/run/ApiTestRunMapper.java
  ```
  - [ ] Map response data
  - [ ] Map assertion results
  - [ ] **Write unit tests**

- [ ] **E2eTestRunMapper** & **E2eStepRunMapper**
  ```
  Location: mapper/run/
  ```
  - [ ] Similar to definition mappers
  - [ ] Handle extracted values JSON
  - [ ] **Write unit tests**

- [ ] **AssertionResultMapper**
  ```
  Location: mapper/run/AssertionResultMapper.java
  Complexity: ⭐⭐☆☆☆ (Easy)
  ```
  - [ ] Simple field mapping
  - [ ] **Write unit tests**

---

## Phase 5: Service Layer ⏱️ 5-6 days

### Day 11-12: Exception Handling

- [ ] **Create exception hierarchy**
  ```
  Location: service/exception/
  ```
  - [ ] `OrchestratorException.java` (base)
  - [ ] `EntityNotFoundException.java` (404)
  - [ ] `DuplicateEntityException.java` (409)
  - [ ] `ValidationException.java` (400)
  - [ ] `TestExecutionException.java` (500)
  - [ ] `MappingException.java` (500)

- [ ] **Create GlobalExceptionHandler**
  ```
  Location: service/exception/GlobalExceptionHandler.java
  ```
  - [ ] `@RestControllerAdvice`
  - [ ] Handle each exception type
  - [ ] Return appropriate HTTP status
  - [ ] Create `ErrorResponse` record

### Day 13-14: Core Services

- [ ] **ProjectService**
  ```
  Location: service/definition/ProjectService.java
  Complexity: ⭐⭐⭐⭐☆ (Hard - Many methods)
  ```
  - [ ] Inject `ProjectRepository`, `ProjectMapper`
  - [ ] **CRUD Methods**:
    - [ ] `createProject(name, description)` - Check duplicate name
    - [ ] `findById(id)` - Throw exception if not found
    - [ ] `findByIdWithTestSuites(id)` - Use JOIN FETCH query
    - [ ] `findAll()` - Map list to domain
    - [ ] `search(searchTerm)`
    - [ ] `updateProject(id, name, description)` - Use domain methods
    - [ ] `deleteProject(id)` - Check exists
  - [ ] **Relationship Methods**:
    - [ ] `addTestSuite(projectId, testSuiteId)` - Bidirectional sync
    - [ ] `removeTestSuite(projectId, testSuiteId)`
  - [ ] **Variable Methods**:
    - [ ] `setVariable(projectId, name, value)`
    - [ ] `removeVariable(projectId, name)`
  - [ ] Add `@Transactional` to class
  - [ ] Add `@Transactional(readOnly = true)` to read methods
  - [ ] **Write integration tests**: `ProjectServiceIntegrationTest.java`
    - [ ] Test CRUD operations
    - [ ] Test duplicate name validation
    - [ ] Test test suite linking
    - [ ] Test variable management
    - [ ] Test transaction rollback

- [ ] **TestSuiteService**
  ```
  Location: service/definition/TestSuiteService.java
  Complexity: ⭐⭐⭐⭐☆ (Hard)
  ```
  - [ ] Similar structure to ProjectService
  - [ ] `createTestSuite(name, description)`
  - [ ] `findById(id)`
  - [ ] `findByIdWithTestCases(id)` - JOIN FETCH
  - [ ] `updateTestSuite(id, name, description)`
  - [ ] `deleteTestSuite(id)`
  - [ ] `addTestCase(suiteId, testCase)`
  - [ ] `removeTestCase(suiteId, testCaseId)`
  - [ ] `findOrphanedTestSuites()` - Not linked to projects
  - [ ] `setVariable(suiteId, name, value)`
  - [ ] **Write integration tests**

- [ ] **TestCaseService**
  ```
  Location: service/definition/TestCaseService.java
  Complexity: ⭐⭐⭐⭐☆ (Hard - Polymorphic)
  ```
  - [ ] `createRestApiTest(suiteId, name, description, request, assertions)`
  - [ ] `createSoapApiTest(suiteId, name, description, request, assertions)`
  - [ ] `createE2eTest(suiteId, name, description, steps)`
  - [ ] `findById(id)` - Polymorphic mapping
  - [ ] `updateTestCase(id, name, description)`
  - [ ] `deleteTestCase(id)`
  - [ ] `addAssertion(testCaseId, assertion)` - Handle by type
  - [ ] `removeAssertion(testCaseId, assertionId)`
  - [ ] **Write integration tests**
    - [ ] Test each test type creation
    - [ ] Test polymorphic retrieval

### Day 15-16: Execution Services

- [ ] **TestExecutionService**
  ```
  Location: service/execution/TestExecutionService.java
  Complexity: ⭐⭐⭐⭐⭐ (Very Hard - Core logic)
  ```
  - [ ] Inject `TestSuiteService`, `TestRunnerService`, `RunRecordingService`
  - [ ] `executeTestSuite(testSuiteId)`
    - [ ] Load test suite with test cases
    - [ ] Create TestSuiteRun
    - [ ] Execute each test case
    - [ ] Record results
    - [ ] Handle errors gracefully
  - [ ] `executeTestCase(testCaseId)`
  - [ ] `executeE2eTest(e2eTestId)`
  - [ ] **Write integration tests**

- [ ] **TestRunnerService** (HTTP execution logic)
  ```
  Location: service/execution/TestRunnerService.java
  ```
  - [ ] Execute HTTP requests
  - [ ] Evaluate assertions
  - [ ] Extract variables for E2E
  - [ ] **Write integration tests** (use MockServer)

- [ ] **RunRecordingService**
  ```
  Location: service/execution/RunRecordingService.java
  ```
  - [ ] `startTestSuiteRun(testSuite)`
  - [ ] `saveTestSuiteRun(run)`
  - [ ] Map domain run → entity run
  - [ ] **Write unit tests**

---

## Phase 6: Reporting Services ⏱️ 2-3 days

### Day 17-18: Analytics

- [ ] **TestReportingService**
  ```
  Location: service/reporting/TestReportingService.java
  Complexity: ⭐⭐⭐☆☆ (Medium)
  ```
  - [ ] `getTestSuiteStatistics(testSuiteId)`
    - [ ] Success rate
    - [ ] Total runs
    - [ ] Success/failure/cancelled counts
  - [ ] `getRecentRuns(testSuiteId, limit)`
  - [ ] `getFailedTestsSince(since)`
  - [ ] **Write unit tests**

- [ ] **MetricsService**
  ```
  Location: service/reporting/MetricsService.java
  ```
  - [ ] `getSlowestTests(limit)`
  - [ ] `getMostFailingTests(limit)`
  - [ ] `getTestTypeDistribution()`
  - [ ] `getAverageResponseTime()`
  - [ ] **Write unit tests**

- [ ] **AnalyticsService**
  ```
  Location: service/reporting/AnalyticsService.java
  ```
  - [ ] `getSuccessTrend(testSuiteId, days)`
  - [ ] `getExecutionFrequency(testSuiteId)`
  - [ ] `getFlakeyTests()` - Tests with inconsistent results
  - [ ] **Write unit tests**

---

## Phase 7: Integration & Testing ⏱️ 3-4 days

### Day 19-20: End-to-End Tests

- [ ] **E2E Workflow Test**
  ```
  Location: service/E2eWorkflowTest.java
  ```
  - [ ] Create project
  - [ ] Create test suite
  - [ ] Link suite to project
  - [ ] Create REST API test
  - [ ] Execute test suite
  - [ ] Verify results
  - [ ] Check persistence

- [ ] **E2E Test Execution Test**
  - [ ] Create E2E test with multiple steps
  - [ ] Execute test
  - [ ] Verify variable extraction
  - [ ] Verify step execution order
  - [ ] Check all assertions evaluated

- [ ] **Performance Tests**
  - [ ] Test with 100+ test cases
  - [ ] Measure execution time
  - [ ] Check N+1 query issues
  - [ ] Profile database queries

### Day 21-22: Documentation & Cleanup

- [ ] **Service Documentation**
  - [ ] JavaDoc for all public methods
  - [ ] Usage examples
  - [ ] Transaction boundary documentation

- [ ] **Mapper Documentation**
  - [ ] Document complex mappings
  - [ ] Add comments for JSON serialization

- [ ] **Code Review Checklist**
  - [ ] All mappers have unit tests
  - [ ] All services have integration tests
  - [ ] Exception handling is consistent
  - [ ] Transaction boundaries are correct
  - [ ] No N+1 query problems
  - [ ] Null safety everywhere
  - [ ] Logging is appropriate

- [ ] **Cleanup**
  - [ ] Remove unused imports
  - [ ] Format code consistently
  - [ ] Run static analysis (if available)
  - [ ] Update README with architecture

---

## Verification Checklist

### Mapper Verification

For EACH mapper:
- [ ] Compiles without errors
- [ ] Generated code is in `build/generated/sources`
- [ ] Unit test exists and passes
- [ ] Handles null values
- [ ] Maps collections correctly
- [ ] Preserves all data (no loss)
- [ ] Domain validation still works

### Service Verification

For EACH service:
- [ ] `@Transactional` annotation present
- [ ] Read methods use `@Transactional(readOnly = true)`
- [ ] Throws appropriate exceptions
- [ ] Uses mappers correctly
- [ ] Integration test exists and passes
- [ ] Handles edge cases
- [ ] Logs important operations

### Overall Verification

- [ ] Run full test suite: `./gradlew test`
- [ ] All tests pass (aim for >95% coverage)
- [ ] No compilation warnings
- [ ] Build completes: `./gradlew build`
- [ ] No TODO comments in production code
- [ ] All documentation is up to date

---

## Common Issues & Solutions

### Issue: "Cannot find mapper bean"

**Cause**: MapStruct not generating Spring beans

**Solution**:
```kotlin
// In build.gradle.kts
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Amapstruct.defaultComponentModel=spring")
}
```

### Issue: "Unmapped target property"

**Cause**: Field exists in target but not mapped

**Solution**: Add `@Mapping(target = "fieldName", ignore = true)`

### Issue: Circular dependency when injecting mappers

**Cause**: Two mappers depend on each other

**Solution**: Use `@Lazy` or restructure to avoid cycle

### Issue: Transaction rollback not working

**Cause**: Method is not public or `@Transactional` missing

**Solution**: Ensure method is public and annotated

### Issue: Factory method not being used

**Cause**: MapStruct uses default constructor

**Solution**: Use decorator pattern for custom instantiation

---

## Progress Tracking

| Phase | Days | Status | Completion Date |
|-------|------|--------|-----------------|
| Phase 1: Setup | 2-3 | ⬜ Not Started | |
| Phase 2: Simple Mappers | 3-4 | ⬜ Not Started | |
| Phase 3: Complex Mappers | 4-5 | ⬜ Not Started | |
| Phase 4: Run Mappers | 3-4 | ⬜ Not Started | |
| Phase 5: Services | 5-6 | ⬜ Not Started | |
| Phase 6: Reporting | 2-3 | ⬜ Not Started | |
| Phase 7: Testing | 3-4 | ⬜ Not Started | |
| **TOTAL** | **22-29 days** | | |

**Update this table as you complete each phase!**

---

## Files Created Checklist

### Mappers (20 files)

- [ ] `mapper/config/MapStructConfig.java`
- [ ] `mapper/helper/JsonSerializationMapper.java`
- [ ] `mapper/definition/AssertionMapper.java`
- [ ] `mapper/definition/ProjectMapper.java`
- [ ] `mapper/definition/ProjectMapperDecorator.java`
- [ ] `mapper/definition/TestSuiteMapper.java`
- [ ] `mapper/definition/TestSuiteMapperDecorator.java`
- [ ] `mapper/definition/TestCaseMapper.java`
- [ ] `mapper/definition/ApiTestMapper.java`
- [ ] `mapper/definition/ApiTestMapperDecorator.java`
- [ ] `mapper/definition/E2eTestMapper.java`
- [ ] `mapper/definition/E2eTestMapperDecorator.java`
- [ ] `mapper/definition/E2eStepMapper.java`
- [ ] `mapper/definition/E2eStepMapperDecorator.java`
- [ ] `mapper/run/TestSuiteRunMapper.java`
- [ ] `mapper/run/TestCaseRunMapper.java`
- [ ] `mapper/run/ApiTestRunMapper.java`
- [ ] `mapper/run/E2eTestRunMapper.java`
- [ ] `mapper/run/E2eStepRunMapper.java`
- [ ] `mapper/run/AssertionResultMapper.java`

### Services (9+ files)

- [ ] `service/exception/OrchestratorException.java`
- [ ] `service/exception/EntityNotFoundException.java`
- [ ] `service/exception/DuplicateEntityException.java`
- [ ] `service/exception/ValidationException.java`
- [ ] `service/exception/TestExecutionException.java`
- [ ] `service/exception/MappingException.java`
- [ ] `service/exception/GlobalExceptionHandler.java`
- [ ] `service/definition/ProjectService.java`
- [ ] `service/definition/TestSuiteService.java`
- [ ] `service/definition/TestCaseService.java`
- [ ] `service/execution/TestExecutionService.java`
- [ ] `service/execution/TestRunnerService.java`
- [ ] `service/execution/RunRecordingService.java`
- [ ] `service/reporting/TestReportingService.java`
- [ ] `service/reporting/MetricsService.java`
- [ ] `service/reporting/AnalyticsService.java`

### Tests (20+ files)

- [ ] Unit tests for all 20 mappers
- [ ] Integration tests for all 9+ services
- [ ] E2E workflow tests

---

## Success Criteria

✅ **Mappers**:
- All 20 mappers implemented
- 100% mapper test coverage
- Domain objects remain pure (no JPA)
- Bidirectional mapping works correctly

✅ **Services**:
- All 9 services implemented
- Business logic enforced
- Transactions work correctly
- Exception handling is consistent

✅ **Tests**:
- >95% code coverage
- All integration tests pass
- E2E workflow tests pass
- Performance acceptable (<100ms per service call)

✅ **Documentation**:
- All public APIs documented
- Architecture diagrams updated
- README includes service layer info

---

**Start Date**: _____________
**Target Completion**: _____________ (6-9 weeks from start)
**Actual Completion**: _____________

---

*Checklist Version: 1.0*
*Last Updated: 2025-11-09*
*Print this and check off items as you complete them!*
