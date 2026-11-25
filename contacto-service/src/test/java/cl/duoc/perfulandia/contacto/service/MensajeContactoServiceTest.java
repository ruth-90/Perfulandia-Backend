
package cl.duoc.perfulandia.contacto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Sort;

import cl.duoc.perfulandia.contacto.dto.MensajeContactoRequest;
import cl.duoc.perfulandia.contacto.dto.MensajeContactoResponse;
import cl.duoc.perfulandia.contacto.model.MensajeContacto;
import cl.duoc.perfulandia.contacto.model.MensajeContacto.EstadoMensaje;
import cl.duoc.perfulandia.contacto.repository.MensajeContactoRepository;
import cl.duoc.perfulandia.contacto.service.MensajeContactoService.MensajeNoEncontradoException;

// =============================================
// PERFULANDIA - PRUEBAS UNITARIAS
// SERVICIO DE CONTACTO
// JUNIT 5 + MOCKITO
// =============================================

@ExtendWith(MockitoExtension.class)
class MensajeContactoServiceTest {

    @Mock
    private MensajeContactoRepository repository;

    @InjectMocks
    private MensajeContactoService service;

    private MensajeContacto mensajePrueba;

    // =========================================
    // PREPARAR UN MENSAJE ANTES DE CADA PRUEBA
    // =========================================

    @BeforeEach
    void preparar() {

        mensajePrueba = new MensajeContacto();

        mensajePrueba.setId(1L);
        mensajePrueba.setNombre("Cliente de Prueba");
        mensajePrueba.setCorreo("cliente@gmail.com");
        mensajePrueba.setComentario("Consulta sobre perfumes");
        mensajePrueba.setFechaCreacion(
                LocalDateTime.of(2026, 11, 18, 12, 0)
        );
        mensajePrueba.setEstado(EstadoMensaje.PENDIENTE);
    }

    // =========================================
    // PRUEBA 1 - CREAR MENSAJE
    // =========================================

    @Test
    void crearMensajeGuardaDatosCorrectamente() {

        MensajeContactoRequest request =
                new MensajeContactoRequest(
                        "  Maria  ",
                        "maria@gmail.com",
                        "  Necesito información  "
                );

        when(repository.save(any(MensajeContacto.class)))
                .thenAnswer(invocacion -> {

                    MensajeContacto mensaje =
                            invocacion.getArgument(0);

                    mensaje.setId(2L);

                    return mensaje;
                });

        MensajeContactoResponse respuesta =
                service.crearMensaje(request);

        assertEquals(2L, respuesta.id());
        assertEquals("Maria", respuesta.nombre());
        assertEquals(
                "maria@gmail.com",
                respuesta.correo()
        );
        assertEquals(
                "Necesito información",
                respuesta.comentario()
        );
        assertEquals(
                EstadoMensaje.PENDIENTE,
                respuesta.estado()
        );
        assertNotNull(respuesta.fechaCreacion());

        verify(repository).save(any(MensajeContacto.class));
    }

    // =========================================
    // PRUEBA 2 - PERMITIR CORREO OPCIONAL
    // =========================================

    @Test
    void crearMensajeSinCorreo() {

        MensajeContactoRequest request =
                new MensajeContactoRequest(
                        "Ana",
                        "",
                        "Consulta sin correo"
                );

        when(repository.save(any(MensajeContacto.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0)
                );

        MensajeContactoResponse respuesta =
                service.crearMensaje(request);

        assertNull(respuesta.correo());
        assertEquals("Ana", respuesta.nombre());
        assertEquals(
                EstadoMensaje.PENDIENTE,
                respuesta.estado()
        );
    }

    // =========================================
    // PRUEBA 3 - LISTAR MENSAJES
    // =========================================

    @Test
    void listarMensajesDevuelveRegistros() {

        when(repository.findAll(any(Sort.class)))
                .thenReturn(List.of(mensajePrueba));

        List<MensajeContactoResponse> mensajes =
                service.listarMensajes();

        assertEquals(1, mensajes.size());

        assertEquals(
                "Cliente de Prueba",
                mensajes.get(0).nombre()
        );

        verify(repository).findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "fechaCreacion"
                )
        );
    }

    // =========================================
    // PRUEBA 4 - LISTA VACÍA
    // =========================================

    @Test
    void listarMensajesSinRegistros() {

        when(repository.findAll(any(Sort.class)))
                .thenReturn(List.of());

        List<MensajeContactoResponse> mensajes =
                service.listarMensajes();

        assertTrue(mensajes.isEmpty());
    }

    // =========================================
    // PRUEBA 5 - BUSCAR MENSAJE POR ID
    // =========================================

    @Test
    void obtenerMensajePorIdExistente() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(mensajePrueba));

        MensajeContactoResponse respuesta =
                service.obtenerMensajePorId(1L);

        assertEquals(1L, respuesta.id());

        assertEquals(
                "Cliente de Prueba",
                respuesta.nombre()
        );

        assertEquals(
                EstadoMensaje.PENDIENTE,
                respuesta.estado()
        );
    }

    // =========================================
    // PRUEBA 6 - MENSAJE INEXISTENTE
    // =========================================

    @Test
    void obtenerMensajePorIdInexistente() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        MensajeNoEncontradoException error =
                assertThrows(
                        MensajeNoEncontradoException.class,
                        () -> service.obtenerMensajePorId(99L)
                );

        assertEquals(
                "No existe un mensaje con ID 99.",
                error.getMessage()
        );
    }

    // =========================================
    // PRUEBA 7 - MARCAR COMO RESPONDIDO
    // =========================================

    @Test
    void marcarMensajeComoRespondido() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(mensajePrueba));

        when(repository.save(any(MensajeContacto.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0)
                );

        MensajeContactoResponse respuesta =
                service.marcarComoRespondido(1L);

        assertEquals(
                EstadoMensaje.RESPONDIDO,
                respuesta.estado()
        );

        assertEquals(
                EstadoMensaje.RESPONDIDO,
                mensajePrueba.getEstado()
        );

        verify(repository).save(mensajePrueba);
    }

    // =========================================
    // PRUEBA 8 - RESPONDER MENSAJE INEXISTENTE
    // =========================================

    @Test
    void marcarComoRespondidoMensajeInexistente() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                MensajeNoEncontradoException.class,
                () -> service.marcarComoRespondido(99L)
        );

        verify(repository, never())
                .save(any(MensajeContacto.class));
    }

    // =========================================
    // PRUEBA 9 - ELIMINAR MENSAJE
    // =========================================

    @Test
    void eliminarMensajeExistente() {

        when(repository.existsById(1L))
                .thenReturn(true);

        service.eliminarMensaje(1L);

        verify(repository).deleteById(1L);
    }

    // =========================================
    // PRUEBA 10 - ELIMINAR MENSAJE INEXISTENTE
    // =========================================

    @Test
    void eliminarMensajeInexistente() {

        when(repository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                MensajeNoEncontradoException.class,
                () -> service.eliminarMensaje(99L)
        );

        verify(repository, never())
                .deleteById(anyLong());
    }
}
