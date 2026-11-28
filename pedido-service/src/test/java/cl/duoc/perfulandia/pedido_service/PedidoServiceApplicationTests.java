package cl.duoc.perfulandia.pedido_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:pedido_tests",
		"eureka.client.enabled=false"
})
class PedidoServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
