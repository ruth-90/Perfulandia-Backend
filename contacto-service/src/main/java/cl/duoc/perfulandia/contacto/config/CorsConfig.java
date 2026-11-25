
package cl.duoc.perfulandia.contacto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// =============================================
// PERFULANDIA - MICROSERVICIO DE CONTACTO
// CONFIGURACIÓN CORS PARA REACT
// =============================================

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/api/v1/contactos/**")

                        // PERMITIR EL FRONTEND DE REACT
                        .allowedOrigins(
                                "http://localhost:5173",
                                "http://127.0.0.1:5173"
                        )

                        // MÉTODOS UTILIZADOS POR CONTACTO
                        .allowedMethods(
                                "GET",
                                "POST",
                                "PATCH",
                                "DELETE",
                                "OPTIONS"
                        )

                        // PERMITIR ENCABEZADOS DE JSON
                        .allowedHeaders("*")

                        .maxAge(3600);
            }
        };
    }
}
