package com.example.demo.orchestrator.app.service;


import com.example.demo.orchestrator.domain.spec.SpecSource;
import org.springframework.stereotype.Service;

@Service
public class ContextGatherer {

    private ProjectService projectService;
    private EndpointService endpointService;

    public ContextGatherer(ProjectService projectService){
        this.projectService = projectService;
        this.endpointService = endpointService;
    }

    public String gatherContext(Long projectId, Long endpointId){
        Project project = projectService.getProject(projectId);
        Endpoint endpoint = endpointService.getEndpoint(endpointId);
        SpecSource specSource = endpoint.getSpecSource();
        return project.getName() + " " + endpoint.getName();
    }
}
