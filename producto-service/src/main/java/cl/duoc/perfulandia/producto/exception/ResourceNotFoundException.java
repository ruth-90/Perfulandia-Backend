
package cl.duoc.perfulandia.producto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Indica que el recurso solicitado no existe.
// Spring Boot responderá con HTTP 404 Not Found.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // Constructor que recibe el mensaje del error.
    public ResourceNotFoundException(String mensaje) {

        // Envía el mensaje a la clase RuntimeException.
        super(mensaje);
    }
}
