# Persistence Layer - Design Decisions

We need to agree on these critical decisions before implementing entities.

---

## Decision 1: Project ↔ TestSuite Relationship

**Question**: Should TestSuites belong to a single Project (composition) or be shareable across Projects (aggregation)?

### **Option A: Composition (One-to-Many with ownership)**
```java
// ProjectEntity
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
@JoinColumn(name = "project_id")
private List<TestSuiteEntity> testSuites;

// TestSuiteEntity has project_id foreign key
```

**Pros**:
- ✅ Simpler model
- ✅ Maintains strict aggregate boundary
- ✅ Deleting project deletes its test suites
- ✅ Clear ownership

**Cons**:
- ❌ Can't share test suites across projects
- ❌ Can't have "library" of reusable test suites
- ❌ Duplicate test suites if used in multiple projects

**Use Case**: Each project has unique test suites

---

### **Option B: Aggregation (Many-to-Many)** ⭐ **RECOMMENDED**
```java
// ProjectEntity
@ManyToMany
@JoinTable(
    name = "project_test_suites",
    joinColumns = @JoinColumn(name = "project_id"),
    inverseJoinColumns = @JoinColumn(name = "test_suite_id")
)
private List<TestSuiteEntity> testSuites;

// TestSuiteEntity is independent
@ManyToMany(mappedBy = "testSuites")
private List<ProjectEntity> projects;
```

**Pros**:
- ✅ Can share/reuse test suites across projects
- ✅ Create "test suite library"
- ✅ Aligns with AI generation (generate once, use multiple times)
- ✅ More flexible

**Cons**:
- ❌ More complex cascade management
- ❌ Need to handle orphaned test suites
- ❌ Deleting project doesn't delete shared test suites

**Use Case**: Test suites are valuable assets, reusable across projects

---

### **My Recommendation**: Option B (Many-to-Many)

**Rationale**:
- API test suites represent contracts that may apply to multiple projects
- AI-generated test suites are expensive to create, should be reusable
- Users will want "smoke test suite" used across all projects
- More aligned with professional testing tools

**Question for you**: Do you envision users wanting to reuse test suites across projects?

---

## Decision 2: TestSuite ↔ TestCase Relationship

**Question**: Should TestCases belong to a single TestSuite or be shareable?

### **Option A: One-to-Many (Ownership)**
```java
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
private List<TestCaseEntity> testCases;
```

**Recommended**: YES - Test cases are tightly coupled to their suite

---

### **Option B: Many-to-Many**
```java
@ManyToMany
private List<TestCaseEntity> testCases;
```

**Not Recommended**: Test cases are typically not shared across suites

---

### **My Recommendation**: Option A (One-to-Many)

**Rationale**:
- Test cases are part of the test suite's definition
- Sharing individual tests across suites is rare
- Simpler to manage

**Question for you**: Do you agree, or do you see a need to share individual test cases?

---

## Decision 3: TestCase Inheritance Strategy

**Question**: How should we map the TestCase hierarchy (RestApiTest, SoapApiTest, E2eTest)?

### **Option A: SINGLE_TABLE** ⭐ **RECOMMENDED**
```java
@Entity
@Table(name = "test_cases")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "test_type")
public abstract class TestCaseEntity extends BaseEntity {
    // Common fields
}

@Entity
@DiscriminatorValue("REST_API")
public class RestApiTestEntity extends TestCaseEntity {
    // REST-specific fields
}
```

**Database**: Single table with discriminator column
```
test_cases
├── id
├── test_type (REST_API, SOAP_API, E2E)
├── name
├── description
├── request_json (nullable - only for API tests)
├── steps_json (nullable - only for E2E tests)
└── ...
```

**Pros**:
- ✅ Best query performance (no joins)
- ✅ Polymorphic queries are fast
- ✅ Simpler schema
- ✅ Easy to query all test types together

**Cons**:
- ❌ Nullable columns for type-specific fields
- ❌ Less normalized (but acceptable)

---

### **Option B: JOINED (Normalized)**
```java
@Inheritance(strategy = InheritanceType.JOINED)
```

**Database**: Separate tables with joins
```
test_cases (base table)
├── id, name, description

rest_api_tests
├── id (FK to test_cases)
├── request_json

soap_api_tests
├── id (FK to test_cases)
├── request_json

e2e_tests
├── id (FK to test_cases)
└── ...
```

**Pros**:
- ✅ Fully normalized
- ✅ No null columns

**Cons**:
- ❌ Slower queries (requires joins)
- ❌ More complex schema
- ❌ More tables to manage

---

### **My Recommendation**: Option A (SINGLE_TABLE)

**Rationale**:
- Better performance for typical queries ("show all tests in this suite")
- Simpler schema
- Null columns are acceptable tradeoff
- Spring Data JPA handles it well

**Question for you**: Are you okay with nullable columns for better performance?

---

## Decision 4: Variables Storage (Project/TestSuite)

**Question**: Store variables as JSON or in separate table?

### **Option A: JSON** ⭐ **RECOMMENDED**
```java
@Convert(converter = MapToJsonConverter.class)
@Column(columnDefinition = "TEXT")
private Map<String, String> variables;
```

**Stored as**:
```json
{"API_KEY": "abc123", "BASE_URL": "https://api.example.com"}
```

**Pros**:
- ✅ Simpler schema
- ✅ Variables read/written as unit
- ✅ Easy to serialize/deserialize

**Cons**:
- ❌ Can't query by variable name in SQL
- ❌ Can't index variable names

---

### **Option B: Separate Table**
```java
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
private List<ProjectVariableEntity> variables;
```

**Tables**:
```sql
projects (id, name, ...)
project_variables (id, project_id, name, value)
```

**Pros**:
- ✅ Can query by variable name
- ✅ Can index

**Cons**:
- ❌ More complex schema
- ❌ More tables
- ❌ Overkill for simple key-value pairs

---

### **My Recommendation**: Option A (JSON)

**Rationale**:
- Variables are always loaded/saved as a group
- Unlikely to query "find all projects with variable X"
- Simpler code and schema

**Question for you**: Do you need to query/search by variable names?

---

## Decision 5: HTTP Request Storage

**Question**: How should we store HTTP requests (RestRequest, SoapRequest)?

### **Option A: Embedded JSON** ⭐ **RECOMMENDED**
```java
// In RestApiTestEntity
@Column(columnDefinition = "TEXT")
private String requestJson;  // Full RestRequest serialized as JSON
```

**Stored as**:
```json
{
  "method": "POST",
  "url": "https://api.example.com/users",
  "headers": {"Content-Type": "application/json"},
  "queryParams": {"page": "1"},
  "body": {...},
  "auth": {...}
}
```

**Pros**:
- ✅ Very simple
- ✅ Flexible (can change request structure without migrations)
- ✅ Good for requests as units
- ✅ Easier to version

**Cons**:
- ❌ Can't query by URL, method, etc.
- ❌ Can't index headers

---

### **Option B: Fully Normalized (Embeddable)**
```java
@Embedded
private RestRequestEmbeddable request;

@Embeddable
class RestRequestEmbeddable {
    private String method;
    private String url;
    @Convert(converter = HeadersConverter.class)
    private Map<String, List<String>> headers;
    // ...
}
```

**Pros**:
- ✅ Can query by method, URL
- ✅ Can index URL

**Cons**:
- ❌ Still need JSON for complex fields (headers, body, auth)
- ❌ More columns

---

### **Option C: Full Normalization (Separate Tables)**
```java
// request_headers, query_parameters, etc. as separate tables
```

**Pros**:
- ✅ Fully queryable

**Cons**:
- ❌ Way too complex
- ❌ Many tables and joins
- ❌ Overkill for this use case

---

### **My Recommendation**: Option B (Embeddable with JSON)

**Rationale**:
- Can query tests by URL (useful: "show all tests for this API")
- Can filter by HTTP method
- Still use JSON for complex parts (headers, auth)
- Good balance between queryability and simplicity

**Fields in table**:
```sql
test_cases
├── method VARCHAR(10)         -- Queryable
├── url VARCHAR(2000)          -- Queryable
├── headers TEXT (JSON)        -- Not queryable, but okay
├── query_params TEXT (JSON)
├── body_json TEXT (JSON)
├── auth_json TEXT (JSON)
```

**Question for you**: Do you need to search/filter tests by URL or HTTP method?

---

## Decision 6: Assertions Storage

**Question**: Store assertions as JSON or separate table?

### **Option A: Separate Table** ⭐ **RECOMMENDED**
```java
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
private List<AssertionEntity> assertions;
```

**Pros**:
- ✅ Can query assertion results by type
- ✅ Can aggregate (count failed assertions)
- ✅ Useful for reporting

**Cons**:
- ❌ More tables

---

### **Option B: JSON**
```java
@Column(columnDefinition = "TEXT")
private String assertionsJson;
```

**Pros**:
- ✅ Simpler

**Cons**:
- ❌ Can't query results
- ❌ Can't report "most common failing assertion type"

---

### **My Recommendation**: Option A (Separate Table)

**Rationale**:
- Test results/analytics require querying assertions
- "Which assertions fail most often?"
- "Success rate by assertion type"

---

## Decision 7: E2E Steps Storage

**Question**: Store E2E steps inline or separate table?

### **Option A: Separate Table** ⭐ **RECOMMENDED**
```java
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
@OrderColumn(name = "step_order")
private List<E2eStepEntity> steps;
```

**Pros**:
- ✅ Steps can be reused (future feature)
- ✅ Can query individual steps
- ✅ Proper ordering

**Cons**:
- ❌ More tables

---

### **My Recommendation**: Option A (Separate Table)

**Rationale**:
- Steps are first-class entities (now have id, name, description)
- May want to search "find all steps that call this endpoint"
- Easier to implement "step library"

---

## Decision 8: Cascade Strategies

### **Test Definitions**
```java
// User creates/deletes these together
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
```

**Use for**:
- Project → Variables (if separate table)
- TestSuite → TestCases
- TestCase → Assertions
- E2eTest → E2eSteps

---

### **Test Runs (History)**
```java
// Don't cascade delete runs when deleting tests
@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
```

**Use for**:
- TestSuiteRun → TestSuite reference
- TestCaseRun → TestCase reference

---

### **Shared Resources (Many-to-Many)**
```java
// Don't orphan remove shared resources
@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
```

**Use for**:
- Project ↔ TestSuite (if many-to-many)

---

## Summary of Recommendations

| Decision | Recommendation | Rationale |
|----------|---------------|-----------|
| **Project-TestSuite** | Many-to-Many | Reusability, AI generation |
| **TestSuite-TestCase** | One-to-Many | Tight coupling |
| **TestCase Inheritance** | SINGLE_TABLE | Performance |
| **Variables** | JSON | Simple, always loaded as group |
| **HTTP Requests** | Embeddable + JSON | Balance queryability/simplicity |
| **Assertions** | Separate Table | Reporting & analytics |
| **E2E Steps** | Separate Table | First-class entities, reusability |
| **Cascade (Definitions)** | ALL + orphanRemoval | User manages lifecycle |
| **Cascade (Runs)** | PERSIST + MERGE | History independence |

---

## Questions for Discussion

1. **TestSuite Sharing**: Do users need to reuse test suites across projects?
   - If YES → Many-to-Many
   - If NO → One-to-Many

2. **Query by URL/Method**: Do you need to filter/search tests by HTTP endpoint?
   - If YES → Embeddable with queryable fields
   - If NO → Full JSON

3. **Variable Queries**: Do you need to search "projects that have variable X"?
   - If YES → Separate table
   - If NO → JSON

4. **Performance vs Normalization**: Are you okay with nullable columns (SINGLE_TABLE) for better query performance?
   - If YES → SINGLE_TABLE
   - If NO → JOINED

5. **TestCase Reuse**: Should individual test cases be shareable across suites?
   - If YES → Many-to-Many (unusual)
   - If NO → One-to-Many (recommended)

---

## Next Steps

Once we agree on these decisions, I'll:
1. Create entity classes following the agreed patterns
2. Implement repositories
3. Write integration tests
4. Document the schema

**Please review and let me know your preferences!**
