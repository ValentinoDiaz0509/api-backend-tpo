package com.uade.tpo.almacen.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI api() {
    final String schemeName = "bearerAuth";
    return new OpenAPI()
        .components(new Components().addSecuritySchemes(
            schemeName,
            new SecurityScheme()
                .name(schemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        ))
        // Seguridad global: todos los endpoints requieren bearer salvo los que dejaste en tu whitelist
        .addSecurityItem(new SecurityRequirement().addList(schemeName));
  }
}
