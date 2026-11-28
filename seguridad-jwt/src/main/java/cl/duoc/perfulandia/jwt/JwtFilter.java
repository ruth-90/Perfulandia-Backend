
package cl.duoc.perfulandia.jwt;

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

// Valida el JWT y autentica al usuario en Pedidos.
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        // Continuar sin autenticar si no hay token.
        if (authorization == null
                || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7).trim();

        try {
            // Comprobar firma y vencimiento.
            Claims claims = jwtService.verificar(token);

            Number id = claims.get("usuarioId", Number.class);
            String correo = claims.getSubject();
            String rol = claims.get("role", String.class);

            // Comprobar que el token tenga los datos necesarios.
            if (id != null && id.longValue() > 0
                    && correo != null && !correo.isBlank()
                    && ("CLIENTE".equals(rol)
                        || "ADMINISTRADOR".equals(rol) || "VENDEDOR".equals(rol))) {

                Long usuarioId = id.longValue();

                // Datos que utilizara PedidoController.
                request.setAttribute("usuarioId", usuarioId);
                request.setAttribute("rol", rol);

                // Registrar la identidad en Spring Security.
                UsernamePasswordAuthenticationToken autenticacion =
                        new UsernamePasswordAuthenticationToken(
                                correo,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                "ROLE_" + rol
                                        )
                                )
                        );

                SecurityContext contexto =
                        SecurityContextHolder.createEmptyContext();

                contexto.setAuthentication(autenticacion);
                SecurityContextHolder.setContext(contexto);
            }

        } catch (JwtException | IllegalArgumentException ex) {
            // Token invalido: no autenticar.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
