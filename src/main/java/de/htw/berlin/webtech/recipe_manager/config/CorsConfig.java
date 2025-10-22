package de.htw.berlin.webtech.recipe_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS-Konfiguration: erlaubt dem Frontend (andere Origin) auf dieses Backend zuzugreifen.
 */
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")                 // gilt für alle Pfade
                        .allowedOriginPatterns(             // erlaubte Ursprünge (mit Wildcards)
                                "http://localhost:*",       // lokale Dev-Server (alle Ports)
                                "http://127.0.0.1:*",
                                "https://recipe-manager-frontend-qrb9.onrender.com" //  Render-Frontend
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // erlaubte HTTP-Methoden
                        .allowedHeaders("*")               // alle Header erlaubt
                        .allowCredentials(false)           // keine Cookies/Authorization-Credentials mitgeben
                        .maxAge(3600);                     // Preflight-Caching: 1 Stunde
            }
        };
    }
}
