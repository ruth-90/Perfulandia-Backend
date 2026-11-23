
package cl.duoc.perfulandia.usuario.security;

import cl.duoc.perfulandia.usuario.model.Usuario;
import cl.duoc.perfulandia.usuario.repository.UsuarioRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/*
 *FILTRO DE SEGURIDAD JWT
 
 * revisa el token enviado en Authorization: Bearer TOKEN_JWT
 
 * token es valido: registra su identidad y rol en Spring Security.
 
 * token no  valido, no autentica al usuario.
 * Las reglas de SecurityConfig decidiran si puede acceder a la ruta.
 */

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtFilter(
            JwtService jwtService,
            UsuarioRepository usuarioRepository
    ) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // OBTENER EL ENCABEZADO AUTHORIZATION
        String authorization = request.getHeader("Authorization");

        // SI NO HAY TOKEN, CONTINUAR SIN AUTENTICAR
        if (authorization == null
                || !authorization.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7).trim();

        try {

            // VERIFICAR FIRMA Y VENCIMIENTO DEL JWT
            Claims claims = jwtService.verificar(token);

            String correo = claims.getSubject();

            if (correo != null
                    && SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                // COMPROBAR QUE EL USUARIO EXISTA
                Optional<Usuario> usuarioEncontrado =
                        usuarioRepository.findByCorreoIgnoreCase(correo);

                if (usuarioEncontrado.isPresent()) {

                    Usuario usuario = usuarioEncontrado.get();

                    // SOLO AUTENTICAR CUENTAS ACTIVAS
                    if (usuario.isActivo()) {

                        // USAR EL ROL ACTUAL DE LA BASE DE DATOS
                        String autoridad =
                                "ROLE_" + usuario.getRol().name();

                        UsernamePasswordAuthenticationToken autenticacion =
                                new UsernamePasswordAuthenticationToken(
                                        usuario.getCorreo(),
                                        null,
                                        List.of(
                                                new SimpleGrantedAuthority(
                                                        autoridad
                                                )
                                        )
                                );

                        // GUARDAR LA AUTENTICACION
                        SecurityContext contexto =
                                SecurityContextHolder.createEmptyContext();

                        contexto.setAuthentication(autenticacion);

                        SecurityContextHolder.setContext(contexto);
                    }
                }
            }

        } catch (JwtException | IllegalArgumentException ex) {

            // TOKEN INVALIDO O VENCIDO:
            // NO AUTENTICAR AL USUARIO
            SecurityContextHolder.clearContext();
        }

        // CONTINUAR CON LOS DEMAS FILTROS
        filterChain.doFilter(request, response);
    }
}
