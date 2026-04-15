package io.eikon.projectservice.controller;

import io.eikon.projectservice.dto.BidRequest;
import io.eikon.projectservice.dto.BidResponse;
import io.eikon.projectservice.exception.AccessDeniedException;
import io.eikon.projectservice.service.BidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/bids")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class BidController {

    private final BidService bidService;

    @PostMapping
    public ResponseEntity<BidResponse> placeBid(
            @PathVariable("projectId") UUID projectId,
            @Valid @RequestBody BidRequest request,
            Authentication authentication) {

        UUID freelancerId = extractUserIdFromAuth(authentication);
        log.info("Freelancer {} placing bid on project {}", freelancerId, projectId);

        BidResponse response = bidService.placeBid(projectId, request, freelancerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BidResponse>> getBidsForProject(
            @PathVariable("projectId") UUID projectId,
            Authentication authentication) {

        UUID clientId = extractUserIdFromAuth(authentication);
        log.info("User {} requesting bids for project {}", clientId, projectId);

        // Verify ownership - only project owner can see bids
        List<BidResponse> bids = bidService.getBidsForProject(projectId, clientId);
        return ResponseEntity.ok(bids);
    }

    @PatchMapping("/{bidId}/accept")
    public ResponseEntity<BidResponse> acceptBid(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("bidId") UUID bidId,
            Authentication authentication) {

        UUID clientId = extractUserIdFromAuth(authentication);
        log.info("Client {} accepting bid {} for project {}", clientId, bidId, projectId);

        BidResponse response = bidService.acceptBid(bidId, clientId);
        return ResponseEntity.ok(response);
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

