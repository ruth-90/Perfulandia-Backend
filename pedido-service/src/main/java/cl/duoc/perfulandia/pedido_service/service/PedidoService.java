package cl.duoc.perfulandia.pedido_service.service;

import cl.duoc.perfulandia.pedido_service.dto.CrearPedidoRequest;
import cl.duoc.perfulandia.pedido_service.dto.PedidoResponse;
import cl.duoc.perfulandia.pedido_service.model.DetallePedido;
import cl.duoc.perfulandia.pedido_service.model.Pedido;
import cl.duoc.perfulandia.pedido_service.repository.PedidoRepository;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PedidoService {

    // Reglas de despacho de Perfulandia
    private static final long ENVIO_GRATIS_DESDE = 40990L;
    private static final long COSTO_ENVIO = 3990L;

    private final PedidoRepository pedidoRepository;
    private final RestClient productosApi;

    // Conexión con el microservicio de Ruth
    public PedidoService(
            PedidoRepository pedidoRepository,
            @Value("${app.productos.url}") String productosUrl) {

        this.pedidoRepository = pedidoRepository;

        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        this.productosApi = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(productosUrl)
                .build();
    }

    // Datos mínimos esperados desde producto-service
    private record ProductoInfo(
            Long id,
            String nombre,
            Long precio,
            Integer stock
    ) {}

    // Consultar un producto mediante REST
    private ProductoInfo consultarProducto(Long productoId) {

        try {
            ProductoInfo producto = productosApi.get()
                    .uri("/api/v1/productos/{id}", productoId)
                    .retrieve()
                    .body(ProductoInfo.class);

            if (producto == null
                    || !Objects.equals(producto.id(), productoId)
                    || producto.nombre() == null
                    || producto.nombre().isBlank()
                    || producto.precio() == null
                    || producto.precio() < 0
                    || producto.stock() == null
                    || producto.stock() < 0) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Respuesta inválida del servicio de productos"
                );
            }

            return producto;

        } catch (RestClientResponseException error) {

            if (error.getStatusCode().value() == 404) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe el producto " + productoId
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Error al consultar el servicio de productos",
                    error
            );

        } catch (ResourceAccessException error) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "El servicio de productos no está disponible",
                    error
            );
        }
    }

    // Crear un pedido pendiente
    @Transactional
    public PedidoResponse crearPedido(
            CrearPedidoRequest request,
            Long usuarioId) {

        // El usuarioId debe venir de un JWT validado
        if (usuarioId == null || usuarioId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        // Validar información principal
        if (request == null
                || request.items() == null
                || request.items().isEmpty()
                || request.entrega() == null
                || request.metodoPago() == null
                || request.metodoPago().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Datos del pedido incompletos"
            );
        }

        Pedido pedido = new Pedido();

        pedido.setUsuarioId(usuarioId);
        pedido.setMetodoPago(request.metodoPago().trim());

        // Datos de entrega
        var entrega = request.entrega();

        pedido.setNombreEntrega(entrega.nombre());
        pedido.setCorreoEntrega(entrega.correo());
        pedido.setTelefono(entrega.telefono());
        pedido.setRegion(entrega.region());
        pedido.setComuna(entrega.comuna());
        pedido.setDireccion(entrega.direccion());

        long subtotalProductos = 0L;
        Set<Long> productosAgregados = new HashSet<>();

        // Recorrer los productos enviados desde React
        for (CrearPedidoRequest.ItemRequest item : request.items()) {

            if (item == null
                    || item.productoId() == null
                    || item.productoId() <= 0
                    || item.cantidad() == null
                    || item.cantidad() <= 0) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Producto o cantidad inválida"
                );
            }

            // Evitar duplicados en la misma solicitud
            if (!productosAgregados.add(item.productoId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Hay productos repetidos en el pedido"
                );
            }

            // Consultar producto real desde el backend de Ruth
            ProductoInfo producto = consultarProducto(item.productoId());

            // Verificación preliminar de stock
            if (producto.stock() < item.cantidad()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Stock insuficiente para " + producto.nombre()
                );
            }

            try {
                long subtotal = Math.multiplyExact(
                        producto.precio(),
                        item.cantidad().longValue()
                );

                DetallePedido detalle = new DetallePedido();

                detalle.setProductoId(producto.id());
                detalle.setNombre(producto.nombre());
                detalle.setCantidad(item.cantidad());
                detalle.setPrecioUnitario(producto.precio());
                detalle.setSubtotal(subtotal);

                // Asociar el detalle al pedido
                pedido.agregarDetalle(detalle);

                subtotalProductos = Math.addExact(
                        subtotalProductos,
                        subtotal
                );

            } catch (ArithmeticException error) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El importe del pedido excede el límite permitido"
                );
            }
        }

        // Calcular despacho
        long envio = subtotalProductos >= ENVIO_GRATIS_DESDE
                ? 0L
                : COSTO_ENVIO;

        pedido.setCostoEnvio(envio);

        try {
            pedido.setTotal(Math.addExact(subtotalProductos, envio));
        } catch (ArithmeticException error) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El total excede el límite permitido"
            );
        }

        // Permanece PENDIENTE hasta confirmar stock y pago
        Pedido guardado = pedidoRepository.save(pedido);

        return PedidoResponse.desdeEntidad(guardado);
    }

    // Consultar los pedidos del cliente autenticado
    @Transactional(readOnly = true)
    public List<PedidoResponse> misPedidos(Long usuarioId) {

        return pedidoRepository
                .findByUsuarioIdOrderByFechaCreacionDesc(usuarioId)
                .stream()
                .map(PedidoResponse::desdeEntidad)
                .toList();
    }

    // Consultar un pedido verificando su propietario
    @Transactional(readOnly = true)
    public PedidoResponse buscarMiPedido(Long id, Long usuarioId) {

        Pedido pedido = pedidoRepository
                .findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido no encontrado"
                ));

        return PedidoResponse.desdeEntidad(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .map(PedidoResponse::desdeEntidad)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }
    @Transactional(readOnly = true)
    public List<PedidoResponse> listarTodos() {

        return pedidoRepository.findAll()
                .stream()
                .map(PedidoResponse::desdeEntidad)
                .toList();
    }
}
