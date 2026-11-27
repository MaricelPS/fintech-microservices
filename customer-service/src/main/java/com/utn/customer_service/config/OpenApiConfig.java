package com.utn.customer_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI customerServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Customer Service API")
                        .version("1.0.0")
                        .description("Manages customers and their balance, and retrieves their products "
                                + "from product-service through Feign and Eureka. Secured with JWT: "
                                + "GET endpoints require role USER or ADMIN; all other operations require ADMIN. "
                                + "Get a token from POST /auth/login and use the Authorize button."))
                .components(new Components().addSecuritySchemes(BEARER_AUTH,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }

    /**
     * Adds the security responses to every protected operation, so they don't
     * have to be repeated in each controller method:
     * 401 everywhere, and 403 on every non-GET operation (ADMIN only).
     */
    @Bean
    public OpenApiCustomizer securityResponsesCustomizer() {
        return openApi -> openApi.getPaths().forEach((path, pathItem) -> {
            if (path.startsWith("/auth")) {
                return;
            }
            pathItem.readOperationsMap().forEach((method, operation) -> {
                operation.getResponses().addApiResponse("401",
                        new ApiResponse().description("Token is missing, invalid or expired"));
                if (method != PathItem.HttpMethod.GET) {
                    operation.getResponses().addApiResponse("403",
                            new ApiResponse().description("Requires role ADMIN"));
                }
            });
        });
    }
}
