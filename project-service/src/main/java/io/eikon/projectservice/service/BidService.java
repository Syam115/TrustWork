package io.eikon.projectservice.service;

import io.eikon.projectservice.client.AuthClient;
import io.eikon.projectservice.dto.BidRequest;
import io.eikon.projectservice.dto.BidResponse;
import io.eikon.projectservice.dto.UserSummaryDTO;
import io.eikon.projectservice.entity.Bid;
import io.eikon.projectservice.entity.BidStatus;
import io.eikon.projectservice.entity.Project;
import io.eikon.projectservice.entity.ProjectStatus;
import io.eikon.projectservice.exception.*;
import io.eikon.projectservice.repository.BidRepository;
import io.eikon.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidService {

    private final BidRepository bidRepository;
    private final ProjectRepository projectRepository;
    private final AuthClient authClient;

    @Transactional
    public BidResponse placeBid(UUID projectId, BidRequest bidRequest, UUID freelancerId) {
        log.info("Freelancer {} placing bid on project {}", freelancerId, projectId);
        
        // Check 1: Project exists and is OPEN
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("Project not found: {}", projectId);
                    return new ResourceNotFoundException("Project with ID " + projectId + " not found");
                });

        if (!project.getStatus().equals(ProjectStatus.OPEN)) {
            log.warn("Project {} is not open for bidding", projectId);
            throw new InvalidProjectStateException("Cannot place bid: Project is no longer OPEN");
        }

        // Check 2: Prevent double-bidding
        if (bidRepository.existsByProjectIdAndFreelancerId(projectId, freelancerId)) {
            log.warn("Freelancer {} has already bid on project {}", freelancerId, projectId);
            throw new DuplicateResourceException("You have already placed a bid on this project");
        }

        // Check 3: Prevent self-bidding
        if (project.getClientId().equals(freelancerId)) {
            log.warn("Freelancer {} attempted to bid on own project", freelancerId);
            throw new AccessDeniedException("Clients cannot bid on their own projects");
        }

        // Fetch freelancer details from Auth Service
        UserSummaryDTO freelancerDetails = fetchUserDetails(freelancerId);

        // Create and save bid
        Bid bid = Bid.builder()
                .freelancerId(freelancerId)
                .bidAmount(bidRequest.getBidAmount())
                .proposal(bidRequest.getProposal())
                .status(BidStatus.PENDING)
                .project(project)
                .build();

        bidRepository.save(bid);
        log.info("Bid placed with ID: {}", bid.getId());

        return mapToResponse(bid, bid.getId(), freelancerDetails);
    }

    @Transactional
    public BidResponse acceptBid(UUID bidId, UUID clientId) {
        log.info("Client {} accepting bid {}", clientId, bidId);
        
        // Check 1: Bid exists
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> {
                    log.warn("Bid not found: {}", bidId);
                    return new ResourceNotFoundException("Bid with ID " + bidId + " not found");
                });

        Project project = bid.getProject();

        // Check 2: Verify ownership - only project owner can accept bids
        if (!project.getClientId().equals(clientId)) {
            log.warn("User {} is not the owner of project {}", clientId, project.getId());
            throw new AccessDeniedException("You are not authorized to accept bids on this project");
        }

        // Check 3: Project must still be OPEN
        if (!project.getStatus().equals(ProjectStatus.OPEN)) {
            log.warn("Project {} is not open for bid acceptance", project.getId());
            throw new InvalidProjectStateException("Project is no longer open for bid acceptance");
        }

        // Check 4 & 5: Transaction - Reject all other bids and accept this one
        for (Bid otherBid : project.getBids()) {
            if (!otherBid.getId().equals(bidId)) {
                otherBid.setStatus(BidStatus.REJECTED);
                log.info("Rejecting bid {} on project {}", otherBid.getId(), project.getId());
            }
        }

        // Accept the winning bid
        bid.setStatus(BidStatus.ACCEPTED);
        project.setStatus(ProjectStatus.ASSIGNED);

        bidRepository.save(bid);
        projectRepository.save(project);
        log.info("Bid {} accepted. Project {} now ASSIGNED", bidId, project.getId());

        // Fetch freelancer details from Auth Service
        UserSummaryDTO freelancerDetails = fetchUserDetails(bid.getFreelancerId());

        return mapToResponse(bid, bidId, freelancerDetails);

    }

    @Transactional(readOnly = true)
    public List<BidResponse> getBidsForProject(UUID projectId, UUID clientId) {
        log.info("Fetching bids for project: {}", projectId);

        // Verify that the project exists
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("Project not found: {}", projectId);
                    return new ResourceNotFoundException("Project with ID " + projectId + " not found");
                });

        // Security check: Only the project owner (clientId) can view bids
        if (!project.getClientId().equals(clientId)) {
            log.warn("User {} attempted to view bids for project {} they don't own", clientId, projectId);
            throw new AccessDeniedException("You are not authorized to view bids for this project");
        }

        // Fetch all bids for this project
        List<Bid> bids = bidRepository.findByProjectId(projectId);

        return bids.stream()
                .map(bid -> {
                    UserSummaryDTO freelancerDetails = fetchUserDetails(bid.getFreelancerId());
                    return mapToResponse(bid, bid.getId(), freelancerDetails);
                })
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

    private BidResponse mapToResponse(Bid bid, UUID bidId, UserSummaryDTO freelancerDetails) {
        return BidResponse.builder()
                .id(bidId.toString())
                .freelancerId(bid.getFreelancerId().toString())
                .bidAmount(bid.getBidAmount())
                .proposal(bid.getProposal())
                .status(bid.getStatus().name())
                .freelancerDetails(freelancerDetails)
                .build();
    }
}
