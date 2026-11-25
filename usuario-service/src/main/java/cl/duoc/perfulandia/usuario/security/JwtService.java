
package cl.duoc.perfulandia.usuario.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/*
 *  SEGURIDAD JWT
 *
 * Este servicio genera y verifica tokens JWT.
 *
 * Cuando un usuario inicia sesion correctamente:
 * 1. Se crea un token con su correo y rol.
 * 2. El token se firma con una clave secreta.
 * 3. El token tiene una duracion de una hora.
 *
 * Despues podremos utilizarlo para acceder
 * a las rutas protegidas de Perfulandia.

 */

@Service
public class JwtService {

    private final SecretKey key;

    public JwtService(
            @Value("${app.jwt.secret}") String secret
    ) {

        byte[] bytes = secret.getBytes(
                StandardCharsets.UTF_8
        );

        if (bytes.length < 32) {
            throw new IllegalArgumentException(
                    "La clave JWT debe tener al menos 32 bytes."
            );
        }

        this.key = Keys.hmacShaKeyFor(bytes);
    }

    // GENERAR TOKEN JWT
    public String generar(String correo, String rol) {

        Date ahora = new Date();

        Date vencimiento = new Date(
                ahora.getTime() + 3_600_000
        );

        return Jwts.builder()
                .subject(correo)
                .claim("role", rol)
                .issuedAt(ahora)
                .expiration(vencimiento)
                .signWith(key)
                .compact();
    }

    // VERIFICAR TOKEN JWT
    public Claims verificar(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
