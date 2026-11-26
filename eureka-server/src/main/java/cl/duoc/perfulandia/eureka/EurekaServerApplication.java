
package cl.duoc.perfulandia.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/*
 * Servidor de descubrimiento de microservicios.
 *
 * Permite registrar y localizar los servicios
 * de Productos, Usuarios, Contacto, Pedidos
 */

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                EurekaServerApplication.class,
                args
        );
    }
}
