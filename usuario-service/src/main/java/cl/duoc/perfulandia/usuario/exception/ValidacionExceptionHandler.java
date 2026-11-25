
package cl.duoc.perfulandia.usuario.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


// MANEJO DE ERRORES DE VALIDACION
@RestControllerAdvice
public class ValidacionExceptionHandler {
  
    // ERRORES DE LOS CAMPOS DEL FORMULARIO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(
            MethodArgumentNotValidException excepcion
    ) {

        // GUARDAR LOS ERRORES POR NOMBRE DE CAMPO
        Map<String, String> errores = new LinkedHashMap<>();

        excepcion.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {

                    // SI UN CAMPO TIENE VARIOS ERRORES,
                    // CONSERVAR EL PRIMER MENSAJE
                    errores.putIfAbsent(
                            error.getField(),
                            error.getDefaultMessage()
                    );
                });

        // PREPARAR RESPUESTA JSON
        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put(
                "mensaje",
                "Existen errores de validación."
        );

        respuesta.put(
                "errores",
                errores
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(respuesta);
    }
}
