# Persistence Layer Architecture Review

As a senior developer, I'll provide a comprehensive analysis of the persistence layer design for your AI-powered API testing tool.

## 1. Entity Analysis Based on Domain Model

Let me first map out what we have from the domain model:

```
Domain Model Hierarchy:
├── Project (Aggregate Root)
│   ├── TestSuites (Collection)
│   └── Variables (Map)
├── TestSuite (Aggregate Root)
│   ├── TestCases (Collection)
│   └── Variables (Map)
├── TestCase (Abstract)
│   ├── ApiTest (Abstract)
│   │   ├── RestApiTest (Concrete)
│   │   └── SoapApiTest (Concrete)
│   └── E2eTest (Concrete)
│       └── E2eSteps (Collection)
├── E2eStep
│   ├── HttpRequest
│   ├── Assertions (Collection)
│   └── ExtractorItems (Collection)
├── HttpRequest Hierarchy
│   ├── RestRequest
│   └── SoapRequest
└── Run Hierarchy (Execution Results)
    ├── TestSuiteRun
    ├── TestCaseRun (Abstract)
    │   ├── ApiTestRun
    │   └── E2eTestRun
    └── E2eStepRun
```

### **Entities to Define**

Based on the domain model, here are the persistence entities we need:

#### **Core Test Definition Entities** (What to test)
1. **ProjectEntity** - Top-level container
2. **TestSuiteEntity** - Groups related tests
3. **TestCaseEntity** - Abstract/discriminator for test types
   - **RestApiTestEntity**
   - **SoapApiTestEntity**
   - **E2eTestEntity**
4. **E2eStepEntity** - Individual steps in E2E tests
5. **HttpRequestEntity** - Polymorphic request storage
6. **AssertionEntity** - Test assertions (embedded or separate table)
7. **ExtractorItemEntity** - Data extractors for E2E tests

#### **Execution/Run Entities** (Test results)
8. **TestSuiteRunEntity**
9. **TestCaseRunEntity** - Abstract/discriminator
   - **ApiTestRunEntity**
   - **E2eTestRunEntity**
10. **E2eStepRunEntity**
11. **AssertionResultEntity** - Assertion execution results

#### **Supporting/Value Object Entities**
12. **ProjectVariableEntity** - Project-level variables
13. **TestSuiteVariableEntity** - Suite-level variables
14. **RequestHeaderEntity** - HTTP headers
15. **QueryParameterEntity** - Query parameters for REST

---

## 2. Repository Design

Following the **Repository Pattern** and respecting **Aggregate Boundaries**:

### **Primary Repositories (Aggregate Roots)**

```java
// For test definitions
ProjectRepository
TestSuiteRepository

// For test execution history
TestSuiteRunRepository
```

### **Query/Lookup Repositories** (Read-only, for search)

```java
TestCaseRepository      // Search tests across projects
E2eStepRepository       // Find reusable steps
RunHistoryRepository    // Query execution history, metrics
```

### **Why This Design?**

- **ProjectRepository**: Manages entire project aggregate (includes test suites)
- **TestSuiteRepository**: Manages test suite aggregate (includes test cases)
  - Even though Project contains TestSuites, TestSuite can exist independently
  - Users may want to share test suites across projects
- **TestSuiteRunRepository**: Manages execution results
  - Separates test definitions from results
  - Enables efficient queries for execution history

### **What NOT to have repositories for:**
- ❌ `E2eStepRepository` (accessed through TestCase)
- ❌ `AssertionRepository` (value objects)
- ❌ `HttpRequestRepository` (embedded in TestCase)

---

## 3. Database Choice: SQLite vs Alternatives

You asked about SQLite. Let me provide a detailed comparison:

### **Option 1: SQLite**
```
✅ Pros:
- Zero configuration
- Single file database (easy backup)
- Lightweight (~600KB)
- Widely used, battle-tested
- Good for read-heavy workloads
- Cross-platform

❌ Cons:
- Write concurrency limited (entire DB locks on write)
- No stored procedures
- Limited ALTER TABLE support
- Weaker type system
- Native library required (JNI wrapper needed for Java)
```

### **Option 2: H2 Database** ⭐ **RECOMMENDED**
```
✅ Pros:
- Pure Java (no native dependencies)
- Better concurrency than SQLite (MVCC)
- Multiple modes: embedded, in-memory, server
- PostgreSQL compatibility mode
- Built-in web console for debugging
- Excellent JPA/Hibernate support
- Can transition to server mode later
- Faster than SQLite for most operations
- Better for testing (in-memory mode)

❌ Cons:
- Slightly larger footprint (~2MB jar)
- Less known than SQLite outside Java ecosystem
```

### **Option 3: HSQLDB**
```
✅ Pros:
- Pure Java
- Lightweight
- Embedded mode

❌ Cons:
- Less active development than H2
- Smaller community
- Fewer features than H2
```

### **Option 4: Apache Derby**
```
✅ Pros:
- Apache project (stable)
- Pure Java
- Embedded mode

❌ Cons:
- Larger footprint
- Slower than H2
- Verbose configuration
```

---

## **🎯 My Recommendation: H2 Database**

### **Why H2 for your desktop Java application:**

1. **Pure Java Integration**
   - No native libraries → easier Electron packaging
   - Works identically on Windows, Mac, Linux
   - No JNI overhead or platform-specific issues

2. **Development Workflow**
   ```
   Development: In-memory H2 (fast tests)
        ↓
   Desktop App: File-based H2 (user data)
        ↓
   Future?: Server mode or migrate to PostgreSQL
   ```

3. **Built-in Tools**
   - H2 Console: `http://localhost:8082` for debugging
   - Can inspect DB while app is running

4. **Performance**
   - MVCC (Multi-Version Concurrency Control) → better for concurrent reads/writes
   - Your AI generation features may write while user browses

5. **JPA/Hibernate Support**
   - First-class support (it's used in Spring Boot tutorials)
   - Fewer quirks than SQLite with JPA

6. **PostgreSQL Compatibility Mode**
   - If you ever need to scale to server deployment
   - Easier migration path

### **Configuration Example**

```properties
# application.properties for H2

# File-based mode (production)
spring.datasource.url=jdbc:h2:file:./data/testorchestrator
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Enable H2 console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# In-memory mode (for tests)
# spring.datasource.url=jdbc:h2:mem:testdb
```

---

## 4. Implementation Plan

Here's a comprehensive, phased approach:

### **Phase 1: Infrastructure Setup** (2-3 days)

#### **1.1 Configure H2 Database**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

```java
// application.properties
spring.datasource.url=jdbc:h2:file:./data/testorchestrator
spring.jpa.hibernate.ddl-auto=update  // Start with update, move to Flyway later
spring.jpa.show-sql=true  // For development
```

#### **1.2 Define Base Entity Classes**
```java
// Base entity for all persistent objects
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // getters/setters
}
```

#### **1.3 Set Up JPA Converters for Value Objects**
```java
// For storing complex types as JSON
@Converter
public class MapToJsonConverter implements AttributeConverter<Map<String, String>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting map to JSON", e);
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            return new HashMap<>();
        }
    }
}
```

---

### **Phase 2: Entity Mapping** (5-7 days)

#### **2.1 Project & TestSuite Entities**

**Key Decision: Mapping Strategy**
```java
@Entity
@Table(name = "projects")
public class ProjectEntity extends BaseEntity {

    @Column(nullable = false, length = 40)
    private String name;

    @Column(length = 2000)
    private String description;

    // Option A: Store as JSON (simpler)
    @Convert(converter = MapToJsonConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, String> variables;

    // Option B: Separate table (more queryable)
    // @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<ProjectVariableEntity> variables;

    // Relationship to TestSuites
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_id")
    private List<TestSuiteEntity> testSuites = new ArrayList<>();
}
```

**Important Consideration:** Based on your aggregate boundary design, should TestSuites be **owned by Project or independent**?

**Two Approaches:**

**Approach A: TestSuites are owned by Projects (Composition)**
```java
// In ProjectEntity
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
private List<TestSuiteEntity> testSuites;

// Pros: Simple, maintains aggregate boundary
// Cons: Can't share test suites across projects
```

**Approach B: TestSuites are independent (Aggregation)** ⭐ **RECOMMENDED**
```java
// In ProjectEntity
@ManyToMany
@JoinTable(
    name = "project_test_suites",
    joinColumns = @JoinColumn(name = "project_id"),
    inverseJoinColumns = @JoinColumn(name = "test_suite_id")
)
private List<TestSuiteEntity> testSuites;

// Pros: Can share/reuse test suites
// Cons: More complex cascade management
```

**My Recommendation:** Use **Approach B** because:
- Test suites are valuable assets (API contracts, regression tests)
- Users will want to reuse test suites across projects
- Aligns with your AI generation feature (generate suite once, use multiple times)

#### **2.2 TestCase Hierarchy - Inheritance Mapping**

**Key Decision: Inheritance Strategy**

**Option 1: SINGLE_TABLE** (Recommended for TestCases)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_suite_id")
    private TestSuiteEntity testSuite;
}

@Entity
@DiscriminatorValue("REST_API")
public class RestApiTestEntity extends TestCaseEntity {

    @Embedded
    private RestRequestEmbeddable request;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "test_case_id")
    private List<AssertionEntity> assertions;
}

@Entity
@DiscriminatorValue("SOAP_API")
public class SoapApiTestEntity extends TestCaseEntity {
    // Similar structure
}

@Entity
@DiscriminatorValue("E2E")
public class E2eTestEntity extends TestCaseEntity {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "e2e_test_id")
    @OrderColumn(name = "step_order")
    private List<E2eStepEntity> steps;
}
```

**Why SINGLE_TABLE?**
- ✅ Best query performance (no joins needed)
- ✅ Polymorphic queries are fast
- ✅ Simpler schema
- ❌ Null columns for unused fields (acceptable tradeoff)

**Alternative: JOINED strategy** (if you want normalized schema)
```java
@Inheritance(strategy = InheritanceType.JOINED)
// Creates separate tables: test_cases, rest_api_tests, soap_api_tests, e2e_tests
// More normalized but slower queries
```

#### **2.3 Request Entities - Embedded vs Separate Tables**

**Key Decision:** Store HTTP requests as JSON or normalize?

**Option A: Store as JSON** ⭐ **RECOMMENDED**
```java
@Embeddable
public class RestRequestEmbeddable {

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private HttpMethod method;

    @Column(nullable = false, length = 2000)
    private String url;

    // Store complex objects as JSON
    @Convert(converter = HeadersConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, List<String>> headers;

    @Convert(converter = QueryParamsConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, String> queryParams;

    @Column(columnDefinition = "TEXT")
    private String bodyJson;  // Serialized Body object

    @Column(length = 100)
    private String contentType;

    // Auth stored as JSON
    @Convert(converter = AuthConverter.class)
    @Column(columnDefinition = "TEXT")
    private Auth auth;
}
```

**Why JSON storage for requests?**
- ✅ Simpler schema
- ✅ Flexible (can evolve request structure without migrations)
- ✅ Good for your use case (requests are read/written as units)
- ✅ H2 supports JSON functions if you need to query
- ❌ Can't easily query by header names (but you probably won't)

**Option B: Fully Normalized** (if you need to query request details)
```java
@Entity
public class RequestHeaderEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String value;

    @ManyToOne
    private HttpRequestEntity request;
}
// More tables, complex queries, probably unnecessary
```

#### **2.4 Run Entities (Execution History)**

**Important:** Keep Run entities **completely separate** from Test entities

```java
@Entity
@Table(name = "test_suite_runs")
public class TestSuiteRunEntity extends BaseEntity {

    // Reference to test suite (NOT cascade)
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
    private List<TestCaseRunEntity> testCaseRuns;

    // Index for performance
    @Index(columnList = "testSuite_id, createdAt DESC")
}
```

**Why separate Run entities?**
- Test definitions change independently of execution history
- Can delete old runs without affecting tests
- Can query run history efficiently
- Aligns with CQRS pattern (Command/Query Separation)

---

### **Phase 3: Repository Implementation** (3-4 days)

#### **3.1 Base Repository Interface**
```java
// Extends Spring Data JPA
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    // Custom query methods
    Optional<ProjectEntity> findByName(String name);

    List<ProjectEntity> findByNameContainingIgnoreCase(String searchTerm);

    // Custom query with joins
    @Query("SELECT p FROM ProjectEntity p LEFT JOIN FETCH p.testSuites WHERE p.id = :id")
    Optional<ProjectEntity> findByIdWithTestSuites(@Param("id") Long id);

    // Count active projects
    @Query("SELECT COUNT(p) FROM ProjectEntity p")
    long countProjects();
}
```

#### **3.2 TestSuite Repository**
```java
public interface TestSuiteRepository extends JpaRepository<TestSuiteEntity, Long> {

    Optional<TestSuiteEntity> findByName(String name);

    // Find by project
    List<TestSuiteEntity> findByProjects_Id(Long projectId);

    // Fetch with test cases
    @Query("SELECT ts FROM TestSuiteEntity ts LEFT JOIN FETCH ts.testCases WHERE ts.id = :id")
    Optional<TestSuiteEntity> findByIdWithTestCases(@Param("id") Long id);

    // Search
    @Query("SELECT ts FROM TestSuiteEntity ts WHERE " +
           "LOWER(ts.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ts.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<TestSuiteEntity> search(@Param("search") String searchTerm);
}
```

#### **3.3 TestCase Query Repository**
```java
public interface TestCaseRepository extends JpaRepository<TestCaseEntity, Long> {

    // Find by type
    @Query("SELECT tc FROM TestCaseEntity tc WHERE TYPE(tc) = :type")
    List<TestCaseEntity> findByType(@Param("type") Class<? extends TestCaseEntity> type);

    // Find REST API tests
    default List<RestApiTestEntity> findAllRestApiTests() {
        return findByType(RestApiTestEntity.class)
            .stream()
            .map(tc -> (RestApiTestEntity) tc)
            .collect(Collectors.toList());
    }

    // Find tests by URL pattern (if requests stored as JSON)
    @Query(value = "SELECT * FROM test_cases WHERE test_type = 'REST_API' " +
                   "AND JSON_EXTRACT(request_json, '$.url') LIKE :urlPattern",
           nativeQuery = true)
    List<TestCaseEntity> findByUrlPattern(@Param("urlPattern") String urlPattern);
}
```

#### **3.4 Run History Repository**
```java
public interface TestSuiteRunRepository extends JpaRepository<TestSuiteRunEntity, Long> {

    // Find latest runs for a test suite
    @Query("SELECT r FROM TestSuiteRunEntity r WHERE r.testSuite.id = :testSuiteId " +
           "ORDER BY r.createdAt DESC")
    List<TestSuiteRunEntity> findLatestRunsByTestSuite(@Param("testSuiteId") Long testSuiteId,
                                                         Pageable pageable);

    // Statistics
    @Query("SELECT r.result, COUNT(r) FROM TestSuiteRunEntity r " +
           "WHERE r.testSuite.id = :testSuiteId GROUP BY r.result")
    List<Object[]> getRunStatistics(@Param("testSuiteId") Long testSuiteId);

    // Find failed runs
    List<TestSuiteRunEntity> findByResultAndCreatedAtAfter(RunResult result, Instant since);
}
```

---

### **Phase 4: Domain-Persistence Mapping** (2-3 days)

You'll need **mappers** to convert between domain and persistence models:

```java
@Component
public class ProjectMapper {

    public ProjectEntity toEntity(Project domain) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setVariables(new HashMap<>(domain.getVariables()));
        // Map test suites...
        return entity;
    }

    public Project toDomain(ProjectEntity entity) {
        Project domain = Project.create(entity.getName(), entity.getDescription());
        domain.setId(entity.getId());
        // Set variables, test suites...
        return domain;
    }
}
```

**Tools to consider:**
- **MapStruct** (compile-time generation, fast)
- **ModelMapper** (runtime reflection, flexible)
- **Manual mappers** (full control, more code)

I recommend **MapStruct** for performance and type safety.

---

### **Phase 5: Schema Migration Strategy** (1-2 days)

**Don't rely on `hibernate.ddl-auto=update` in production!**

Use **Flyway** or **Liquibase** for migrations:

**Flyway Setup:**
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

```sql
-- V1__Initial_schema.sql
CREATE TABLE projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    description VARCHAR(2000),
    variables TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE test_suites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    description VARCHAR(2000),
    type VARCHAR(50),
    variables TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE project_test_suites (
    project_id BIGINT NOT NULL,
    test_suite_id BIGINT NOT NULL,
    PRIMARY KEY (project_id, test_suite_id),
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (test_suite_id) REFERENCES test_suites(id)
);

-- etc...
```

---

### **Phase 6: Testing Strategy** (3-4 days)

#### **6.1 Repository Tests**
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void shouldSaveAndRetrieveProject() {
        ProjectEntity project = new ProjectEntity();
        project.setName("Test Project");
        project.setDescription("Description");

        ProjectEntity saved = projectRepository.save(project);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindByName() {
        // Create test data
        ProjectEntity project = new ProjectEntity();
        project.setName("Unique Name");
        projectRepository.save(project);

        // Test query
        Optional<ProjectEntity> found = projectRepository.findByName("Unique Name");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Unique Name");
    }
}
```

#### **6.2 Integration Tests**
```java
@SpringBootTest
@Transactional
class ProjectServiceIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Test
    void shouldCreateProjectWithTestSuites() {
        Project domain = Project.create("My Project", "Description");
        TestSuite suite = new TestSuite("My Suite", "Suite description");
        domain.addSuite(suite);

        Project saved = projectService.save(domain);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTestSuites()).hasSize(1);
    }
}
```

---

## 5. Critical Design Decisions

### **Decision 1: JSON vs Normalized for Complex Objects**

| Object | Recommendation | Reason |
|--------|---------------|--------|
| Variables (Map<String, String>) | JSON | Rarely queried individually |
| HTTP Headers | JSON | Read/written as unit |
| Query Parameters | JSON | Part of request |
| Assertions | **Separate table** | Need to query results by assertion type |
| E2E Steps | **Separate table** | Need ordering, reusability |
| Request Body | JSON | Complex, polymorphic |

### **Decision 2: Cascade Strategies**

```java
// Test definitions (user creates/deletes)
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)

// Test runs (history, don't cascade delete)
@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})

// Shared test suites (many-to-many, don't orphan remove)
@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
```

### **Decision 3: Fetch Strategies**

```java
// Default: LAZY (avoid N+1 queries)
@OneToMany(fetch = FetchType.LAZY)

// Use JOIN FETCH in queries when needed
@Query("SELECT p FROM ProjectEntity p LEFT JOIN FETCH p.testSuites WHERE p.id = :id")
```

---

## 6. Database File Location Strategy

For a desktop app:

```java
// Windows: C:\Users\{username}\AppData\Local\TestOrchestrator\data
// Mac: ~/Library/Application Support/TestOrchestrator/data
// Linux: ~/.local/share/TestOrchestrator/data

public class DatabaseConfig {
    public static String getDatabasePath() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return System.getenv("LOCALAPPDATA") + "/TestOrchestrator/data";
        } else if (os.contains("mac")) {
            return userHome + "/Library/Application Support/TestOrchestrator/data";
        } else {
            return userHome + "/.local/share/TestOrchestrator/data";
        }
    }
}
```

---

## 7. Implementation Timeline

| Phase | Tasks | Duration | Priority |
|-------|-------|----------|----------|
| **Phase 1** | H2 setup, base entities, converters | 2-3 days | CRITICAL |
| **Phase 2** | Entity mapping (all entities) | 5-7 days | CRITICAL |
| **Phase 3** | Repository implementation | 3-4 days | CRITICAL |
| **Phase 4** | Domain-persistence mappers | 2-3 days | HIGH |
| **Phase 5** | Flyway migrations | 1-2 days | HIGH |
| **Phase 6** | Testing | 3-4 days | HIGH |
| **Total** | **16-23 days** | ~3-4 weeks | |

---

## 8. Recommended Next Steps

1. **Start with Phase 1** - Get H2 configured and verify it works
2. **Create a spike** - Map 1 entity (Project) end-to-end to validate approach
3. **Review schema design** - Discuss JSON vs normalized decisions
4. **Implement incrementally** - Start with Project → TestSuite → TestCase
5. **Test continuously** - Write tests as you build entities

---

## 9. Key Takeaways

### **Database Choice: H2**
- Pure Java, no native dependencies
- Better concurrency than SQLite
- Built-in console for debugging
- Easy migration path to PostgreSQL

### **Entity Design**
- Use SINGLE_TABLE inheritance for TestCase hierarchy
- Store complex objects (requests, headers) as JSON
- Keep Run entities separate from Test entities
- Make TestSuites shareable across projects (many-to-many)

### **Repository Pattern**
- Repository per aggregate root (Project, TestSuite, TestSuiteRun)
- Query repositories for cross-cutting concerns
- Use JOIN FETCH to avoid N+1 queries

### **Mapping Strategy**
- Use MapStruct for domain-to-entity mapping
- Keep domain and persistence models separate
- Use JPA converters for JSON serialization

### **Migration Strategy**
- Use Flyway for schema version control
- Don't rely on Hibernate auto-DDL in production
- Test migrations in CI/CD pipeline
