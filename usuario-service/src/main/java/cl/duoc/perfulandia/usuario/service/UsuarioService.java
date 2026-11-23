
package cl.duoc.perfulandia.usuario.service;

import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.perfulandia.usuario.dto.RegistroUsuarioRequest;
import cl.duoc.perfulandia.usuario.dto.UsuarioResponse;
import cl.duoc.perfulandia.usuario.model.Usuario;
import cl.duoc.perfulandia.usuario.model.Usuario.RolUsuario;
import cl.duoc.perfulandia.usuario.repository.UsuarioRepository;

// LÓGICA DE NEGOCIO DEL REGISTRO
@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    // =========================================
    // INYECTAR DEPENDENCIAS
    // =========================================

    public UsuarioService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================
    // REGISTRAR NUEVO CLIENTE
    // =========================================

    @Transactional
    public UsuarioResponse registrarUsuario(
            RegistroUsuarioRequest request
    ) {

        // NORMALIZAR EL RUT Y EL CORREO
        String rut = normalizarRut(request.rut());

        String correo = request.correo()
                .trim()
                .toLowerCase(Locale.ROOT);

        String nombre = request.nombre().trim();

        // VALIDAR EL RUT CHILENO
        if (!validarRut(rut)) {
            throw new IllegalArgumentException(
                    "El RUT ingresado no es válido."
            );
        }

        // COMPROBAR QUE EL RUT NO ESTÉ REGISTRADO
        if (repository.existsByRut(rut)) {
            throw new UsuarioDuplicadoException(
                    "El RUT ya está registrado."
            );
        }

        // COMPROBAR QUE EL CORREO NO ESTÉ REGISTRADO
        if (repository.existsByCorreoIgnoreCase(correo)) {
            throw new UsuarioDuplicadoException(
                    "El correo ya está registrado."
            );
        }

        // PREPARAR EL NUEVO USUARIO
        Usuario usuario = new Usuario();

        usuario.setRut(rut);
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);

        // PROTEGER LA CONTRASEÑA CON BCRYPT
        usuario.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        // DATOS OPCIONALES
        usuario.setTelefono(
                limpiarOpcional(request.telefono())
        );

        usuario.setDireccion(
                limpiarOpcional(request.direccion())
        );

        usuario.setRegion(
                limpiarOpcional(request.region())
        );

        usuario.setComuna(
                limpiarOpcional(request.comuna())
        );

        // EL CLIENTE NO PUEDE ELEGIR SU ROL
        usuario.setRol(RolUsuario.CLIENTE);

        // CUENTA ACTIVA POR DEFECTO
        usuario.setActivo(true);

        // FECHA DEL REGISTRO
        usuario.setFechaRegistro(
                LocalDateTime.now()
        );

        // GUARDAR EN LA BASE DE DATOS
        Usuario guardado = repository.save(usuario);

        // DEVOLVER DATOS SIN CONTRASEÑA
        return convertirAResponse(guardado);
    }

    // =========================================
    // NORMALIZAR RUT
    // EJEMPLO: 12.345.678-5 -> 12345678-5
    // =========================================

    private String normalizarRut(String rut) {

        if (rut == null) {
            return "";
        }

        String limpio = rut
                .trim()
                .replace(".", "")
                .replace("-", "")
                .toUpperCase(Locale.ROOT);

        if (limpio.length() < 2) {
            return limpio;
        }

        String cuerpo = limpio.substring(
                0,
                limpio.length() - 1
        );

        String digito = limpio.substring(
                limpio.length() - 1
        );

        return cuerpo + "-" + digito;
    }

    // =========================================
    // VALIDAR RUT CHILENO CON MÓDULO 11
    // =========================================

    private boolean validarRut(String rut) {

        if (rut == null ||
                !rut.matches("^[0-9]{1,8}-[0-9K]$")) {
            return false;
        }

        String[] partes = rut.split("-");

        String cuerpo = partes[0];
        char digitoIngresado = partes[1].charAt(0);

        // RECHAZAR RUT CON CUERPO CERO
        if (Long.parseLong(cuerpo) == 0) {
            return false;
        }

        int suma = 0;
        int multiplicador = 2;

        // RECORRER EL CUERPO DE DERECHA A IZQUIERDA
        for (int i = cuerpo.length() - 1; i >= 0; i--) {

            int numero = Character.digit(
                    cuerpo.charAt(i),
                    10
            );

            suma += numero * multiplicador;

            multiplicador++;

            if (multiplicador > 7) {
                multiplicador = 2;
            }
        }

        int resultado = 11 - (suma % 11);

        char digitoEsperado;

        if (resultado == 11) {
            digitoEsperado = '0';
        } else if (resultado == 10) {
            digitoEsperado = 'K';
        } else {
            digitoEsperado = Character.forDigit(
                    resultado,
                    10
            );
        }

        return digitoIngresado == digitoEsperado;
    }

    // =========================================
    // LIMPIAR CAMPOS OPCIONALES
    // =========================================

    private String limpiarOpcional(String valor) {

        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }

    // =========================================
    // CONVERTIR USUARIO A DTO DE RESPUESTA
    // NUNCA DEVOLVER EL PASSWORD HASH
    // =========================================

    private UsuarioResponse convertirAResponse(
            Usuario usuario
    ) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getRut(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                usuario.getRegion(),
                usuario.getComuna(),
                usuario.getRol(),
                usuario.isActivo(),
                usuario.getFechaRegistro()
        );
    }

    // =========================================
    // EXCEPCIÓN PARA USUARIOS DUPLICADOS
    // =========================================

    public static class UsuarioDuplicadoException
            extends RuntimeException {

        public UsuarioDuplicadoException(String mensaje) {
            super(mensaje);
        }
    }
}