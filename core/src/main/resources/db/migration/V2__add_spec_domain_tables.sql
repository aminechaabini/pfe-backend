-- =============================================================================
-- Migration V2: Add Spec Domain Tables
-- =============================================================================
-- Creates tables for spec sources and endpoints (REST and SOAP)
-- Part of the persistence layer alignment for the spec domain model
-- =============================================================================

-- Create spec_sources table
CREATE TABLE IF NOT EXISTS spec_sources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    spec_type VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    version VARCHAR(50),
    project_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_spec_source_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Create unique constraint for spec name within a project
CREATE UNIQUE INDEX IF NOT EXISTS idx_spec_sources_project_name
ON spec_sources(project_id, name);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_spec_sources_project_id ON spec_sources(project_id);
CREATE INDEX IF NOT EXISTS idx_spec_sources_spec_type ON spec_sources(spec_type);

-- =============================================================================

-- Create endpoints table (SINGLE_TABLE inheritance strategy)
CREATE TABLE IF NOT EXISTS endpoints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    endpoint_type VARCHAR(10) NOT NULL,
    summary VARCHAR(500),
    operation_id VARCHAR(200),
    spec_details TEXT,

    -- Foreign keys
    spec_source_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,

    -- REST-specific fields (nullable for SOAP endpoints)
    method VARCHAR(10),
    path VARCHAR(500),

    -- SOAP-specific fields (nullable for REST endpoints)
    service_name VARCHAR(200),
    operation_name VARCHAR(200),
    soap_action VARCHAR(500),
    soap_version VARCHAR(10),

    -- Audit fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_endpoint_spec_source FOREIGN KEY (spec_source_id) REFERENCES spec_sources(id) ON DELETE CASCADE,
    CONSTRAINT fk_endpoint_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_endpoints_spec_source_id ON endpoints(spec_source_id);
CREATE INDEX IF NOT EXISTS idx_endpoints_project_id ON endpoints(project_id);
CREATE INDEX IF NOT EXISTS idx_endpoints_type ON endpoints(endpoint_type);
CREATE INDEX IF NOT EXISTS idx_endpoints_method_path ON endpoints(method, path);
CREATE INDEX IF NOT EXISTS idx_endpoints_service_operation ON endpoints(service_name, operation_name);

-- =============================================================================
-- Migration complete
-- =============================================================================
