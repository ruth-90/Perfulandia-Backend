
package cl.duoc.perfulandia.usuario.config;

import cl.duoc.perfulandia.usuario.security.JwtFilter;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
 * CONFIGURACION DE SEGURIDAD
    - Rutas publicas: registro y login
    - Rutas privadas: requieren autenticacion JWT
    - El filtro JWT comprueba la identidad y el rol.
    - No se utilizan sesiones HTTP para la API.
 */

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtFilter jwtFilter) throws Exception {

        http

                // DESACTIVAR CSRF PARA LA API REST CON JWT
                .csrf(csrf -> csrf.disable())

                // NO GUARDAR SESIONES EN EL SERVIDOR
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // DEFINIR LOS PERMISOS
                .authorizeHttpRequests(autorizacion -> autorizacion

                        // PERMITIR RESPUESTAS DE ERROR
                        .dispatcherTypeMatchers(
                                DispatcherType.ERROR)
                        .permitAll()

                        // REGISTRO PUBLICO
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/usuarios/registro")
                        .permitAll()

                        // LOGIN PUBLICO
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/login")
                        .permitAll()

                        // CONSOLA H2 SOLO PARA DESARROLLO LOCAL
                        .requestMatchers(
                                "/h2-console",
                                "/h2-console/**")
                        .permitAll()

                        // SOLO LOS ADMINISTRADORES PODRAN CONSULTAR
                        // LA LISTA DE USUARIOS DE PERFULANDIA
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/admin/usuarios")
                        .hasRole("ADMINISTRADOR")

                        // LAS DEMAS RUTAS REQUIEREN AUTENTICACION
                        .anyRequest().authenticated()

                )

                // PERMITIR FRAMES DEL MISMO ORIGEN PARA H2
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()))

                // AGREGAR NUESTRO FILTRO JWT
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class)

                // DESACTIVAR LOGIN POR FORMULARIO
                .formLogin(form -> form.disable())

                // DESACTIVAR AUTENTICACIÓN HTTP BASIC
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
