
package cl.duoc.perfulandia.usuario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// =============================================
// PERFULANDIA - MICROSERVICIO DE USUARIOS
// CONFIGURACIÓN DE CIFRADO DE CONTRASEÑAS
// =============================================

@Configuration
public class PasswordConfig {

    // CREAR EL CODIFICADOR DE CONTRASEÑAS
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

    }
}
