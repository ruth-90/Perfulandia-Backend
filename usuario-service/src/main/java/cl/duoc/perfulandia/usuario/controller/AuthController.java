
package cl.duoc.perfulandia.usuario.controller;

import cl.duoc.perfulandia.usuario.dto.LoginRequest;
import cl.duoc.perfulandia.usuario.dto.LoginResponse;
import cl.duoc.perfulandia.usuario.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/*
 * PERFULANDIA - CONTROLADOR DE AUTENTICACION
 *
 * POST /api/v1/auth/login
 * Permite iniciar sesion y obtener un token JWT.
 *
 * GET /api/v1/auth/yo
 * Permite comprobar que el token JWT funciona.
 * Solo responde a usuarios autenticados.
 */

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // INICIO DE SESION PUBLICO
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> iniciarSesion(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse respuesta = authService.iniciarSesion(request);

        return ResponseEntity.ok(respuesta);
    }

    // CONSULTAR EL USUARIO AUTENTICADO
    @GetMapping("/yo")
    public ResponseEntity<Map<String, String>> obtenerUsuarioActual(
            Authentication authentication
    ) {

        String correo = authentication.getName();

        String rol = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        return ResponseEntity.ok(
                Map.of(
                        "correo", correo,
                        "rol", rol,
                        "mensaje", "Token JWT válido."
                )
        );
    }
}
