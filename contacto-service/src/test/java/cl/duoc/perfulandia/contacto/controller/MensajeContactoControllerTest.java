
package cl.duoc.perfulandia.contacto.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cl.duoc.perfulandia.contacto.dto.MensajeContactoRequest;
import cl.duoc.perfulandia.contacto.dto.MensajeContactoResponse;
import cl.duoc.perfulandia.contacto.model.MensajeContacto.EstadoMensaje;
import cl.duoc.perfulandia.contacto.service.MensajeContactoService;
import cl.duoc.perfulandia.contacto.service.MensajeContactoService.MensajeNoEncontradoException;

// =============================================
// PERFULANDIA - PRUEBAS DEL CONTROLADOR REST
// JUNIT 5 + MOCKITO + MOCKMVC
// NO UTILIZA LA BASE DE DATOS H2
// =============================================

@ExtendWith(MockitoExtension.class)
class MensajeContactoControllerTest {

    @Mock
    private MensajeContactoService service;

    @InjectMocks
    private MensajeContactoController controller;

    private MockMvc mockMvc;

    private MensajeContactoResponse mensajePrueba;

    // =========================================
    // PREPARACIÓN DE LAS PRUEBAS
    // =========================================

    @BeforeEach
    void preparar() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        mensajePrueba = new MensajeContactoResponse(
                1L,
                "Cliente de Prueba",
                "cliente@gmail.com",
                "Consulta sobre perfumes",
                LocalDateTime.of(2026, 11, 18, 12, 0),
                EstadoMensaje.PENDIENTE
        );
    }

    // =========================================
    // PRUEBA 1 - POST: CREAR MENSAJE
    // =========================================

    @Test
    void crearMensajeDevuelve201Created() throws Exception {

        when(service.crearMensaje(
                any(MensajeContactoRequest.class)
        )).thenReturn(mensajePrueba);

        String json = """
                {
                    "nombre": "Cliente de Prueba",
                    "correo": "cliente@gmail.com",
                    "comentario": "Consulta sobre perfumes"
                }
                """;

        mockMvc.perform(
                post("/api/v1/contactos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isCreated())
                .andExpect(
                        header().string(
                                "Location",
                                "http://localhost/api/v1/contactos/1"
                        )
                )
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.nombre")
                                .value("Cliente de Prueba")
                )
                .andExpect(
                        jsonPath("$.estado")
                                .value("PENDIENTE")
                );

        verify(service).crearMensaje(
                any(MensajeContactoRequest.class)
        );
    }

    // =========================================
    // PRUEBA 2 - GET: LISTAR MENSAJES
    // =========================================

    @Test
    void listarMensajesDevuelve200OK() throws Exception {

        when(service.listarMensajes())
                .thenReturn(List.of(mensajePrueba));

        mockMvc.perform(
                get("/api/v1/contactos")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(
                        jsonPath("$[0].nombre")
                                .value("Cliente de Prueba")
                )
                .andExpect(
                        jsonPath("$[0].estado")
                                .value("PENDIENTE")
                );

        verify(service).listarMensajes();
    }

    // =========================================
    // PRUEBA 3 - GET: LISTA VACÍA
    // =========================================

    @Test
    void listarMensajesSinRegistrosDevuelveListaVacia()
            throws Exception {

        when(service.listarMensajes())
                .thenReturn(List.of());

        mockMvc.perform(
                get("/api/v1/contactos")
        )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service).listarMensajes();
    }

    // =========================================
    // PRUEBA 4 - GET: BUSCAR POR ID
    // =========================================

    @Test
    void obtenerMensajePorIdDevuelve200OK()
            throws Exception {

        when(service.obtenerMensajePorId(1L))
                .thenReturn(mensajePrueba);

        mockMvc.perform(
                get("/api/v1/contactos/1")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.correo")
                                .value("cliente@gmail.com")
                )
                .andExpect(
                        jsonPath("$.comentario")
                                .value("Consulta sobre perfumes")
                );

        verify(service).obtenerMensajePorId(1L);
    }

    // =========================================
    // PRUEBA 5 - PATCH: MARCAR COMO RESPONDIDO
    // =========================================

    @Test
    void marcarRespondidoDevuelve200OK()
            throws Exception {

        MensajeContactoResponse respondido =
                new MensajeContactoResponse(
                        1L,
                        "Cliente de Prueba",
                        "cliente@gmail.com",
                        "Consulta sobre perfumes",
                        mensajePrueba.fechaCreacion(),
                        EstadoMensaje.RESPONDIDO
                );

        when(service.marcarComoRespondido(1L))
                .thenReturn(respondido);

        mockMvc.perform(
                patch("/api/v1/contactos/1/estado")
        )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.estado")
                                .value("RESPONDIDO")
                );

        verify(service).marcarComoRespondido(1L);
    }

    // =========================================
    // PRUEBA 6 - DELETE: ELIMINAR MENSAJE
    // =========================================

    @Test
    void eliminarMensajeDevuelve204NoContent()
            throws Exception {

        mockMvc.perform(
                delete("/api/v1/contactos/1")
        )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).eliminarMensaje(1L);
    }

    // =========================================
    // PRUEBA 7 - GET: MENSAJE INEXISTENTE
    // =========================================

    @Test
    void obtenerMensajeInexistenteDevuelve404()
            throws Exception {

        when(service.obtenerMensajePorId(99L))
                .thenThrow(
                        new MensajeNoEncontradoException(99L)
                );

        mockMvc.perform(
                get("/api/v1/contactos/99")
        )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.mensaje")
                                .value(
                                        "No existe un mensaje con ID 99."
                                )
                );
    }

    // =========================================
    // PRUEBA 8 - PATCH: MENSAJE INEXISTENTE
    // =========================================

    @Test
    void marcarRespondidoInexistenteDevuelve404()
            throws Exception {

        when(service.marcarComoRespondido(99L))
                .thenThrow(
                        new MensajeNoEncontradoException(99L)
                );

        mockMvc.perform(
                patch("/api/v1/contactos/99/estado")
        )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.mensaje")
                                .value(
                                        "No existe un mensaje con ID 99."
                                )
                );
    }

    // =========================================
    // PRUEBA 9 - DELETE: MENSAJE INEXISTENTE
    // =========================================

    @Test
    void eliminarMensajeInexistenteDevuelve404()
            throws Exception {

        doThrow(
                new MensajeNoEncontradoException(99L)
        ).when(service).eliminarMensaje(99L);

        mockMvc.perform(
                delete("/api/v1/contactos/99")
        )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.mensaje")
                                .value(
                                        "No existe un mensaje con ID 99."
                                )
                );
    }
}
