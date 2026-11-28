package cl.duoc.perfulandia.producto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:producto_tests",
		"eureka.client.enabled=false"
})
class ProductoServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
