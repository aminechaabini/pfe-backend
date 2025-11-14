# Entity Relationship Diagram (ERD)

Visual representation of the persistence layer entities and their relationships.

---

## Complete ERD

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         TEST DEFINITION LAYER                                │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────────────┐           ┌─────────────────────┐
│  ProjectEntity   │           │ project_test_suites │
├──────────────────┤           │    (Join Table)     │
│ PK: id           │           ├─────────────────────┤
│    name          │◄─────────►│ FK: project_id      │
│    description   │   M:N     │ FK: test_suite_id   │
│    variables(JSON)│           └─────────────────────┘
└──────────────────┘                     ▲
                                         │
                                         │ M:N
                                         │
                                         ▼
                              ┌──────────────────┐
                              │ TestSuiteEntity  │
                              ├──────────────────┤
                              │ PK: id           │
                              │    name          │
                              │    description   │
                              │    variables(JSON)│
                              └──────────────────┘
                                         │
                                         │ 1:N
                                         │ (Owns)
                                         ▼
                              ┌──────────────────────────┐
                              │   TestCaseEntity         │
                              │      (SINGLE_TABLE)      │
                              ├──────────────────────────┤
                              │ PK: id                   │
                              │    test_type (DISC)      │
                              │    name                  │
                              │    description           │
                              │ FK: test_suite_id        │
                              └──────────────────────────┘
                                         △
                ┌────────────────────────┼────────────────────────┐
                │                        │                        │
     ┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐
     │RestApiTestEntity │   │SoapApiTestEntity │   │  E2eTestEntity   │
     ├──────────────────┤   ├──────────────────┤   ├──────────────────┤
     │ DISC: REST_API   │   │ DISC: SOAP_API   │   │ DISC: E2E        │
     │ requestJson(JSON)│   │ requestJson(JSON)│   │                  │
     └──────────────────┘   └──────────────────┘   └──────────────────┘
              │                       │                       │
              │ 1:N                   │ 1:N                   │ 1:N
              │ (Owns)                │ (Owns)                │ (Owns)
              ▼                       ▼                       ▼
     ┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐
     │ AssertionEntity  │   │ AssertionEntity  │   │  E2eStepEntity   │
     ├──────────────────┤   ├──────────────────┤   ├──────────────────┤
     │ PK: id           │   │ PK: id           │   │ PK: id           │
     │    type          │   │    type          │   │    name          │
     │    target        │   │    target        │   │    description   │
     │    expected      │   │    expected      │   │    step_order    │
     │ FK: test_case_id │   │ FK: test_case_id │   │ FK: e2e_test_id  │
     └──────────────────┘   └──────────────────┘   │ httpRequestJson  │
                                                    │ extractorsJson   │
                                                    └──────────────────┘
                                                             │
                                                             │ 1:N
                                                             │ (Owns)
                                                             ▼
                                                    ┌──────────────────┐
                                                    │ AssertionEntity  │
                                                    ├──────────────────┤
                                                    │ PK: id           │
                                                    │    type          │
                                                    │    target        │
                                                    │    expected      │
                                                    │ FK: e2e_step_id  │
                                                    └──────────────────┘


┌─────────────────────────────────────────────────────────────────────────────┐
│                        EXECUTION HISTORY LAYER                               │
└─────────────────────────────────────────────────────────────────────────────┘

                              ┌──────────────────┐
                              │ TestSuiteEntity  │
                              │  (From Above)    │
                              └──────────────────┘
                                         ▲
                                         │ M:1
                                         │ (Reference, No Cascade)
                                         │
                              ┌──────────────────────┐
                              │ TestSuiteRunEntity   │
                              ├──────────────────────┤
                              │ PK: id               │
                              │ FK: test_suite_id    │
                              │    status            │
                              │    result            │
                              │    startedAt         │
                              │    completedAt       │
                              │    errorMessage      │
                              └──────────────────────┘
                                         │
                                         │ 1:N
                                         │ (Owns)
                                         ▼
                              ┌──────────────────────────┐
                              │   TestCaseRunEntity      │
                              │      (SINGLE_TABLE)      │
                              ├──────────────────────────┤
                              │ PK: id                   │
                              │    run_type (DISC)       │
                              │ FK: test_suite_run_id    │
                              │    test_case_id (Ref)    │
                              │    test_case_name        │
                              │    status                │
                              │    result                │
                              │    startedAt             │
                              │    completedAt           │
                              │    errorMessage          │
                              └──────────────────────────┘
                                         △
                                         │
                      ┌──────────────────┴──────────────────┐
                      │                                     │
           ┌──────────────────────┐          ┌──────────────────────┐
           │  ApiTestRunEntity    │          │  E2eTestRunEntity    │
           ├──────────────────────┤          ├──────────────────────┤
           │ DISC: API_TEST       │          │ DISC: E2E_TEST       │
           │ actualStatusCode     │          │                      │
           │ actualResponseBody   │          └──────────────────────┘
           │ actualResponseHeaders│                     │
           │ responseTimeMs       │                     │ 1:N
           └──────────────────────┘                     │ (Owns)
                      │                                 │
                      │ 1:N                             ▼
                      │ (Owns)              ┌──────────────────────┐
                      ▼                     │  E2eStepRunEntity    │
           ┌──────────────────────┐        ├──────────────────────┤
           │ AssertionResultEntity│        │ PK: id               │
           ├──────────────────────┤        │ FK: e2e_test_run_id  │
           │ PK: id               │        │    e2e_step_id (Ref) │
           │ FK: test_case_run_id │        │    step_name         │
           │    assertion_id (Ref)│        │    step_order        │
           │    assertionType     │        │    status            │
           │    target            │        │    result            │
           │    expectedValue     │        │    startedAt         │
           │    actualValue       │        │    completedAt       │
           │    passed            │        │    actualStatusCode  │
           │    errorMessage      │        │    actualResponseBody│
           └──────────────────────┘        │actualResponseHeaders │
                                           │    responseTimeMs    │
                                           │extractedValuesJson   │
                                           │    errorMessage      │
                                           └──────────────────────┘
                                                      │
                                                      │ 1:N
                                                      │ (Owns)
                                                      ▼
                                           ┌──────────────────────┐
                                           │ AssertionResultEntity│
                                           ├──────────────────────┤
                                           │ PK: id               │
                                           │ FK: e2e_step_run_id  │
                                           │    assertion_id (Ref)│
                                           │    assertionType     │
                                           │    target            │
                                           │    expectedValue     │
                                           │    actualValue       │
                                           │    passed            │
                                           │    errorMessage      │
                                           └──────────────────────┘
```

---

## Relationship Types

### Legend
- **PK**: Primary Key
- **FK**: Foreign Key
- **DISC**: Discriminator (for SINGLE_TABLE inheritance)
- **1:N**: One-to-Many relationship
- **M:N**: Many-to-Many relationship
- **△**: Inheritance hierarchy
- **(Ref)**: Reference by ID only (not managed by JPA relationship)
- **(Owns)**: Parent owns children, cascade ALL with orphan removal
- **(JSON)**: Field stored as JSON text

---

## SINGLE_TABLE Inheritance

### TestCaseEntity Hierarchy

```
┌──────────────────────────────────────────────────┐
│            test_cases (Table)                    │
├──────────────────────────────────────────────────┤
│ id  │ test_type │ name │ ... │ request_json │ ... │
├──────────────────────────────────────────────────┤
│ 1   │ REST_API  │ ...  │ ... │ {...}        │     │
│ 2   │ SOAP_API  │ ...  │ ... │ {...}        │     │
│ 3   │ E2E       │ ...  │ ... │ NULL         │     │
└──────────────────────────────────────────────────┘
         │           │           │
         ▼           ▼           ▼
  RestApiTestEntity  SoapApiTestEntity  E2eTestEntity
```

**Discriminator Column**: `test_type` (VARCHAR 20)
**Values**: 'REST_API', 'SOAP_API', 'E2E'

### TestCaseRunEntity Hierarchy

```
┌─────────────────────────────────────────────────────────────┐
│                test_case_runs (Table)                        │
├─────────────────────────────────────────────────────────────┤
│ id │ run_type │ ... │ actual_status_code │ response_time_ms │
├─────────────────────────────────────────────────────────────┤
│ 1  │ API_TEST │ ... │ 200                │ 150              │
│ 2  │ E2E_TEST │ ... │ NULL               │ NULL             │
└─────────────────────────────────────────────────────────────┘
        │              │
        ▼              ▼
  ApiTestRunEntity  E2eTestRunEntity
```

**Discriminator Column**: `run_type` (VARCHAR 20)
**Values**: 'API_TEST', 'E2E_TEST'

---

## Cascade Strategies Visualized

### Test Definitions (Cascade ALL + Orphan Removal)

```
TestSuite ──[ALL]──> TestCase ──[ALL]──> Assertion
   │                    │
   │                    └──[ALL]──> (RestApiTest)
   │                    └──[ALL]──> (SoapApiTest)
   │                    └──[ALL]──> (E2eTest) ──[ALL]──> E2eStep ──[ALL]──> Assertion
   │
   └──[PERSIST,MERGE]──> Project (Many-to-Many, no orphan removal)

Delete TestSuite → Deletes all TestCases, Assertions, E2eSteps
Delete TestCase → Deletes all Assertions (or E2eSteps)
Delete E2eStep → Deletes all Assertions
```

### Execution History (Separate Lifecycle)

```
TestSuite ◄──[NO CASCADE]── TestSuiteRun ──[ALL]──> TestCaseRun ──[ALL]──> AssertionResult
                               │                        │
                               │                        └──[ALL]──> (ApiTestRun)
                               │                        └──[ALL]──> (E2eTestRun) ──[ALL]──> E2eStepRun
                               │
                               └──[NO CASCADE]─► TestSuite (Reference only)

Delete TestSuite → Does NOT delete TestSuiteRuns (history preserved)
Delete TestSuiteRun → Deletes all TestCaseRuns, AssertionResults, E2eStepRuns
```

---

## Join Table Detail

### project_test_suites (Many-to-Many)

```
┌──────────────┬─────────────────┐
│ project_id   │ test_suite_id   │
├──────────────┼─────────────────┤
│ 1            │ 10              │  ← Project 1 uses TestSuite 10
│ 1            │ 11              │  ← Project 1 uses TestSuite 11
│ 2            │ 10              │  ← Project 2 uses TestSuite 10 (shared!)
│ 2            │ 12              │  ← Project 2 uses TestSuite 12
└──────────────┴─────────────────┘

TestSuite 10 is SHARED between Project 1 and Project 2
```

**Primary Key**: Composite (project_id, test_suite_id)
**Foreign Keys**:
- project_id → projects(id)
- test_suite_id → test_suites(id)

---

## Assertion Ownership Patterns

### Pattern 1: API Test Assertions

```
RestApiTestEntity (id=1)
     │
     └──1:N──> AssertionEntity (test_case_id=1, e2e_step_id=NULL)
                    ├─ Assertion 1: STATUS_EQUALS 200
                    ├─ Assertion 2: JSONPATH_EQUALS $.id 123
                    └─ Assertion 3: RESPONSE_TIME_LESS_THAN 1000
```

### Pattern 2: E2E Step Assertions

```
E2eTestEntity (id=2)
     │
     └──1:N──> E2eStepEntity (id=10, e2e_test_id=2)
                    │
                    └──1:N──> AssertionEntity (test_case_id=NULL, e2e_step_id=10)
                                   ├─ Assertion 4: STATUS_EQUALS 201
                                   └─ Assertion 5: JSONPATH_EXISTS $.userId
```

**Rule**: Assertion has EITHER test_case_id OR e2e_step_id populated, never both.

---

## Execution History Flow

### Recording a Test Suite Run

```
1. Create TestSuiteRunEntity
   ├─ Set: testSuite reference
   ├─ Set: status = IN_PROGRESS
   └─ Set: startedAt = now()

2. For each TestCase in TestSuite:
   ├─ Create TestCaseRunEntity (ApiTestRun or E2eTestRun)
   ├─ Execute test
   ├─ Record: actualStatusCode, actualResponseBody, responseTimeMs
   └─ For each Assertion:
       ├─ Evaluate assertion
       └─ Create AssertionResultEntity (passed, actualValue, etc.)

3. Complete TestSuiteRun
   ├─ Set: completedAt = now()
   ├─ Set: status = COMPLETED
   └─ Set: result = (all passed? SUCCESS : FAILURE)

4. Save TestSuiteRunEntity
   └─ Cascades to all TestCaseRuns and AssertionResults
```

---

## Data Flow Example

### Example: E2E Test with Variable Extraction

```
Step 1: POST /register
   ├─ Execute → Response: {"userId": 123}
   ├─ Extract: userId = 123
   └─ Store in E2eStepRunEntity.extractedValuesJson

Step 2: POST /login (uses extracted userId)
   ├─ Read: userId from previous step
   ├─ Execute → POST /login?userId=123
   └─ Store result in E2eStepRunEntity

Complete E2eTestRun
   └─ All steps' results aggregated
```

---

## Indexes Recommendation

### For Performance

```sql
-- Primary lookups
CREATE INDEX idx_projects_name ON projects(name);
CREATE INDEX idx_test_suites_name ON test_suites(name);
CREATE INDEX idx_test_cases_suite_id ON test_cases(test_suite_id);

-- Execution history queries (most important!)
CREATE INDEX idx_test_suite_runs_suite_created
    ON test_suite_runs(test_suite_id, created_at DESC);

CREATE INDEX idx_test_suite_runs_status
    ON test_suite_runs(status);

CREATE INDEX idx_test_case_runs_suite_run
    ON test_case_runs(test_suite_run_id);

CREATE INDEX idx_test_case_runs_case_created
    ON test_case_runs(test_case_id, created_at DESC);

-- E2E lookups
CREATE INDEX idx_e2e_steps_test_id ON e2e_steps(e2e_test_id, step_order);
CREATE INDEX idx_e2e_step_runs_run_id ON e2e_step_runs(e2e_test_run_id, step_order);

-- Assertions
CREATE INDEX idx_assertions_test_case ON assertions(test_case_id);
CREATE INDEX idx_assertions_e2e_step ON assertions(e2e_step_id);
```

---

## Storage Estimates

### Test Definitions (Relatively Small)

| Table | Avg Row Size | 1000 Tests | 10000 Tests |
|-------|--------------|------------|-------------|
| projects | 500 bytes | 50 KB | 500 KB |
| test_suites | 1 KB | 1 MB | 10 MB |
| test_cases | 5 KB | 5 MB | 50 MB |
| e2e_steps | 3 KB | 3 MB | 30 MB |
| assertions | 200 bytes | 200 KB | 2 MB |

### Execution History (Grows Continuously)

| Table | Avg Row Size | 1M Runs | 10M Runs |
|-------|--------------|---------|----------|
| test_suite_runs | 500 bytes | 500 MB | 5 GB |
| test_case_runs | 2 KB | 2 GB | 20 GB |
| e2e_step_runs | 2 KB | 2 GB | 20 GB |
| assertion_results | 300 bytes | 300 MB | 3 GB |

**Recommendation**: Implement data retention policy (delete runs older than 90 days).

---

*ERD Version: 1.0*
*Last Updated: 2025-11-09*
