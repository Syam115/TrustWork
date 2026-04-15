package io.eikon.projectservice.controller;

import io.eikon.projectservice.dto.ProjectRequest;
import io.eikon.projectservice.dto.ProjectResponse;
import io.eikon.projectservice.entity.ProjectStatus;
import io.eikon.projectservice.exception.AccessDeniedException;
import io.eikon.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication) {

        UUID clientId = extractUserIdFromAuth(authentication);
        log.info("Creating project for client: {}", clientId);

        ProjectResponse response = projectService.createProject(request, clientId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllOpenProjects() {
        log.info("Fetching all OPEN projects");
        List<ProjectResponse> projects = projectService.getAllProjectsByStatus(ProjectStatus.OPEN);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable("id") UUID id) {
        log.info("Fetching project: {}", id);
        ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/my-projects")
    public ResponseEntity<List<ProjectResponse>> getMyProjects(Authentication authentication) {
        UUID clientId = extractUserIdFromAuth(authentication);
        log.info("Fetching projects for client: {}", clientId);

        List<ProjectResponse> projects = projectService.getProjectsByClientId(clientId);
        return ResponseEntity.ok(projects);
    }

    private UUID extractUserIdFromAuth(Authentication authentication) {
        if (authentication == null) {
            log.warn("Authentication object is null");
            throw new AccessDeniedException("Authentication required for this operation");
        }

        String userId = (String) authentication.getPrincipal();

        if (userId == null) {
            log.warn("userId claim not found in JWT token");
            throw new AccessDeniedException("userId claim missing from authentication token");
        }

        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid UUID format in userId claim: {}", userId);
            throw new AccessDeniedException("Invalid userId format in authentication token");
        }
    }
}

