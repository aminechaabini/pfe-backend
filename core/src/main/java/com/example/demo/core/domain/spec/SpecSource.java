package com.example.demo.core.domain.spec;

import java.time.Instant;
import java.util.*;

/**
 * SpecSource - Aggregate Root
 *
 * Represents an API specification source (OpenAPI, Swagger, WSDL).
 * Owns and manages a collection of Endpoints extracted from the spec.
 *
 * Business Rules:
 * - Each spec must have a unique name within a project
 * - Spec content is immutable once created (updates = new version)
 * - Endpoints are derived from spec parsing (cannot be manually added)
 * - Maintains lifecycle of all its endpoints
 *
 * Aggregate Boundary:
 * - SpecSource (root)
 *   └── Endpoint (child entities)
 */
public class SpecSource {

    // Validation constants
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_FILE_NAME_LENGTH = 255;
    private static final int MAX_VERSION_LENGTH = 50;

    // Identity
    private Long id;

    // Core attributes
    private String name;           // User-friendly name: "Orders API"
    private String fileName;       // Original file: "orders-v1.yaml"
    private SpecType specType;     // OPENAPI_3_0, SWAGGER_2_0, WSDL_1_1
    private String content;        // Full spec content (immutable)
    private String version;        // Spec version: "3.0.0"

    // Owned entities
    private final List<Endpoint> endpoints = new ArrayList<>();

    // Timestamps
    private final Instant createdAt;
    private Instant updatedAt;

    // Private constructor - use factory method
    private SpecSource(String name, String fileName, SpecType specType, String content) {
        validateName(name);
        validateFileName(fileName);
        Objects.requireNonNull(specType, "Spec type cannot be null");
        validateContent(content);

        this.name = name.trim();
        this.fileName = fileName.trim();
        this.specType = specType;
        this.content = content;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }



    /**
     * Factory method to create a new SpecSource.
     *
     * @param name     user-friendly name (required, will be trimmed)
     * @param fileName original file name (required)
     * @param specType type of specification
     * @param content  full spec content (required, immutable)
     * @return new SpecSource instance
     * @throws IllegalArgumentException if validation fails
     */
    public static SpecSource create(String name, String fileName, SpecType specType, String content) {
        return new SpecSource(name, fileName, specType, content);
    }

    // ========================================================================
    // AGGREGATE ROOT METHODS - Endpoint Management
    // ========================================================================

    /**
     * Add an endpoint to this spec.
     * Should only be called during spec parsing.
     *
     * @param endpoint the endpoint to add
     * @throws IllegalArgumentException if endpoint is null or duplicate
     */
    public void addEndpoint(Endpoint endpoint) {
        Objects.requireNonNull(endpoint, "Endpoint cannot be null");

        // Check for duplicate using unique key
        if (hasEndpoint(endpoint.getUniqueKey())) {
            throw new IllegalArgumentException(
                    String.format("Endpoint already exists: %s",
                            endpoint.getDisplayName())
            );
        }

        this.endpoints.add(endpoint);
        touch();
    }

    /**
     * Check if this spec contains an endpoint with the given unique key.
     */
    public boolean hasEndpoint(String uniqueKey) {
        return endpoints.stream()
                .anyMatch(e -> e.getUniqueKey().equals(uniqueKey));
    }

    /**
     * Find an endpoint by unique key.
     */
    public Optional<Endpoint> findEndpoint(String uniqueKey) {
        return endpoints.stream()
                .filter(e -> e.getUniqueKey().equals(uniqueKey))
                .findFirst();
    }

    /**
     * Get all endpoints in this spec.
     * Returns unmodifiable list to protect invariants.
     */
    public List<Endpoint> getEndpoints() {
        return Collections.unmodifiableList(endpoints);
    }

    /**
     * Get REST endpoints only.
     */
    public List<RestEndpoint> getRestEndpoints() {
        return endpoints.stream()
                .filter(e -> e instanceof RestEndpoint)
                .map(e -> (RestEndpoint) e)
                .toList();
    }

    /**
     * Get SOAP endpoints only.
     */
    public List<SoapEndpoint> getSoapEndpoints() {
        return endpoints.stream()
                .filter(e -> e instanceof SoapEndpoint)
                .map(e -> (SoapEndpoint) e)
                .toList();
    }

    /**
     * Count total endpoints in this spec.
     */
    public int getEndpointCount() {
        return endpoints.size();
    }

    // ========================================================================
    // GETTERS
    // ========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Cannot change ID once set");
        }
        this.id = Objects.requireNonNull(id, "ID cannot be null");
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public SpecType getSpecType() {
        return specType;
    }

    public String getContent() {
        return content;
    }

    public String getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // ========================================================================
    // BUSINESS METHODS
    // ========================================================================

    /**
     * Set the spec version (extracted during parsing).
     */
    public void setVersion(String version) {
        if (version != null && version.length() > MAX_VERSION_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Version must be at most %d characters", MAX_VERSION_LENGTH)
            );
        }
        this.version = version == null ? null : version.trim();
        touch();
    }

    /**
     * Rename the spec source.
     *
     * @param newName new name (required, will be trimmed)
     * @throws IllegalArgumentException if name is invalid
     */
    public void rename(String newName) {
        validateName(newName);
        String trimmed = newName.trim();
        if (trimmed.equals(this.name)) return;
        this.name = trimmed;
        touch();
    }

    /**
     * Check if this spec is of OpenAPI type (any version).
     */
    public boolean isOpenApi() {
        return specType == SpecType.OPENAPI_3_0 || specType == SpecType.OPENAPI_3_1;
    }

    /**
     * Check if this spec is of Swagger type.
     */
    public boolean isSwagger() {
        return specType == SpecType.SWAGGER_2_0;
    }

    /**
     * Check if this spec is of WSDL type (any version).
     */
    public boolean isWsdl() {
        return specType == SpecType.WSDL_1_1 || specType == SpecType.WSDL_2_0;
    }

    // ========================================================================
    // VALIDATION
    // ========================================================================

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Spec name cannot be null or blank");
        }
        if (name.trim().length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Spec name must be at most %d characters", MAX_NAME_LENGTH)
            );
        }
    }

    private void validateFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or blank");
        }
        if (fileName.trim().length() > MAX_FILE_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("File name must be at most %d characters", MAX_FILE_NAME_LENGTH)
            );
        }
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Spec content cannot be null or blank");
        }
        // Content is stored as-is, no max length (database will use TEXT type)
    }

    /**
     * Update the updatedAt timestamp.
     */
    private void touch() {
        this.updatedAt = Instant.now();
    }
}
