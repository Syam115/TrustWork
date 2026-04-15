package io.eikon.projectservice.service;

import io.eikon.projectservice.client.AuthClient;
import io.eikon.projectservice.dto.ProjectRequest;
import io.eikon.projectservice.dto.ProjectResponse;
import io.eikon.projectservice.dto.UserSummaryDTO;
import io.eikon.projectservice.entity.Project;
import io.eikon.projectservice.entity.ProjectStatus;
import io.eikon.projectservice.exception.ExternalServiceException;
import io.eikon.projectservice.exception.ResourceNotFoundException;
import io.eikon.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final AuthClient authClient;

    @Transactional
    public ProjectResponse createProject(ProjectRequest request, UUID clientId) {
        log.info("Creating project for client: {}", clientId);
        
        Project project = Project.builder()
                .clientId(clientId)
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .budget(request.getBudget())
                .status(ProjectStatus.OPEN)
                .bids(new ArrayList<>())
                .build();
        
        projectRepository.save(project);
        log.info("Project created with ID: {}", project.getId());

        // Fetch client details from Auth Service
        UserSummaryDTO clientDetails = fetchUserDetails(clientId);

        return mapToResponse(project, clientId, clientDetails);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(UUID projectId) {
        log.info("Fetching project: {}", projectId);
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("Project not found: {}", projectId);
                    return new ResourceNotFoundException("Project with ID " + projectId + " not found");
                });
        
        UUID clientId = project.getClientId();
        
        // Call Feign Client to get client details from Auth Service
        UserSummaryDTO clientDetails = fetchUserDetails(clientId);

        return mapToResponse(project, clientId, clientDetails);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjectsByStatus(ProjectStatus status) {
        log.info("Fetching all projects with status: {}", status);

        List<Project> projects = projectRepository.findByStatus(status);

        return projects.stream()
                .map(project -> {
                    UserSummaryDTO clientDetails = fetchUserDetails(project.getClientId());
                    return mapToResponse(project, project.getClientId(), clientDetails);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsByClientId(UUID clientId) {
        log.info("Fetching projects for client: {}", clientId);

        List<Project> projects = projectRepository.findByClientId(clientId);

        UserSummaryDTO clientDetails = fetchUserDetails(clientId);

        return projects.stream()
                .map(project -> mapToResponse(project, clientId, clientDetails))
                .collect(Collectors.toList());
    }

    private UserSummaryDTO fetchUserDetails(UUID userId) {
        try {
            return authClient.getUserById(userId);
        } catch (Exception ex) {
            log.error("Failed to fetch user details for ID: {}", userId, ex);
            throw new ExternalServiceException("Failed to fetch user details from Auth Service", ex);
        }
    }

    private ProjectResponse mapToResponse(Project project, UUID clientId, UserSummaryDTO clientDetails) {
        return ProjectResponse.builder()
                .id(project.getId().toString())
                .clientId(clientId.toString())
                .title(project.getTitle())
                .description(project.getDescription())
                .category(project.getCategory())
                .budget(project.getBudget())
                .status(project.getStatus().name())
                .clientDetails(clientDetails)
                .build();
    }
}
