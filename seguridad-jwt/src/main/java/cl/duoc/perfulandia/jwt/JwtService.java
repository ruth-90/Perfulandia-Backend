
package cl.duoc.perfulandia.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Verifica los JWT emitidos por usuario-service.
@Service
public class JwtService {

    private final SecretKey key;

    public JwtService(
            @Value("${app.jwt.secret}") String secret
    ) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);

        if (bytes.length < 32) {
            throw new IllegalArgumentException(
                    "La clave JWT debe tener al menos 32 bytes."
            );
        }

        this.key = Keys.hmacShaKeyFor(bytes);
    }

    // Comprueba firma y vencimiento del token.
    public Claims verificar(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
