
package cl.duoc.perfulandia.producto.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


// Maneja las excepciones de todos los controladores REST.
@RestControllerAdvice
public class GlobalExceptionHandler {


    // 1. PRODUCTO NO ENCONTRADO
    // Devuelve HTTP 404 cuando un producto no existe.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(
            ResourceNotFoundException ex) {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("estado", HttpStatus.NOT_FOUND.value());
        respuesta.put("error", "Recurso no encontrado");
        respuesta.put("mensaje", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(respuesta);
    }


    // 2. ERRORES DE VALIDACIÓN
    // Devuelve HTTP 400 cuando los datos enviados no son válidos.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores = new LinkedHashMap<>();

        // Recorre cada campo que no cumplió sus validaciones.
        ex.getBindingResult().getFieldErrors().forEach(error -> {

            errores.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        });

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("estado", HttpStatus.BAD_REQUEST.value());
        respuesta.put("error", "Datos inválidos");
        respuesta.put("mensajes", errores);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(respuesta);
    }
}
