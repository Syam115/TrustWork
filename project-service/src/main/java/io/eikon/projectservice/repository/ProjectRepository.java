package io.eikon.projectservice.repository;

import io.eikon.projectservice.entity.Project;
import io.eikon.projectservice.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByClientId(UUID clientId);
}
