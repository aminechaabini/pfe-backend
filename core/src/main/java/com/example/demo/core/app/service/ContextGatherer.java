package com.example.demo.core.app.service;


import com.example.demo.core.application.service.ProjectService;
import com.example.demo.core.domain.spec.SpecSource;
import com.example.demo.core.dto.Context;
import org.springframework.stereotype.Service;

@Service
public class ContextGatherer {

    private ProjectService projectService;
    private EndpointService endpointService;

    public ContextGatherer(ProjectService projectService){
        this.projectService = projectService;
        this.endpointService = endpointService;
    }

    public Context gatherContext(Long projectId, Long endpointId){
        Project project = projectService.getProject(projectId);
        Endpoint endpoint = endpointService.getEndpoint(endpointId);
        SpecSource specSource = endpoint.getSpecSource();
        return project.getName() + " " + endpoint.getName();
    }
}
