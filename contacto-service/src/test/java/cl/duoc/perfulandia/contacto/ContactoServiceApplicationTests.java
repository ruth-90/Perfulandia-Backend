package cl.duoc.perfulandia.contacto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:contacto_tests",
		"eureka.client.enabled=false"
})
class ContactoServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
