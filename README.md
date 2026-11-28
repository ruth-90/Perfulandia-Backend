# Perfulandia Backend

El ejemplo del profesor separa usuarios y productos y usa Axios con JWT para las rutas privadas. Perfulandia conserva esa separación y usa un Gateway como entrada común, con Eureka para descubrir los servicios.

## Desarrollo local

El POM raíz centraliza Java 21, Spring Boot 4.1.2, Spring Cloud 2025.1.5 y las versiones de las bibliotecas compartidas. Desde la raíz, ejecuta `.\eureka-server\mvnw.cmd -f pom.xml package` para compilar, probar y empaquetar todos los servicios. El repositorio incluye una biblioteca interna de validación JWT que se construye en el mismo reactor Maven.

Ejecuta cada JAR desde la carpeta de su módulo con `java -jar target/<servicio>-0.0.1-SNAPSHOT.jar`. Inicia `eureka-server` (8761), luego `producto-service` (8081), `usuario-service` (8082), `pedido-service` (8083), `contacto-service` (8084) y `api-gateway` (8080). Usar cada carpeta como directorio de trabajo conserva la ubicación de sus archivos H2.

La URL base de Axios para el Gateway es `http://localhost:8080/api/v1`. CORS permite React en localhost y 127.0.0.1 por el puerto 5173. Define `FRONTEND_ORIGINS` en el Gateway para configurar otro origen.

Pedidos consulta Productos por HTTP en el puerto 8081. En Docker, usa `PRODUCTOS_URL` con la dirección accesible desde el contenedor. Usuarios y Pedidos deben compartir la misma clave JWT.

Para crear la cuenta administrativa de demostración (`adminprueba@perfulandia.cl`), define `PERFULANDIA_ADMIN_ENABLED=true` y `PERFULANDIA_ADMIN_PASSWORD` antes de iniciar Usuarios. Para crear `vendedorprueba@perfulandia.cl`, define `PERFULANDIA_VENDEDOR_PASSWORD`. El registro público crea cuentas de cliente.

Para cargar el catálogo de ejemplo en una base nueva, inicia Productos con `APP_SEED_ENABLED=true`. Desactiva esa opción después de la carga si quieres que las eliminaciones se conserven en los próximos arranques.

## Perfiles y Docker

H2 guarda los datos en archivos. Las rutas y la observación de Eureka se configuran en cada módulo y los archivos de base de datos conservan su ubicación al cambiar de perfil.

| Perfil | Uso | Configuración |
| --- | --- | --- |
| `dev` | Desarrollo y demo académica. Es el perfil predeterminado | Swagger disponible, registro SQL y carga opcional de catálogo y cuentas de demostración |
| `prod` | Ejecución con configuración externa | Swagger y panel de Eureka deshabilitados. Los servicios requieren una clave JWT externa y el Gateway usa el origen autorizado que se entregue en `FRONTEND_ORIGINS` |

Para activar `prod`, define `SPRING_PROFILES_ACTIVE=prod` para los servicios. Usa la misma `PERFULANDIA_JWT_SECRET` de al menos 32 bytes para Usuarios, Productos, Pedidos y Contacto. Configura `FRONTEND_ORIGINS` en el Gateway con la URL de React. Crea las cuentas de los roles antes de ejecutar la demo con este perfil usando la misma base persistente.

Cada servicio tiene un Dockerfile. Desde la raíz, define `PERFULANDIA_JWT_SECRET` con al menos 32 bytes y ejecuta `docker compose up --build -d`. Compose publica el Gateway en el puerto 8080, deja Eureka disponible localmente en 8761 y mantiene los cuatro servicios de negocio en su red interna. Los datos H2 se guardan en volúmenes separados. Usa `docker compose ps` y el panel de Eureka para comprobar el registro antes de probar las rutas.

## Swagger y roles

Desde un arranque local, abre `/swagger-ui/index.html` en los puertos 8081 a 8084. En Swagger usa **Authorize** con el JWT que devuelve Usuarios. Las escrituras de productos y la gestión de mensajes están reservadas al administrador. El vendedor puede consultar el listado y el detalle de productos y pedidos. El cliente puede crear pedidos y consultar los propios.

El vendedor consulta precio y stock de los productos y ve las líneas, cantidades, estado, método de pago, despacho y total de cada pedido. La API reserva la administración de usuarios, productos y mensajes al administrador, y la compra a clientes y administradores.

## Contrato para React

| Pantalla | Rutas relativas a /api/v1 | Campos y permisos |
| --- | --- | --- |
| Catálogo y detalle | `GET /productos`, `GET /productos/{id}` | `id`, `nombre`, `categoria`, `precio`, `stock`, `imagen`, `descripcion` |
| Mantenedor de productos | `POST /productos`, `PUT /productos/{id}`, `DELETE /productos/{id}` | JWT de `ADMINISTRADOR`. Usa el ID del producto para editar o eliminar |
| Contacto | `POST /contactos` | `nombre`, `correo`, `comentario` |
| Registro | `POST /usuarios/registro` | `rut`, `nombre`, `correo`, `password`. Campos opcionales `apellidos`, `fechaNacimiento`, `telefono`, `direccion`, `region`, `comuna` |
| Login | `POST /auth/login` | Envía `correo` y `password`. Recibe `token`, `id`, `nombre`, `correo` y `rol` |
| Mantenedor de usuarios | `GET /admin/usuarios`, `POST /admin/usuarios`, `GET /admin/usuarios/{id}`, `PUT /admin/usuarios/{id}`, `DELETE /admin/usuarios/{id}` | JWT de `ADMINISTRADOR`. La edición y eliminación se aplican a clientes |
| Compra | `POST /pedidos` | JWT de `CLIENTE` o `ADMINISTRADOR`. Envía `items`, `entrega` y `metodoPago` |
| Historial | `GET /pedidos/mis-pedidos`, `GET /pedidos/{id}`, `GET /pedidos` | El cliente consulta sus pedidos. Administrador y vendedor pueden consultar el listado general y sus detalles |

Envía `Authorization: Bearer <token>` para los recursos privados. La fecha de nacimiento usa `AAAA-MM-DD`. Las contraseñas nuevas requieren 8 a 72 caracteres con un máximo de 72 bytes. Omite `password` al editar para conservar la anterior.

Para crear un pedido, mapea cada elemento del carrito a `{ productoId, cantidad }`. En `entrega` envía `nombre`, `telefono`, `region`, `comuna`, `direccion` y el `correo` opcional. El servidor calcula el subtotal y cobra $3.990 de despacho para subtotales bajo $40.990. La respuesta devuelve el identificador, el estado, la fecha, los artículos, la entrega y los importes. El pedido queda `PENDIENTE`. El stock se verifica al crear el pedido. El cobro y la reserva de inventario quedan fuera de ese flujo.

## Estado de Perfulandia React

El ZIP ya usa HTTP para Productos y Contacto por sus puertos directos. Login, registro, mantenedor de usuarios y confirmación de compra todavía funcionan con datos locales. Esas pantallas deben llamar a las rutas anteriores. Al migrar el acceso, usa los roles `CLIENTE`, `VENDEDOR` y `ADMINISTRADOR` de la respuesta del servidor. Muestra al vendedor los accesos de consulta de Productos y Órdenes. Confirma la compra en pantalla al recibir el pedido de la API.
