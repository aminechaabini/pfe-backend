# Service Layer & MapStruct Mappers - Ultra-Detailed Plan

## Executive Summary

This document provides a comprehensive architectural plan for implementing:
1. **MapStruct Mappers** - Bidirectional mapping between domain and persistence layers
2. **Service Layer** - Business logic orchestration and transaction management

**Key Principles:**
- Domain models remain pure (no persistence concerns)
- Mappers handle all transformation complexity
- Services enforce business rules and manage transactions
- Clear separation of concerns

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [MapStruct Configuration](#mapstruct-configuration)
3. [Mapper Design](#mapper-design)
4. [Complex Mapping Scenarios](#complex-mapping-scenarios)
5. [Service Layer Design](#service-layer-design)
6. [Transaction Management](#transaction-management)
7. [Error Handling Strategy](#error-handling-strategy)
8. [Implementation Order](#implementation-order)
9. [Testing Strategy](#testing-strategy)
10. [Integration Patterns](#integration-patterns)

---

## Architecture Overview

### Layered Architecture

```
┌─────────────────────────────────────────────┐
│         Controller/API Layer                │
│         (REST endpoints, DTOs)              │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│           Service Layer                     │
│  - Business logic                           │
│  - Transaction management                   │
│  - Domain orchestration                     │
│  - Uses: Domain models                      │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│         Mapper Layer (MapStruct)            │
│  - Domain ↔ Entity conversion               │
│  - JSON serialization/deserialization       │
│  - Handles complexity                       │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│        Repository Layer                     │
│  - Persistence operations                   │
│  - Custom queries                           │
│  - Uses: Persistence entities               │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│            Database (H2)                    │
└─────────────────────────────────────────────┘
```

### Key Design Decisions

#### 1. Domain Purity
✅ **Decision**: Keep domain models completely free of persistence concerns

**Rationale**:
- Domain models contain business logic and invariants
- No JPA annotations in domain
- No dependency on persistence framework
- Easier to test and reason about

#### 2. Mapper Responsibility
✅ **Decision**: Mappers handle ALL transformation complexity

**What mappers do**:
- Convert domain ↔ entity
- Serialize/deserialize JSON fields
- Handle collections
- Map polymorphic hierarchies
- Manage circular references

**What mappers DON'T do**:
- Business logic
- Validation (that's in domain)
- Database operations
- Transaction management

#### 3. Service Orchestration
✅ **Decision**: Services orchestrate domain logic and persistence

**What services do**:
- Use domain models exclusively
- Enforce business rules
- Manage transactions
- Coordinate multiple repositories
- Handle domain events
- Convert exceptions

**What services DON'T do**:
- Direct database access (use repositories)
- Manual mapping (use mappers)
- Expose entities to controllers

---

## MapStruct Configuration

### Maven Dependency

```xml
<!-- pom.xml -->
<properties>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
    <lombok.version>1.18.30</lombok.version>
</properties>

<dependencies>
    <!-- MapStruct -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>

    <!-- If using Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
        <scope>provided</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>17</source>
                <target>17</target>
                <annotationProcessorPaths>
                    <!-- MapStruct processor -->
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${mapstruct.version}</version>
                    </path>
                    <!-- If using Lombok -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${lombok.version}</version>
                    </path>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok-mapstruct-binding</artifactId>
                        <version>0.2.0</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Gradle Configuration

```kotlin
// build.gradle.kts
plugins {
    id("java")
    id("org.springframework.boot") version "3.2.0"
}

dependencies {
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // If using Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
}
```

### MapStruct Configuration File

```java
// MapStructConfig.java
package com.example.demo.orchestrator.mapper.config;

import org.mapstruct.*;

/**
 * Shared MapStruct configuration for all mappers.
 */
@MapperConfig(
    componentModel = "spring",                    // Spring integration
    unmappedTargetPolicy = ReportingPolicy.ERROR, // Fail on unmapped fields
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface MapStructConfig {
}
```

---

## Mapper Design

### Mapper Architecture

```
orchestrator/
└── mapper/
    ├── config/
    │   └── MapStructConfig.java           [Shared config]
    │
    ├── definition/                        [Test definition mappers]
    │   ├── ProjectMapper.java
    │   ├── TestSuiteMapper.java
    │   ├── TestCaseMapper.java
    │   ├── ApiTestMapper.java
    │   ├── E2eTestMapper.java
    │   ├── E2eStepMapper.java
    │   └── AssertionMapper.java
    │
    ├── request/                           [HTTP request mappers]
    │   ├── HttpRequestMapper.java
    │   ├── RestRequestMapper.java
    │   ├── SoapRequestMapper.java
    │   ├── AuthMapper.java
    │   └── BodyMapper.java
    │
    ├── run/                               [Execution result mappers]
    │   ├── TestSuiteRunMapper.java
    │   ├── TestCaseRunMapper.java
    │   ├── ApiTestRunMapper.java
    │   ├── E2eTestRunMapper.java
    │   ├── E2eStepRunMapper.java
    │   └── AssertionResultMapper.java
    │
    └── helper/                            [Utility mappers]
        ├── JsonSerializationMapper.java   [Domain object → JSON]
        ├── CollectionMapper.java          [Collection helpers]
        └── CycleAvoidingMappingContext.java [Circular reference handling]
```

### Total Mappers: 20

---

## Mapper Specifications

### 1. ProjectMapper

**Responsibility**: Map Project ↔ ProjectEntity

**Challenges**:
- Circular reference: Project ↔ TestSuite
- Factory method: `Project.create()` instead of constructor
- Unmodifiable collections in domain
- Bidirectional relationship management

**Interface**:

```java
@Mapper(
    config = MapStructConfig.class,
    uses = {TestSuiteMapper.class}
)
public interface ProjectMapper {

    /**
     * Convert domain Project to persistence entity.
     * Handles unmodifiable collections from domain.
     */
    @Mapping(target = "testSuites", ignore = true) // Handle separately to avoid cycles
    ProjectEntity toEntity(Project domain);

    /**
     * Convert persistence entity to domain Project.
     * Uses factory method and rebuilds unmodifiable collections.
     */
    @Mapping(target = "testSuites", ignore = true) // Handle separately
    Project toDomain(ProjectEntity entity);

    /**
     * Update existing entity from domain model.
     * Preserves JPA managed state.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "testSuites", ignore = true)
    void updateEntityFromDomain(Project domain, @MappingTarget ProjectEntity entity);

    /**
     * After mapping, handle bidirectional relationships.
     */
    @AfterMapping
    default void linkTestSuites(@MappingTarget ProjectEntity entity, Project domain) {
        if (domain.getTestSuites() != null && !domain.getTestSuites().isEmpty()) {
            entity.getTestSuites().clear();
            for (TestSuite suite : domain.getTestSuites()) {
                // Find or create TestSuiteEntity
                // This requires TestSuiteMapper and repository lookup
                // Will be implemented in service layer
            }
        }
    }
}
```

**Custom Mapping Logic Needed**:

```java
@Component
public class ProjectMapperDecorator implements ProjectMapper {

    @Autowired
    private ProjectMapper delegate;

    @Autowired
    private TestSuiteMapper testSuiteMapper;

    @Autowired
    private TestSuiteRepository testSuiteRepository;

    @Override
    public Project toDomain(ProjectEntity entity) {
        if (entity == null) return null;

        // Use factory method
        Project project = Project.create(entity.getName(), entity.getDescription());
        project.setId(entity.getId());

        // Copy variables
        entity.getVariables().forEach(project::setVariable);

        // Map test suites (avoiding circular reference)
        for (TestSuiteEntity suiteEntity : entity.getTestSuites()) {
            TestSuite suite = testSuiteMapper.toDomainWithoutProjects(suiteEntity);
            project.addSuite(suite);
        }

        return project;
    }

    @Override
    public ProjectEntity toEntity(Project domain) {
        if (domain == null) return null;

        ProjectEntity entity = new ProjectEntity(domain.getName(), domain.getDescription());
        entity.setId(domain.getId());
        entity.setVariables(new HashMap<>(domain.getVariables()));

        // Test suites handled in service layer to avoid circular dependency

        return entity;
    }
}
```

---

### 2. TestSuiteMapper

**Responsibility**: Map TestSuite ↔ TestSuiteEntity

**Challenges**:
- Circular reference: TestSuite ↔ Project
- Contains TestCases (polymorphic)
- Bidirectional relationships

**Interface**:

```java
@Mapper(
    config = MapStructConfig.class,
    uses = {TestCaseMapper.class}
)
public interface TestSuiteMapper {

    @Mapping(target = "projects", ignore = true) // Avoid circular reference
    @Mapping(target = "testCases", source = "testCases")
    TestSuiteEntity toEntity(TestSuite domain);

    @Mapping(target = "testCases", source = "testCases")
    TestSuite toDomain(TestSuiteEntity entity);

    /**
     * Special method to avoid circular reference with projects.
     */
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "testCases", source = "testCases")
    TestSuite toDomainWithoutProjects(TestSuiteEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "projects", ignore = true)
    void updateEntityFromDomain(TestSuite domain, @MappingTarget TestSuiteEntity entity);
}
```

---

### 3. TestCaseMapper (Polymorphic Mapping)

**Responsibility**: Map TestCase hierarchy ↔ TestCaseEntity hierarchy

**Challenges**:
- Abstract base class with 3 concrete types
- Need @SubclassMapping for polymorphism
- Different fields per subtype

**Interface**:

```java
@Mapper(
    config = MapStructConfig.class,
    uses = {
        ApiTestMapper.class,
        E2eTestMapper.class,
        AssertionMapper.class
    }
)
public interface TestCaseMapper {

    /**
     * Map domain TestCase to entity (polymorphic).
     * MapStruct will use @SubclassMapping to determine concrete type.
     */
    @SubclassMapping(source = RestApiTest.class, target = RestApiTestEntity.class)
    @SubclassMapping(source = SoapApiTest.class, target = SoapApiTestEntity.class)
    @SubclassMapping(source = E2eTest.class, target = E2eTestEntity.class)
    TestCaseEntity toEntity(TestCase domain);

    /**
     * Map entity TestCase to domain (polymorphic).
     */
    @SubclassMapping(source = RestApiTestEntity.class, target = RestApiTest.class)
    @SubclassMapping(source = SoapApiTestEntity.class, target = SoapApiTest.class)
    @SubclassMapping(source = E2eTestEntity.class, target = E2eTest.class)
    TestCase toDomain(TestCaseEntity entity);

    /**
     * Map list of test cases (polymorphic).
     */
    List<TestCaseEntity> toEntities(List<TestCase> domains);
    List<TestCase> toDomains(List<TestCaseEntity> entities);
}
```

---

### 4. ApiTestMapper (REST & SOAP)

**Responsibility**: Map REST/SOAP API tests

**Challenges**:
- HttpRequest needs JSON serialization
- Assertions collection
- Distinguish REST vs SOAP

**Interface**:

```java
@Mapper(
    config = MapStructConfig.class,
    uses = {
        HttpRequestMapper.class,
        AssertionMapper.class
    }
)
public interface ApiTestMapper {

    /**
     * Map RestApiTest to entity.
     * HTTP request serialized to JSON.
     */
    @Mapping(target = "requestJson", source = "request", qualifiedByName = "serializeRequest")
    @Mapping(target = "assertions", source = "assertions")
    RestApiTestEntity toEntity(RestApiTest domain);

    /**
     * Map entity to RestApiTest.
     * HTTP request deserialized from JSON.
     */
    @Mapping(target = "request", source = "requestJson", qualifiedByName = "deserializeRestRequest")
    @Mapping(target = "assertions", source = "assertions")
    RestApiTest toRestApiTest(RestApiTestEntity entity);

    /**
     * Map SoapApiTest to entity.
     */
    @Mapping(target = "requestJson", source = "request", qualifiedByName = "serializeRequest")
    @Mapping(target = "assertions", source = "assertions")
    SoapApiTestEntity toEntity(SoapApiTest domain);

    /**
     * Map entity to SoapApiTest.
     */
    @Mapping(target = "request", source = "requestJson", qualifiedByName = "deserializeSoapRequest")
    @Mapping(target = "assertions", source = "assertions")
    SoapApiTest toSoapApiTest(SoapApiTestEntity entity);

    /**
     * Serialize HTTP request to JSON.
     */
    @Named("serializeRequest")
    default String serializeRequest(HttpRequest<?> request) {
        // Delegate to JsonSerializationMapper
        return JsonSerializationMapper.toJson(request);
    }

    /**
     * Deserialize REST request from JSON.
     */
    @Named("deserializeRestRequest")
    default RestRequest deserializeRestRequest(String json) {
        return JsonSerializationMapper.fromJson(json, RestRequest.class);
    }

    /**
     * Deserialize SOAP request from JSON.
     */
    @Named("deserializeSoapRequest")
    default SoapRequest deserializeSoapRequest(String json) {
        return JsonSerializationMapper.fromJson(json, SoapRequest.class);
    }
}
```

---

### 5. E2eTestMapper

**Responsibility**: Map E2E tests with steps

**Challenges**:
- Ordered collection of steps
- Steps have complex structure

**Interface**:

```java
@Mapper(
    config = MapStructConfig.class,
    uses = {E2eStepMapper.class}
)
public interface E2eTestMapper {

    @Mapping(target = "steps", source = "steps")
    E2eTestEntity toEntity(E2eTest domain);

    @Mapping(target = "steps", source = "steps")
    E2eTest toDomain(E2eTestEntity entity);
}
```

---

### 6. E2eStepMapper

**Responsibility**: Map E2E steps

**Challenges**:
- HttpRequest → JSON
- ExtractorItems → JSON
- Assertions collection
- Order preservation

**Interface**:

```java
@Mapper(
    config = MapStructConfig.class,
    uses = {
        HttpRequestMapper.class,
        AssertionMapper.class
    }
)
public interface E2eStepMapper {

    @Mapping(target = "httpRequestJson", source = "httpRequest", qualifiedByName = "serializeRequest")
    @Mapping(target = "extractorsJson", source = "extractorItems", qualifiedByName = "serializeExtractors")
    @Mapping(target = "assertions", source = "assertions")
    E2eStepEntity toEntity(E2eStep domain);

    @Mapping(target = "httpRequest", source = "httpRequestJson", qualifiedByName = "deserializeRequest")
    @Mapping(target = "extractorItems", source = "extractorsJson", qualifiedByName = "deserializeExtractors")
    @Mapping(target = "assertions", source = "assertions")
    E2eStep toDomain(E2eStepEntity entity);

    List<E2eStepEntity> toEntities(List<E2eStep> domains);
    List<E2eStep> toDomains(List<E2eStepEntity> entities);

    @Named("serializeRequest")
    default String serializeRequest(HttpRequest<?> request) {
        return JsonSerializationMapper.toJson(request);
    }

    @Named("deserializeRequest")
    default HttpRequest<?> deserializeRequest(String json) {
        // Determine type and deserialize
        return JsonSerializationMapper.fromJson(json, HttpRequest.class);
    }

    @Named("serializeExtractors")
    default String serializeExtractors(List<ExtractorItem> extractors) {
        return JsonSerializationMapper.toJson(extractors);
    }

    @Named("deserializeExtractors")
    default List<ExtractorItem> deserializeExtractors(String json) {
        return JsonSerializationMapper.fromJsonList(json, ExtractorItem.class);
    }
}
```

---

### 7. AssertionMapper

**Responsibility**: Map Assertion (record) ↔ AssertionEntity

**Challenges**:
- Domain uses record (immutable)
- Simple mapping

**Interface**:

```java
@Mapper(config = MapStructConfig.class)
public interface AssertionMapper {

    AssertionEntity toEntity(Assertion domain);

    Assertion toDomain(AssertionEntity entity);

    List<AssertionEntity> toEntities(List<Assertion> domains);
    List<Assertion> toDomains(List<AssertionEntity> entities);
}
```

**Note**: MapStruct handles records automatically in recent versions.

---

### 8. HttpRequestMapper

**Responsibility**: Serialize/deserialize HTTP requests

**Challenges**:
- Polymorphic (RestRequest, SoapRequest)
- Complex nested structure (Auth, Body, Headers)
- Must preserve all data

**Implementation**:

```java
@Component
public class HttpRequestMapper {

    private final ObjectMapper objectMapper;

    public HttpRequestMapper() {
        this.objectMapper = new ObjectMapper();
        // Configure for polymorphic types
        objectMapper.activateDefaultTyping(
            objectMapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
    }

    public String toJson(HttpRequest<?> request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new MappingException("Failed to serialize HttpRequest", e);
        }
    }

    public <T extends HttpRequest<?>> T fromJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new MappingException("Failed to deserialize HttpRequest", e);
        }
    }
}
```

---

### 9-14. Run Mappers

Similar structure to test definition mappers, but for execution results:

- **TestSuiteRunMapper**: TestSuiteRun ↔ TestSuiteRunEntity
- **TestCaseRunMapper**: Polymorphic run mapping
- **ApiTestRunMapper**: API test execution results
- **E2eTestRunMapper**: E2E test execution results
- **E2eStepRunMapper**: Step execution results
- **AssertionResultMapper**: Assertion evaluation results

---

## Complex Mapping Scenarios

### Scenario 1: Circular References

**Problem**: Project ↔ TestSuite creates infinite loop

**Solution**: Use `@Context` and cycle detection

```java
// CycleAvoidingMappingContext.java
@Component
public class CycleAvoidingMappingContext {

    private final Map<Object, Object> knownInstances = new IdentityHashMap<>();

    @BeforeMapping
    public <T> T getMappedInstance(Object source, @TargetType Class<T> targetType) {
        return (T) knownInstances.get(source);
    }

    @BeforeMapping
    public void storeMappedInstance(Object source, @MappingTarget Object target) {
        knownInstances.put(source, target);
    }
}

// Usage in mapper
@Mapper(config = MapStructConfig.class)
public interface ProjectMapper {

    ProjectEntity toEntity(Project domain, @Context CycleAvoidingMappingContext context);
    Project toDomain(ProjectEntity entity, @Context CycleAvoidingMappingContext context);
}
```

**Alternative**: Break cycles by ignoring relationship in one direction

```java
@Mapper
public interface ProjectMapper {

    // When mapping Project, include testSuites
    @Mapping(target = "testSuites", source = "testSuites")
    ProjectEntity toEntity(Project domain);

    // When mapping TestSuite, IGNORE projects
    @Mapping(target = "projects", ignore = true)
    TestSuiteEntity toEntity(TestSuite domain);
}
```

---

### Scenario 2: Factory Methods

**Problem**: Domain uses `Project.create()` instead of constructor

**Solution**: Custom `@AfterMapping` or decorator

```java
@Mapper(config = MapStructConfig.class)
public abstract class ProjectMapper {

    public Project toDomain(ProjectEntity entity) {
        if (entity == null) return null;

        // Use factory method
        Project project = Project.create(entity.getName(), entity.getDescription());

        // Set other fields using abstract methods
        afterMapping(entity, project);

        return project;
    }

    @AfterMapping
    protected void afterMapping(ProjectEntity entity, @MappingTarget Project project) {
        project.setId(entity.getId());
        entity.getVariables().forEach(project::setVariable);
        // ... map other fields
    }
}
```

---

### Scenario 3: Unmodifiable Collections

**Problem**: Domain returns `Collections.unmodifiableList()`, entity uses `ArrayList`

**Solution**: MapStruct handles this automatically, but for custom logic:

```java
@Mapper
public interface TestSuiteMapper {

    @AfterMapping
    default void mapTestCases(TestSuiteEntity entity, @MappingTarget TestSuite domain) {
        // Clear and re-add (domain manages its own collection)
        for (TestCaseEntity tcEntity : entity.getTestCases()) {
            TestCase tc = testCaseMapper.toDomain(tcEntity);
            domain.addTestCase(tc);
        }
    }
}
```

---

### Scenario 4: JSON Serialization

**Problem**: Complex objects stored as JSON strings

**Solution**: Use helper component and `@Named` methods

```java
@Component
public class JsonSerializationMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new MappingException("Failed to serialize to JSON", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new MappingException("Failed to deserialize from JSON", e);
        }
    }

    public static <T> List<T> fromJsonList(String json, Class<T> elementType) {
        try {
            JavaType type = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, elementType);
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new MappingException("Failed to deserialize list from JSON", e);
        }
    }
}
```

---

### Scenario 5: Polymorphic Mapping

**Problem**: TestCase has 3 subclasses, need correct type mapping

**Solution**: Use `@SubclassMapping`

```java
@Mapper(config = MapStructConfig.class)
public interface TestCaseMapper {

    @SubclassMapping(source = RestApiTest.class, target = RestApiTestEntity.class)
    @SubclassMapping(source = SoapApiTest.class, target = SoapApiTestEntity.class)
    @SubclassMapping(source = E2eTest.class, target = E2eTestEntity.class)
    TestCaseEntity toEntity(TestCase domain);

    @SubclassMapping(source = RestApiTestEntity.class, target = RestApiTest.class)
    @SubclassMapping(source = SoapApiTestEntity.class, target = SoapApiTest.class)
    @SubclassMapping(source = E2eTestEntity.class, target = E2eTest.class)
    TestCase toDomain(TestCaseEntity entity);
}
```

---

### Scenario 6: Bidirectional Relationships

**Problem**: Project has TestSuites, TestSuiteEntity has Projects

**Solution**: Service layer manages relationships

```java
@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TestSuiteRepository testSuiteRepository;
    private final ProjectMapper projectMapper;

    public Project addTestSuiteToProject(Long projectId, Long testSuiteId) {
        // Load entities
        ProjectEntity projectEntity = projectRepository.findById(projectId)
            .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        TestSuiteEntity suiteEntity = testSuiteRepository.findById(testSuiteId)
            .orElseThrow(() -> new EntityNotFoundException("TestSuite not found"));

        // Update bidirectional relationship
        projectEntity.addTestSuite(suiteEntity); // Uses helper method

        // Save (cascades to join table)
        projectEntity = projectRepository.save(projectEntity);

        // Map to domain
        return projectMapper.toDomain(projectEntity);
    }
}
```

---

## Service Layer Design

### Service Architecture

```
orchestrator/
└── service/
    ├── definition/                     [Test definition services]
    │   ├── ProjectService.java
    │   ├── TestSuiteService.java
    │   └── TestCaseService.java
    │
    ├── execution/                      [Test execution services]
    │   ├── TestExecutionService.java
    │   ├── TestRunnerService.java
    │   └── RunRecordingService.java
    │
    ├── reporting/                      [Analytics & reporting]
    │   ├── TestReportingService.java
    │   ├── MetricsService.java
    │   └── AnalyticsService.java
    │
    └── exception/                      [Custom exceptions]
        ├── EntityNotFoundException.java
        ├── DuplicateEntityException.java
        ├── ValidationException.java
        └── TestExecutionException.java
```

### Total Services: 9

---

## Service Specifications

### 1. ProjectService

**Responsibility**: Manage project lifecycle and operations

**Methods**:

```java
@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TestSuiteRepository testSuiteRepository;
    private final ProjectMapper projectMapper;
    private final TestSuiteMapper testSuiteMapper;

    // Constructor injection
    public ProjectService(
        ProjectRepository projectRepository,
        TestSuiteRepository testSuiteRepository,
        ProjectMapper projectMapper,
        TestSuiteMapper testSuiteMapper
    ) {
        this.projectRepository = projectRepository;
        this.testSuiteRepository = testSuiteRepository;
        this.projectMapper = projectMapper;
        this.testSuiteMapper = testSuiteMapper;
    }

    /**
     * Create a new project.
     * Validates uniqueness of name.
     */
    public Project createProject(String name, String description) {
        // Check for duplicate name
        if (projectRepository.existsByName(name)) {
            throw new DuplicateEntityException("Project with name '" + name + "' already exists");
        }

        // Create domain object (validation happens in factory method)
        Project project = Project.create(name, description);

        // Map to entity
        ProjectEntity entity = projectMapper.toEntity(project);

        // Save
        entity = projectRepository.save(entity);

        // Map back to domain
        return projectMapper.toDomain(entity);
    }

    /**
     * Find project by ID.
     * Returns domain object or throws exception.
     */
    @Transactional(readOnly = true)
    public Project findById(Long id) {
        ProjectEntity entity = projectRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Project not found: " + id));
        return projectMapper.toDomain(entity);
    }

    /**
     * Find project by ID with test suites loaded.
     * Uses JOIN FETCH to avoid N+1 queries.
     */
    @Transactional(readOnly = true)
    public Project findByIdWithTestSuites(Long id) {
        ProjectEntity entity = projectRepository.findByIdWithTestSuites(id)
            .orElseThrow(() -> new EntityNotFoundException("Project not found: " + id));
        return projectMapper.toDomain(entity);
    }

    /**
     * Find all projects.
     */
    @Transactional(readOnly = true)
    public List<Project> findAll() {
        return projectRepository.findAll().stream()
            .map(projectMapper::toDomain)
            .collect(Collectors.toList());
    }

    /**
     * Search projects by name or description.
     */
    @Transactional(readOnly = true)
    public List<Project> search(String searchTerm) {
        return projectRepository.search(searchTerm).stream()
            .map(projectMapper::toDomain)
            .collect(Collectors.toList());
    }

    /**
     * Update project.
     * Loads existing entity, applies changes, saves.
     */
    public Project updateProject(Long id, String newName, String newDescription) {
        ProjectEntity entity = projectRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Project not found: " + id));

        // Map to domain to apply business rules
        Project project = projectMapper.toDomain(entity);

        // Use domain methods (includes validation)
        if (newName != null) {
            project.rename(newName);
        }
        if (newDescription != null) {
            project.updateDescription(newDescription);
        }

        // Map changes back to entity
        projectMapper.updateEntityFromDomain(project, entity);

        // Save
        entity = projectRepository.save(entity);

        return projectMapper.toDomain(entity);
    }

    /**
     * Delete project.
     * Cascades to join table, but NOT to test suites (many-to-many).
     */
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Project not found: " + id);
        }
        projectRepository.deleteById(id);
    }

    /**
     * Add test suite to project.
     * Manages bidirectional relationship.
     */
    public Project addTestSuite(Long projectId, Long testSuiteId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
            .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));

        TestSuiteEntity suiteEntity = testSuiteRepository.findById(testSuiteId)
            .orElseThrow(() -> new EntityNotFoundException("TestSuite not found: " + testSuiteId));

        // Use helper method that maintains bidirectional relationship
        projectEntity.addTestSuite(suiteEntity);

        projectEntity = projectRepository.save(projectEntity);

        return projectMapper.toDomain(projectEntity);
    }

    /**
     * Remove test suite from project.
     * Does NOT delete the test suite, just removes association.
     */
    public Project removeTestSuite(Long projectId, Long testSuiteId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
            .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));

        TestSuiteEntity suiteEntity = testSuiteRepository.findById(testSuiteId)
            .orElseThrow(() -> new EntityNotFoundException("TestSuite not found: " + testSuiteId));

        projectEntity.removeTestSuite(suiteEntity);

        projectEntity = projectRepository.save(projectEntity);

        return projectMapper.toDomain(projectEntity);
    }

    /**
     * Set project variable.
     */
    public Project setVariable(Long projectId, String variableName, String variableValue) {
        ProjectEntity entity = projectRepository.findById(projectId)
            .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));

        Project project = projectMapper.toDomain(entity);
        project.setVariable(variableName, variableValue); // Domain validates

        projectMapper.updateEntityFromDomain(project, entity);
        entity = projectRepository.save(entity);

        return projectMapper.toDomain(entity);
    }

    /**
     * Remove project variable.
     */
    public Project removeVariable(Long projectId, String variableName) {
        ProjectEntity entity = projectRepository.findById(projectId)
            .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));

        Project project = projectMapper.toDomain(entity);
        project.removeVariable(variableName);

        projectMapper.updateEntityFromDomain(project, entity);
        entity = projectRepository.save(entity);

        return projectMapper.toDomain(entity);
    }
}
```

**Transaction Boundaries**:
- Read operations: `@Transactional(readOnly = true)`
- Write operations: `@Transactional` (default)
- Each public method is a transaction

---

### 2. TestSuiteService

**Responsibility**: Manage test suite lifecycle

**Key Methods**:
- `createTestSuite(name, description)`
- `findById(id)`
- `findByIdWithTestCases(id)` - JOIN FETCH
- `updateTestSuite(id, name, description)`
- `deleteTestSuite(id)`
- `addTestCase(suiteId, testCase)`
- `removeTestCase(suiteId, testCaseId)`
- `findOrphanedTestSuites()` - Not linked to any project
- `setVariable(suiteId, name, value)`

**Similar structure to ProjectService**

---

### 3. TestCaseService

**Responsibility**: Manage test cases (polymorphic)

**Key Methods**:
- `createRestApiTest(suiteId, name, description, request, assertions)`
- `createSoapApiTest(suiteId, name, description, request, assertions)`
- `createE2eTest(suiteId, name, description, steps)`
- `findById(id)`
- `updateTestCase(id, name, description)`
- `deleteTestCase(id)`
- `addAssertion(testCaseId, assertion)`
- `removeAssertion(testCaseId, assertionId)`

**Polymorphism Handling**:

```java
public TestCase findById(Long id) {
    TestCaseEntity entity = testCaseRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("TestCase not found: " + id));

    // MapStruct handles polymorphism with @SubclassMapping
    return testCaseMapper.toDomain(entity);
}
```

---

### 4. TestExecutionService

**Responsibility**: Execute tests and record results

**Key Methods**:
- `executeTestSuite(testSuiteId)`
- `executeTestCase(testCaseId)`
- `executeE2eTest(e2eTestId)`

**Flow**:

```java
@Service
@Transactional
public class TestExecutionService {

    private final TestSuiteService testSuiteService;
    private final TestRunnerService testRunnerService;
    private final RunRecordingService runRecordingService;

    public TestSuiteRun executeTestSuite(Long testSuiteId) {
        // 1. Load test suite with all test cases
        TestSuite testSuite = testSuiteService.findByIdWithTestCases(testSuiteId);

        // 2. Create run record
        TestSuiteRun run = runRecordingService.startTestSuiteRun(testSuite);

        try {
            // 3. Execute each test case
            for (TestCase testCase : testSuite.getTestCases()) {
                TestCaseRun testCaseRun = testRunnerService.executeTestCase(testCase);
                run.addTestCaseRun(testCaseRun);
            }

            // 4. Complete run
            run.complete();

            // 5. Save results
            return runRecordingService.saveTestSuiteRun(run);

        } catch (Exception e) {
            run.fail(e.getMessage());
            runRecordingService.saveTestSuiteRun(run);
            throw new TestExecutionException("Test suite execution failed", e);
        }
    }
}
```

---

### 5. TestReportingService

**Responsibility**: Analytics and reporting

**Key Methods**:
- `getTestSuiteStatistics(testSuiteId)`
- `getSuccessRate(testSuiteId)`
- `getRecentRuns(testSuiteId, limit)`
- `getFailedTestsSince(since)`
- `getSlowestTests(limit)`
- `getMostFailingTests(limit)`
- `getTestTypeDistribution()`

**Example**:

```java
@Service
@Transactional(readOnly = true)
public class TestReportingService {

    private final TestSuiteRunRepository testSuiteRunRepository;
    private final TestCaseRunRepository testCaseRunRepository;

    public TestSuiteStatistics getStatistics(Long testSuiteId) {
        // Use repository aggregation queries
        Double successRate = testSuiteRunRepository.getSuccessRate(testSuiteId);
        List<Object[]> stats = testSuiteRunRepository.getRunStatistics(testSuiteId);
        long completedRuns = testSuiteRunRepository.countCompletedRuns(testSuiteId);

        // Build statistics object
        return TestSuiteStatistics.builder()
            .successRate(successRate)
            .totalRuns(completedRuns)
            .successCount(extractCount(stats, RunResult.SUCCESS))
            .failureCount(extractCount(stats, RunResult.FAILURE))
            .cancelledCount(extractCount(stats, RunResult.CANCELLED))
            .build();
    }

    public List<SlowTestReport> getSlowestTests(int limit) {
        List<Object[]> results = testCaseRunRepository.findSlowestTests(PageRequest.of(0, limit));

        return results.stream()
            .map(row -> new SlowTestReport(
                (Long) row[0],      // testCaseId
                (String) row[1],    // testCaseName
                (Double) row[2]     // avgDuration
            ))
            .collect(Collectors.toList());
    }
}
```

---

## Transaction Management

### Strategy

**Spring's `@Transactional`**:
- Declarative transaction management
- Rollback on unchecked exceptions
- Read-only optimization for queries

### Rules

```java
// Rule 1: Service methods are transaction boundaries
@Service
@Transactional  // Default for all methods
public class ProjectService {

    // Rule 2: Override for read-only
    @Transactional(readOnly = true)
    public Project findById(Long id) { ... }

    // Rule 3: Default for writes (readOnly = false)
    public Project createProject(...) { ... }
}

// Rule 4: Repositories are called within service transactions
// Rule 5: Mappers are outside transaction (pure functions)
```

### Transaction Propagation

```java
@Service
public class TestExecutionService {

    // New transaction (default)
    @Transactional
    public TestSuiteRun executeTestSuite(Long id) {
        // Calls other services...
    }

    // Participate in existing transaction
    @Transactional(propagation = Propagation.REQUIRED)
    public void recordTestResult(TestCaseRun run) { ... }

    // Always new transaction (for logging, auditing)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logExecution(String message) { ... }
}
```

### Rollback Strategy

```java
@Service
@Transactional
public class ProjectService {

    public Project createProject(String name, String description) {
        try {
            // ... create project
        } catch (IllegalArgumentException e) {
            // Domain validation failed - rollback
            throw new ValidationException("Invalid project data", e);
        } catch (DataAccessException e) {
            // Database error - rollback
            throw new PersistenceException("Failed to save project", e);
        }
    }
}

// Custom exceptions for rollback control
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {
    // Triggers rollback (unchecked exception)
}
```

---

## Error Handling Strategy

### Exception Hierarchy

```java
// Base exception
public class OrchestratorException extends RuntimeException {
    public OrchestratorException(String message) { super(message); }
    public OrchestratorException(String message, Throwable cause) { super(message, cause); }
}

// Entity not found (404)
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends OrchestratorException {
    public EntityNotFoundException(String message) { super(message); }
}

// Duplicate entity (409)
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEntityException extends OrchestratorException {
    public DuplicateEntityException(String message) { super(message); }
}

// Validation error (400)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends OrchestratorException {
    public ValidationException(String message) { super(message); }
    public ValidationException(String message, Throwable cause) { super(message, cause); }
}

// Test execution error (500)
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TestExecutionException extends OrchestratorException {
    public TestExecutionException(String message) { super(message); }
    public TestExecutionException(String message, Throwable cause) { super(message, cause); }
}

// Mapping error (500)
public class MappingException extends OrchestratorException {
    public MappingException(String message) { super(message); }
    public MappingException(String message, Throwable cause) { super(message, cause); }
}
```

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(e.getMessage(), "ENTITY_NOT_FOUND"));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(e.getMessage(), "VALIDATION_ERROR"));
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateEntityException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(e.getMessage(), "DUPLICATE_ENTITY"));
    }

    @ExceptionHandler(TestExecutionException.class)
    public ResponseEntity<ErrorResponse> handleExecutionError(TestExecutionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(e.getMessage(), "EXECUTION_ERROR"));
    }
}

public record ErrorResponse(String message, String errorCode) {}
```

---

## Implementation Order

### Phase 1: Foundation (Week 1)

1. **Setup MapStruct** (Day 1)
   - Add Maven/Gradle dependencies
   - Create MapStructConfig
   - Verify compilation

2. **JSON Serialization Helper** (Day 1)
   - JsonSerializationMapper
   - Configure ObjectMapper
   - Test serialization/deserialization

3. **Basic Mappers** (Days 2-3)
   - AssertionMapper (simplest)
   - ProjectMapper (no relationships)
   - TestSuiteMapper (no relationships)
   - Test each mapper

4. **Service Exceptions** (Day 3)
   - Create exception hierarchy
   - Global exception handler
   - Error response DTOs

### Phase 2: Core Services (Week 2)

5. **ProjectService** (Days 4-5)
   - CRUD operations
   - Variable management
   - Integration tests

6. **TestSuiteService** (Days 5-6)
   - CRUD operations
   - Test case management
   - Integration tests

7. **Relationship Management** (Day 6)
   - Project ↔ TestSuite linking
   - Bidirectional sync
   - Integration tests

### Phase 3: Complex Mappers (Week 2-3)

8. **HTTP Request Mappers** (Day 7)
   - HttpRequestMapper
   - RestRequestMapper
   - SoapRequestMapper
   - JSON serialization

9. **TestCaseMapper** (Days 8-9)
   - Polymorphic mapping
   - ApiTestMapper
   - E2eTestMapper
   - E2eStepMapper

10. **TestCaseService** (Days 9-10)
    - Create polymorphic test cases
    - Update operations
    - Integration tests

### Phase 4: Execution Layer (Week 3)

11. **Run Mappers** (Days 11-12)
    - TestSuiteRunMapper
    - TestCaseRunMapper (polymorphic)
    - ApiTestRunMapper
    - E2eTestRunMapper

12. **Execution Services** (Days 13-14)
    - TestExecutionService
    - TestRunnerService
    - RunRecordingService
    - Integration tests

### Phase 5: Reporting & Analytics (Week 4)

13. **Reporting Service** (Day 15)
    - TestReportingService
    - MetricsService
    - AnalyticsService

14. **End-to-End Testing** (Days 16-17)
    - Full workflow tests
    - Performance testing
    - Load testing

15. **Documentation & Cleanup** (Days 18-20)
    - API documentation
    - Service documentation
    - Code review
    - Refactoring

---

## Testing Strategy

### Unit Tests (Mappers)

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProjectMapperTest {

    @Autowired
    private ProjectMapper projectMapper;

    @Test
    void shouldMapProjectToEntity() {
        // Given
        Project project = Project.create("Test Project", "Description");
        project.setVariable("key", "value");

        // When
        ProjectEntity entity = projectMapper.toEntity(project);

        // Then
        assertThat(entity.getName()).isEqualTo("Test Project");
        assertThat(entity.getDescription()).isEqualTo("Description");
        assertThat(entity.getVariables()).containsEntry("key", "value");
    }

    @Test
    void shouldMapEntityToProject() {
        // Given
        ProjectEntity entity = new ProjectEntity("Test Project", "Description");
        entity.getVariables().put("key", "value");

        // When
        Project project = projectMapper.toDomain(entity);

        // Then
        assertThat(project.getName()).isEqualTo("Test Project");
        assertThat(project.getVariables()).containsEntry("key", "value");
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        Project project = Project.create("Test", null);

        // When
        ProjectEntity entity = projectMapper.toEntity(project);

        // Then
        assertThat(entity.getDescription()).isEqualTo("");
    }
}
```

### Integration Tests (Services)

```java
@SpringBootTest
@Transactional
class ProjectServiceIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void shouldCreateProject() {
        // When
        Project project = projectService.createProject("Test Project", "Description");

        // Then
        assertThat(project.getId()).isNotNull();
        assertThat(projectRepository.existsById(project.getId())).isTrue();
    }

    @Test
    void shouldThrowExceptionForDuplicateName() {
        // Given
        projectService.createProject("Duplicate", "First");

        // When/Then
        assertThatThrownBy(() -> projectService.createProject("Duplicate", "Second"))
            .isInstanceOf(DuplicateEntityException.class)
            .hasMessageContaining("already exists");
    }

    @Test
    void shouldAddTestSuiteToProject() {
        // Given
        Project project = projectService.createProject("Project", "Desc");
        TestSuite suite = testSuiteService.createTestSuite("Suite", "Desc");

        // When
        project = projectService.addTestSuite(project.getId(), suite.getId());

        // Then
        assertThat(project.getTestSuites()).hasSize(1);
        assertThat(project.getTestSuites().get(0).getId()).isEqualTo(suite.getId());
    }
}
```

### End-to-End Tests

```java
@SpringBootTest
@Transactional
class E2eWorkflowTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TestSuiteService testSuiteService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestExecutionService executionService;

    @Test
    void shouldExecuteCompleteTestWorkflow() {
        // 1. Create project
        Project project = projectService.createProject("E2E Project", "Test workflow");

        // 2. Create test suite
        TestSuite suite = testSuiteService.createTestSuite("API Tests", "REST API tests");

        // 3. Add suite to project
        project = projectService.addTestSuite(project.getId(), suite.getId());

        // 4. Create REST API test
        RestRequest request = new RestRequest(HttpMethod.GET, "https://api.example.com/users");
        Assertion assertion = new Assertion(AssertionType.STATUS_EQUALS, "status", "200");
        TestCase testCase = testCaseService.createRestApiTest(
            suite.getId(),
            "Get Users",
            "Fetch all users",
            request,
            List.of(assertion)
        );

        // 5. Execute test suite
        TestSuiteRun run = executionService.executeTestSuite(suite.getId());

        // 6. Verify results
        assertThat(run.getStatus()).isEqualTo(RunStatus.COMPLETED);
        assertThat(run.getTestCaseRuns()).hasSize(1);
    }
}
```

---

## Integration Patterns

### Controller → Service → Repository Flow

```java
// Controller (API Layer)
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody CreateProjectRequest request) {
        // Use service (works with domain)
        Project project = projectService.createProject(
            request.name(),
            request.description()
        );

        // Convert to DTO for API response
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ProjectDTO.fromDomain(project));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {
        Project project = projectService.findById(id);
        return ResponseEntity.ok(ProjectDTO.fromDomain(project));
    }
}

// DTO (API Layer)
public record ProjectDTO(
    Long id,
    String name,
    String description,
    Map<String, String> variables,
    List<Long> testSuiteIds
) {
    public static ProjectDTO fromDomain(Project project) {
        return new ProjectDTO(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getVariables(),
            project.getTestSuites().stream()
                .map(TestSuite::getId)
                .collect(Collectors.toList())
        );
    }
}
```

**Flow**:
1. Controller receives HTTP request
2. Controller calls service with domain objects
3. Service uses mapper to convert domain → entity
4. Service calls repository
5. Repository returns entity
6. Service uses mapper to convert entity → domain
7. Service returns domain to controller
8. Controller converts domain → DTO
9. Controller returns HTTP response

---

## Summary

### Total Components

| Component | Count | Purpose |
|-----------|-------|---------|
| **Mappers** | 20 | Domain ↔ Entity conversion |
| **Services** | 9 | Business logic orchestration |
| **Exceptions** | 6 | Error handling |
| **Helpers** | 3 | JSON, Collections, Cycles |
| **TOTAL** | **38** | Complete service layer |

### Key Benefits

✅ **Domain Purity**: Domain models stay clean, no persistence concerns
✅ **Separation of Concerns**: Each layer has clear responsibility
✅ **Testability**: Easy to unit test mappers and services
✅ **Maintainability**: Changes isolated to appropriate layer
✅ **Type Safety**: MapStruct generates compile-time safe code
✅ **Performance**: MapStruct generates optimized code, no reflection
✅ **Transaction Safety**: Clear transaction boundaries in services
✅ **Error Handling**: Consistent exception handling across layers

### Estimated Effort

- **Mappers**: 2-3 weeks (20 mappers × 4-6 hours each)
- **Services**: 2-3 weeks (9 services × 8-10 hours each)
- **Testing**: 1-2 weeks (unit + integration + e2e)
- **Documentation**: 3-5 days
- **TOTAL**: **6-9 weeks** for complete implementation

---

*Plan Version: 1.0*
*Last Updated: 2025-11-09*
*Status: Ready for Implementation*
