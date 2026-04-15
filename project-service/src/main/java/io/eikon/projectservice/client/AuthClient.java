package io.eikon.projectservice.client;

import io.eikon.projectservice.dto.UserSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "AUTH-SERVICE", url = "http://localhost:8081")
public interface AuthClient {

    @GetMapping("/api/users/{id}")
    UserSummaryDTO getUserById(@PathVariable("id") UUID id);
}
