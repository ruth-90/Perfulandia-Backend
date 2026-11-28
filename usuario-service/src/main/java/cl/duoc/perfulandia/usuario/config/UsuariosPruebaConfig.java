
package cl.duoc.perfulandia.usuario.config;

import cl.duoc.perfulandia.usuario.model.Usuario;
import cl.duoc.perfulandia.usuario.model.Usuario.RolUsuario;
import cl.duoc.perfulandia.usuario.repository.UsuarioRepository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

// Administrador de demostracion para Perfulandia.
@Configuration
@Profile("dev")
public class UsuariosPruebaConfig {

    @Bean
    public CommandLineRunner crearAdministradorPrueba(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,

            @Value("${app.demo.admin.enabled:false}")
            boolean habilitado,

            @Value("${app.demo.admin.reset-password:false}")
            boolean restablecer,

            @Value("${app.demo.admin.password:}")
            String password
    ) {
        return args -> {

            // No ejecutar si las pruebas estan desactivadas.
            if (!habilitado) {
                return;
            }

            if (password.isBlank()) {
                throw new IllegalStateException(
                    "Falta configurar la contraseña de demostracion."
                );
            }

            String correo = "adminprueba@perfulandia.cl";

            Usuario administrador = repository
                    .findByCorreoIgnoreCase(correo)
                    .orElse(null);

            if (administrador == null) {

                // Crear el administrador si no existe.
                administrador = new Usuario();

                administrador.setRut("11111111-1");
                administrador.setNombre("Administrador de Prueba");
                administrador.setCorreo(correo);
                administrador.setRol(RolUsuario.ADMINISTRADOR);
                administrador.setActivo(true);
                administrador.setFechaRegistro(LocalDateTime.now());

                administrador.setPasswordHash(
                        passwordEncoder.encode(password)
                );

                repository.save(administrador);

                System.out.println(
                    "Administrador de prueba creado correctamente."
                );

            } else if (restablecer) {

                // No cambiar contraseñas de cuentas cliente.
                if (administrador.getRol()
                        != RolUsuario.ADMINISTRADOR) {
                    throw new IllegalStateException(
                        "La cuenta existente no es administradora."
                    );
                }

                // Restablecer solo cuando se solicite.
                administrador.setPasswordHash(
                        passwordEncoder.encode(password)
                );

                repository.save(administrador);

                System.out.println(
                    "Contraseña del administrador de prueba actualizada."
                );

            } else {

                System.out.println(
                    "Administrador de prueba existente: sin cambios."
                );
            }
        };
    }

    @Bean
    public CommandLineRunner crearVendedorPrueba(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,
            @Value("${PERFULANDIA_VENDEDOR_PASSWORD:}") String password
    ) {
        return args -> {
            String correo = "vendedorprueba@perfulandia.cl";
            if (password.isBlank() || repository.findByCorreoIgnoreCase(correo).isPresent()) {
                return;
            }

            Usuario vendedor = new Usuario();
            vendedor.setRut("22222222-2");
            vendedor.setNombre("Vendedor de Prueba");
            vendedor.setCorreo(correo);
            vendedor.setRol(RolUsuario.VENDEDOR);
            vendedor.setFechaRegistro(LocalDateTime.now());
            vendedor.setPasswordHash(passwordEncoder.encode(password));
            repository.save(vendedor);
        };
    }
}
