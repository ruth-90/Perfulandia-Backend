
package cl.duoc.perfulandia.usuario.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
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

        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        Usuario usuario = new Usuario();
        aplicarDatos(usuario, request);
        usuario.setFechaRegistro(LocalDateTime.now());
        return convertirAResponse(repository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarUsuarios() {
        return repository.findAll().stream().map(this::convertirAResponse).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarUsuario(Long id) {
        return convertirAResponse(obtenerUsuario(id));
    }

    @Transactional
    public UsuarioResponse actualizarCliente(Long id, RegistroUsuarioRequest request) {
        Usuario usuario = obtenerCliente(id);
        aplicarDatos(usuario, request);
        return convertirAResponse(usuario);
    }

    @Transactional
    public void eliminarCliente(Long id) {
        repository.delete(obtenerCliente(id));
    }

    private Usuario obtenerUsuario(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
    }

    private Usuario obtenerCliente(Long id) {
        Usuario usuario = obtenerUsuario(id);
        if (usuario.getRol() != RolUsuario.CLIENTE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El mantenedor permite modificar cuentas de clientes.");
        }
        return usuario;
    }

    private void aplicarDatos(Usuario usuario, RegistroUsuarioRequest request) {
        String rut = normalizarRut(request.rut());
        String correo = request.correo().trim().toLowerCase(Locale.ROOT);
        if (!validarRut(rut)) {
            throw new IllegalArgumentException("El RUT ingresado no es válido.");
        }
        if (repository.findByRut(rut).filter(otro -> !otro.getId().equals(usuario.getId())).isPresent()) {
            throw new UsuarioDuplicadoException("El RUT ya está registrado.");
        }
        if (repository.findByCorreoIgnoreCase(correo)
                .filter(otro -> !otro.getId().equals(usuario.getId())).isPresent()) {
            throw new UsuarioDuplicadoException("El correo ya está registrado.");
        }
        LocalDate nacimiento = request.fechaNacimiento();
        if (nacimiento != null && nacimiento.isAfter(LocalDate.now(ZoneId.of("America/Santiago")).minusYears(18))) {
            throw new IllegalArgumentException("El usuario debe tener 18 años o más.");
        }

        usuario.setRut(rut);
        usuario.setCorreo(correo);
        usuario.setNombre(request.nombre().trim());
        usuario.setApellidos(limpiarOpcional(request.apellidos()));
        usuario.setFechaNacimiento(nacimiento);
        usuario.setTelefono(limpiarOpcional(request.telefono()));
        usuario.setDireccion(limpiarOpcional(request.direccion()));
        usuario.setRegion(limpiarOpcional(request.region()));
        usuario.setComuna(limpiarOpcional(request.comuna()));
        if (request.password() != null) {
            if (request.password().isBlank() || request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
                throw new IllegalArgumentException("Ingresa una contraseña válida de hasta 72 bytes.");
            }
            usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        }
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
                usuario.getFechaRegistro(),
                usuario.getApellidos(),
                usuario.getFechaNacimiento()
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