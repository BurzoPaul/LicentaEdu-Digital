package com.utcn.edu_digital;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class EduDigitalApplicationTests {

	@Test
	void contextLoads() {
	}

}
