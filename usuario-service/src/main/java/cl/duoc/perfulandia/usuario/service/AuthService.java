
package cl.duoc.perfulandia.usuario.service;

import cl.duoc.perfulandia.usuario.dto.LoginRequest;
import cl.duoc.perfulandia.usuario.dto.LoginResponse;
import cl.duoc.perfulandia.usuario.model.Usuario;
import cl.duoc.perfulandia.usuario.repository.UsuarioRepository;
import cl.duoc.perfulandia.usuario.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/*
 * PERFULANDIA - SERVICIO DE AUTENTICACION
 *
 * 1. Busca al usuario por correo.
 * 2. Comprueba la contrasena con BCrypt.
 * 3. Verifica que la cuenta este activa.
 * 4. Genera un token JWT.
 * 5. Devuelve los datos del usuario y su token.
 *
 * Nunca devuelve la contrasena ni su hash.
 */

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse iniciarSesion(LoginRequest request) {

        // BUSCAR EL USUARIO POR CORREO
        String correo = request.correo().trim();

        Usuario usuario = usuarioRepository
                .findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Correo o contraseña incorrectos."
                ));

        // COMPROBAR LA CONTRASENA CON BCRYPT
        boolean passwordCorrecta = passwordEncoder.matches(
                request.password(),
                usuario.getPasswordHash()
        );

        if (!passwordCorrecta) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Correo o contraseña incorrectos."
            );
        }

        // VERIFICAR QUE LA CUENTA ESTE ACTIVA
        if (!usuario.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La cuenta está desactivada."
            );
        }

        // GENERAR TOKEN JWT CON CORREO Y ROL
        String token = jwtService.generar(
                usuario.getCorreo(),
                usuario.getRol().name()
        );

        // DEVOLVER LOS DATOS DEL USUARIO Y EL TOKEN
        return new LoginResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol(),
                token,
                "Inicio de sesión correcto."
        );
    }
}
