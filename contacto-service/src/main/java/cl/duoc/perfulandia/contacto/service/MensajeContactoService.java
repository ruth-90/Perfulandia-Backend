
package cl.duoc.perfulandia.contacto.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.perfulandia.contacto.dto.MensajeContactoRequest;
import cl.duoc.perfulandia.contacto.dto.MensajeContactoResponse;
import cl.duoc.perfulandia.contacto.model.MensajeContacto;
import cl.duoc.perfulandia.contacto.model.MensajeContacto.EstadoMensaje;
import cl.duoc.perfulandia.contacto.repository.MensajeContactoRepository;

// =============================================
// PERFULANDIA - MICROSERVICIO DE CONTACTO
// LÓGICA DE NEGOCIO DE LOS MENSAJES
// =============================================

@Service
public class MensajeContactoService {

    private final MensajeContactoRepository repository;

    // INYECTAR EL REPOSITORIO
    public MensajeContactoService(
            MensajeContactoRepository repository
    ) {
        this.repository = repository;
    }

    // =========================================
    // CREAR UN MENSAJE
    // =========================================

    @Transactional
    public MensajeContactoResponse crearMensaje(
            MensajeContactoRequest request
    ) {

        MensajeContacto mensaje = new MensajeContacto();

        mensaje.setNombre(request.nombre().trim());

        // EL CORREO ES OPCIONAL
        mensaje.setCorreo(
                request.correo() == null
                        || request.correo().isBlank()
                        ? null
                        : request.correo().trim()
        );

        mensaje.setComentario(request.comentario().trim());

        // LA FECHA Y EL ESTADO LOS ASIGNA EL BACKEND
        mensaje.setFechaCreacion(LocalDateTime.now());
        mensaje.setEstado(EstadoMensaje.PENDIENTE);

        MensajeContacto guardado = repository.save(mensaje);

        return convertirAResponse(guardado);
    }

    // =========================================
    // LISTAR TODOS LOS MENSAJES
    // MÁS RECIENTES PRIMERO
    // =========================================

    @Transactional(readOnly = true)
    public List<MensajeContactoResponse> listarMensajes() {

        return repository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "fechaCreacion"
                )
        ).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // =========================================
    // BUSCAR UN MENSAJE POR SU ID
    // =========================================

    @Transactional(readOnly = true)
    public MensajeContactoResponse obtenerMensajePorId(
            Long id
    ) {

        MensajeContacto mensaje = repository.findById(id)
                .orElseThrow(() ->
                        new MensajeNoEncontradoException(id)
                );

        return convertirAResponse(mensaje);
    }

    // =========================================
    // MARCAR UN MENSAJE COMO RESPONDIDO
    // =========================================

    @Transactional
    public MensajeContactoResponse marcarComoRespondido(
            Long id
    ) {

        MensajeContacto mensaje = repository.findById(id)
                .orElseThrow(() ->
                        new MensajeNoEncontradoException(id)
                );

        mensaje.setEstado(EstadoMensaje.RESPONDIDO);

        MensajeContacto actualizado = repository.save(mensaje);

        return convertirAResponse(actualizado);
    }

    // =========================================
    // ELIMINAR UN MENSAJE
    // =========================================

    @Transactional
    public void eliminarMensaje(Long id) {

        if (!repository.existsById(id)) {
            throw new MensajeNoEncontradoException(id);
        }

        repository.deleteById(id);
    }

    // =========================================
    // CONVERTIR ENTIDAD A DTO DE RESPUESTA
    // =========================================

    private MensajeContactoResponse convertirAResponse(
            MensajeContacto mensaje
    ) {

        return new MensajeContactoResponse(
                mensaje.getId(),
                mensaje.getNombre(),
                mensaje.getCorreo(),
                mensaje.getComentario(),
                mensaje.getFechaCreacion(),
                mensaje.getEstado()
        );
    }

    // =========================================
    // EXCEPCIÓN PARA MENSAJES INEXISTENTES
    // =========================================

    public static class MensajeNoEncontradoException
            extends RuntimeException {

        public MensajeNoEncontradoException(Long id) {
            super("No existe un mensaje con ID " + id + ".");
        }
    }
}
