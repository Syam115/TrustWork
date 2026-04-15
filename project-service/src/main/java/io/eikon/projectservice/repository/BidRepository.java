package io.eikon.projectservice.repository;

import io.eikon.projectservice.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {

    List<Bid> findByProjectId(UUID projectId);

    List<Bid> findByFreelancerId(UUID freelancerId);

    Boolean existsByProjectIdAndFreelancerId(UUID projectId, UUID freelancerId);
}
