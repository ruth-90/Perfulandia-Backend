
package cl.duoc.perfulandia.producto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


// Indica que esta clase contiene configuraciones de Spring.
@Configuration
public class CorsConfig {

    // Registra la configuración CORS en Spring MVC.
    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                // Configura CORS para los endpoints de productos.
                registry.addMapping("/api/v1/productos/**")

                        // Permite solicitudes desde nuestro frontend React.
                        .allowedOrigins("http://localhost:5173")

                        // Define los métodos HTTP permitidos.
                        .allowedMethods(
                                "GET",
                                "POST",
                                "PUT",
                                "PATCH",
                                "DELETE",
                                "OPTIONS"
                        )

                        // Permite los encabezados de las solicitudes.
                        .allowedHeaders(
                                "Content-Type",
                                "Authorization"
                        );
            }
        };
    }
}
