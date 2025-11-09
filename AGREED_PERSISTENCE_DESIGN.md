# Agreed Persistence Design Decisions ✅

## Final Configuration

Based on your requirements, here's our agreed design:

| Decision | Choice | Rationale |
|----------|--------|-----------|
| **1. Project ↔ TestSuite** | **Many-to-Many** | Users can reuse test suites across projects |
| **2. Query by Endpoint** | **No - Full JSON** | Don't need to search by URL/method → simpler |
| **3. Performance vs Normalization** | **SINGLE_TABLE** | Nullable columns acceptable for better performance |
| **4. Variable Queries** | **JSON Storage** | Don't need to search by variable names |
| **5. TestSuite ↔ TestCase** | **One-to-Many** | Test cases belong to one suite |
| **6. Assertions** | **Separate Table** | Needed for analytics/reporting |
| **7. E2E Steps** | **Separate Table** | First-class entities with id/name/description |

---

## Entity Relationships

```
Project (Many) ←→ (Many) TestSuite
                            ↓ (One-to-Many)
                         TestCase (Abstract - SINGLE_TABLE)
                            ├── RestApiTestEntity
                            ├── SoapApiTestEntity
                            └── E2eTestEntity
                                   ↓ (One-to-Many)
                                E2eStepEntity
                                   ↓ (One-to-Many)
                             AssertionEntity

TestSuiteRunEntity → TestSuiteEntity (Many-to-One, no cascade delete)
   ↓ (One-to-Many)
TestCaseRunEntity (Abstract - SINGLE_TABLE)
   ├── ApiTestRunEntity
   └── E2eTestRunEntity
         ↓ (One-to-Many)
      E2eStepRunEntity
```

---

## Implementation Details

### **1. ProjectEntity**

```java
@Entity
@Table(name = "projects")
public class ProjectEntity extends BaseEntity {

    @Column(nullable = false, length = 40)
    private String name;

    @Column(length = 2000)
    private String description;

    // Variables stored as JSON
    @Convert(converter = MapToJsonConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, String> variables = new HashMap<>();

    // Many-to-Many with TestSuites (shareable)
    @ManyToMany
    @JoinTable(
        name = "project_test_suites",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "test_suite_id")
    )
    private List<TestSuiteEntity> testSuites = new ArrayList<>();
}
```

**Tables Created**:
- `projects` (id, name, description, variables, created_at, updated_at)
- `project_test_suites` (project_id, test_suite_id) - join table

---

### **2. TestSuiteEntity**

```java
@Entity
@Table(name = "test_suites")
public class TestSuiteEntity extends BaseEntity {

    @Column(nullable = false, length = 40)
    private String name;

    @Column(length = 2000)
    private String description;

    // Variables stored as JSON
    @Convert(converter = MapToJsonConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, String> variables = new HashMap<>();

    // Reverse side of many-to-many
    @ManyToMany(mappedBy = "testSuites")
    private List<ProjectEntity> projects = new ArrayList<>();

    // One-to-Many with TestCases (owned)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "test_suite_id")
    private List<TestCaseEntity> testCases = new ArrayList<>();
}
```

**Table Created**:
- `test_suites` (id, name, description, variables, created_at, updated_at)

---

### **3. TestCaseEntity (SINGLE_TABLE Inheritance)**

```java
@Entity
@Table(name = "test_cases")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "test_type", discriminatorType = DiscriminatorType.STRING)
public abstract class TestCaseEntity extends BaseEntity {

    @Column(nullable = false, length = 40)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "test_suite_id")
    private Long testSuiteId;  // Foreign key
}

@Entity
@DiscriminatorValue("REST_API")
public class RestApiTestEntity extends TestCaseEntity {

    // Full request stored as JSON
    @Column(columnDefinition = "TEXT", name = "request_json")
    private String requestJson;

    // Assertions in separate table
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "test_case_id")
    private List<AssertionEntity> assertions = new ArrayList<>();
}

@Entity
@DiscriminatorValue("SOAP_API")
public class SoapApiTestEntity extends TestCaseEntity {
    // Similar to REST
}

@Entity
@DiscriminatorValue("E2E")
public class E2eTestEntity extends TestCaseEntity {

    // Steps in separate table with ordering
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "e2e_test_id")
    @OrderColumn(name = "step_order")
    private List<E2eStepEntity> steps = new ArrayList<>();
}
```

**Table Created**:
- `test_cases` (id, test_type, name, description, test_suite_id, request_json, created_at, updated_at)
  - Nullable columns: `request_json` (null for E2E tests)
  - `test_type` values: 'REST_API', 'SOAP_API', 'E2E'

---

### **4. E2eStepEntity**

```java
@Entity
@Table(name = "e2e_steps")
public class E2eStepEntity extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "step_order")
    private Integer orderIndex;

    @Column(name = "e2e_test_id")
    private Long e2eTestId;  // Foreign key

    // HTTP request stored as JSON
    @Column(columnDefinition = "TEXT", name = "http_request_json")
    private String httpRequestJson;

    // Assertions in separate table
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "e2e_step_id")
    private List<AssertionEntity> assertions = new ArrayList<>();

    // Extractors stored as JSON (simple)
    @Convert(converter = ObjectToJsonConverter.class)
    @Column(columnDefinition = "TEXT", name = "extractors_json")
    private String extractorsJson;
}
```

**Table Created**:
- `e2e_steps` (id, name, description, step_order, e2e_test_id, http_request_json, extractors_json, created_at, updated_at)

---

### **5. AssertionEntity**

```java
@Entity
@Table(name = "assertions")
public class AssertionEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AssertionType type;

    @Column(nullable = false, length = 500)
    private String target;

    @Column(nullable = false, length = 1000)
    private String expected;

    // Can belong to TestCase or E2eStep
    @Column(name = "test_case_id")
    private Long testCaseId;

    @Column(name = "e2e_step_id")
    private Long e2eStepId;
}
```

**Table Created**:
- `assertions` (id, type, target, expected, test_case_id, e2e_step_id, created_at, updated_at)

---

### **6. Run Entities (Execution History)**

```java
@Entity
@Table(name = "test_suite_runs")
public class TestSuiteRunEntity extends BaseEntity {

    // Reference to test suite (don't cascade delete)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_suite_id", nullable = false)
    private TestSuiteEntity testSuite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RunStatus status;

    @Enumerated(EnumType.STRING)
    private RunResult result;

    private Instant startedAt;
    private Instant completedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "test_suite_run_id")
    private List<TestCaseRunEntity> testCaseRuns = new ArrayList<>();
}

// SINGLE_TABLE inheritance for runs too
@Entity
@Table(name = "test_case_runs")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "run_type")
public abstract class TestCaseRunEntity extends BaseEntity {
    // Similar pattern
}
```

---

## Storage Decisions Summary

| Data | Storage Method | Column Type |
|------|---------------|-------------|
| **Variables** (Project/Suite) | JSON | TEXT |
| **HTTP Request** (full object) | JSON | TEXT |
| **Request Body** | JSON | TEXT |
| **Auth** | JSON | TEXT |
| **Headers** | JSON | TEXT |
| **Query Params** | JSON | TEXT |
| **Extractors** | JSON | TEXT |
| **Assertions** | Separate rows | Individual columns |
| **E2E Steps** | Separate rows | Individual columns |

---

## Cascade Strategies

### **Test Definitions** (User owns lifecycle)
```java
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
```
**Applied to**:
- TestSuite → TestCases
- TestCase → Assertions
- E2eTest → E2eSteps
- E2eStep → Assertions

### **Shared Resources** (Many-to-Many)
```java
@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
```
**Applied to**:
- Project ↔ TestSuite

### **History References** (Don't delete runs with tests)
```java
@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
```
**Applied to**:
- TestSuiteRun → TestSuite
- TestCaseRun → TestCase

---

## Database Schema Overview

### **Core Tables**
1. `projects` - Project definitions
2. `test_suites` - Test suite definitions
3. `project_test_suites` - Join table (many-to-many)
4. `test_cases` - All test types (SINGLE_TABLE with discriminator)
5. `e2e_steps` - E2E test steps
6. `assertions` - Test assertions

### **Execution Tables**
7. `test_suite_runs` - Suite execution history
8. `test_case_runs` - Test execution history (SINGLE_TABLE)
9. `e2e_step_runs` - Step execution results
10. `assertion_results` - Assertion execution results

---

## Next Steps

1. ✅ Design decisions agreed
2. ⏳ Implement entities
3. ⏳ Create repositories
4. ⏳ Write integration tests
5. ⏳ Create domain-to-entity mappers

---

## Benefits of This Design

✅ **Flexibility**: Test suites can be shared and reused
✅ **Performance**: SINGLE_TABLE inheritance = fast queries
✅ **Simplicity**: JSON storage for complex objects
✅ **Analytics**: Assertions in separate table for reporting
✅ **Clean Separation**: Test definitions vs execution history
✅ **Scalability**: Can add new test types easily (discriminator pattern)

Let's start implementing! 🚀
