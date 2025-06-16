package com.udea.fe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@SpringBootTest
class InnosistemasApplicationTests {

	@Test
	void contextLoads() {
		System.out.println(">>>>> Perfil de prueba activo correctamente. <<<<<");
	}

}
