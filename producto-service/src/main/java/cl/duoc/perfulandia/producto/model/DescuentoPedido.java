package cl.duoc.perfulandia.producto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.util.UUID;

@Entity
public class DescuentoPedido {

    @Id
    private UUID operacion;

    @Lob
    @Column(nullable = false)
    private String contenido;

    protected DescuentoPedido() {
    }

    public DescuentoPedido(UUID operacion, String contenido) {
        this.operacion = operacion;
        this.contenido = contenido;
    }

    public String getContenido() {
        return contenido;
    }
}
