package com.sousgroupe2.e_daara.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Applique à toutes les routes de l'application
                .allowedOrigins(allowedOrigins)  // Utilise la valeur de "cors.allowed-origins" définie dans application.properties
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") //Les methods autorisées
                .allowedHeaders("*")
                .allowCredentials(true); // Permet les cookies ou les credentials (JWT)
   }
}
*/