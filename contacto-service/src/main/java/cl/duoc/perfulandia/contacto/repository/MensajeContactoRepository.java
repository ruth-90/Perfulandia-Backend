
package cl.duoc.perfulandia.contacto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.perfulandia.contacto.model.MensajeContacto;

// =============================================
// PERFULANDIA - MICROSERVICIO DE CONTACTO
// REPOSITORIO DE MENSAJES
// =============================================

// JpaRepository permite guardar, buscar,
// listar y eliminar mensajes en H2.

public interface MensajeContactoRepository
        extends JpaRepository<MensajeContacto, Long> {

}
