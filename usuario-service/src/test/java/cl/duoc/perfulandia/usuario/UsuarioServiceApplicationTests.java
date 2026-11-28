package cl.duoc.perfulandia.usuario;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:usuario_tests",
		"eureka.client.enabled=false"
})
class UsuarioServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
