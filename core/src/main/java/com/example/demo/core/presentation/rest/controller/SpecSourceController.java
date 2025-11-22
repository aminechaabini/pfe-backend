package com.example.demo.core.presentation.rest.controller;

import com.example.demo.core.presentation.rest.dto.response.spec.EndpointResponse;
import com.example.demo.core.presentation.rest.dto.response.spec.SpecSourceDetailResponse;
import com.example.demo.core.presentation.rest.dto.response.spec.SpecSourceResponse;
import com.example.demo.core.presentation.rest.mapper.EndpointResponseMapper;
import com.example.demo.core.presentation.rest.mapper.SpecSourceResponseMapper;
import com.example.demo.core.application.dto.spec.UploadSpecRequest;
import com.example.demo.core.application.service.SpecSourceService;
import com.example.demo.core.domain.spec.Endpoint;
import com.example.demo.core.domain.spec.SpecSource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for Spec Source management.
 *
 * Base paths:
 * - /api/projects/{projectId}/specs - Project-scoped operations
 * - /api/specs/{id} - Spec-scoped operations
 */
@RestController
public class SpecSourceController {

    private final SpecSourceService specSourceService;
    private final SpecSourceResponseMapper specMapper;
    private final EndpointResponseMapper endpointMapper;

    public SpecSourceController(
            SpecSourceService specSourceService,
            SpecSourceResponseMapper specMapper,
            EndpointResponseMapper endpointMapper) {
        this.specSourceService = specSourceService;
        this.specMapper = specMapper;
        this.endpointMapper = endpointMapper;
    }

    /**
     * Upload API specification.
     *
     * POST /api/projects/{projectId}/specs
     */
    @PostMapping("/api/projects/{projectId}/specs")
    public ResponseEntity<SpecSourceResponse> uploadSpec(
            @PathVariable Long projectId,
            @RequestBody @Valid UploadSpecRequest request) {
        SpecSource specSource = specSourceService.uploadSpec(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(specMapper.toResponse(specSource));
    }

    /**
     * Get spec source by ID (summary).
     *
     * GET /api/specs/{id}
     */
    @GetMapping("/api/specs/{id}")
    public ResponseEntity<SpecSourceResponse> getSpecSource(@PathVariable Long id) {
        SpecSource specSource = specSourceService.getSpecSource(id);
        return ResponseEntity.ok(specMapper.toResponse(specSource));
    }

    /**
     * Get spec source by ID with endpoints (detail).
     *
     * GET /api/specs/{id}/detail
     */
    @GetMapping("/api/specs/{id}/detail")
    public ResponseEntity<SpecSourceDetailResponse> getSpecSourceDetail(@PathVariable Long id) {
        SpecSource specSource = specSourceService.getSpecSourceWithEndpoints(id);
        return ResponseEntity.ok(specMapper.toDetailResponse(specSource));
    }

    /**
     * Get all spec sources for a project.
     *
     * GET /api/projects/{projectId}/specs
     */
    @GetMapping("/api/projects/{projectId}/specs")
    public ResponseEntity<List<SpecSourceResponse>> getProjectSpecSources(@PathVariable Long projectId) {
        List<SpecSource> specSources = specSourceService.getProjectSpecSources(projectId);
        return ResponseEntity.ok(specMapper.toResponseList(specSources));
    }

    /**
     * Delete spec source.
     *
     * DELETE /api/specs/{id}
     */
    @DeleteMapping("/api/specs/{id}")
    public ResponseEntity<Void> deleteSpecSource(@PathVariable Long id) {
        specSourceService.deleteSpecSource(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get endpoints from a spec source.
     *
     * GET /api/specs/{id}/endpoints
     */
    @GetMapping("/api/specs/{id}/endpoints")
    public ResponseEntity<List<EndpointResponse>> getSpecEndpoints(@PathVariable Long id) {
        List<Endpoint> endpoints = specSourceService.getSpecEndpoints(id);
        return ResponseEntity.ok(endpointMapper.toResponseList(endpoints));
    }

    /**
     * Get all endpoints across all specs in a project.
     *
     * GET /api/projects/{projectId}/endpoints
     */
    @GetMapping("/api/projects/{projectId}/endpoints")
    public ResponseEntity<List<EndpointResponse>> getProjectEndpoints(@PathVariable Long projectId) {
        List<Endpoint> endpoints = specSourceService.getProjectEndpoints(projectId);
        return ResponseEntity.ok(endpointMapper.toResponseList(endpoints));
    }
}
