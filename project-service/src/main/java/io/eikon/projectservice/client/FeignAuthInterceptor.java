package io.eikon.projectservice.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            System.out.println("AUTH: " + authentication);

            String token = (String) authentication.getCredentials();
            System.out.println("TOKEN: " + token);

            if (token != null) {
                template.header("Authorization", "Bearer " + token);
            }
        }
    }
}
