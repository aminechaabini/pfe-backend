# MapStruct Mappers

This package contains MapStruct mappers for domain ↔ entity mappings.

## What is MapStruct?

MapStruct is a code generation library that creates type-safe bean mappings at compile time using annotations. The implementations are generated during compilation, providing:
- Type safety
- Performance (no reflection)
- Compile-time error checking

## Mapper Structure

### Simple Mappers (Interfaces)
- **AssertionMapper** - Maps Assertion value objects

### Aggregate Mappers (Interfaces with @AfterMapping)
- **ProjectMapper** - Handles factory method `Project.create()` and variables map
- **TestSuiteMapper** - Maps TestSuite with variables
- **SpecSourceMapper** - Handles factory method `SpecSource.create()`

### Test Case Mappers (Abstract Classes)
- **RestApiTestMapper** - Serializes RestRequest to/from JSON
- **SoapApiTestMapper** - Serializes SoapRequest to/from JSON
- **E2eTestMapper** - Maps E2eTest with ordered steps
- **E2eStepMapper** - Serializes HttpRequest and extractors to/from JSON

### Endpoint Mappers (Abstract Classes)
- **RestEndpointMapper** - Serializes specDetails, links test suites
- **SoapEndpointMapper** - Serializes specDetails, links test suites

### Polymorphic Dispatchers (Components)
- **TestCaseMapper** - Dispatches to REST/SOAP/E2E mappers
- **EndpointMapper** - Dispatches to REST/SOAP mappers

## Usage

```java
// Inject mapper
@Service
public class ProjectService {
    private final ProjectMapper projectMapper;
}

// Entity → Domain
Project domain = projectMapper.createProject(entity);

// Domain → Entity
ProjectEntity entity = projectMapper.toEntity(domain);

// Efficient update
projectMapper.updateEntityFromDomain(entity, domain);
```

## Generated Code

MapStruct generates implementations at compile time in:
`build/generated/sources/annotationProcessor/java/main/`
