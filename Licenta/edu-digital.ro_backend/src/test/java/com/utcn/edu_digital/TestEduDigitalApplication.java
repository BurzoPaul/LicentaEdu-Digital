package com.utcn.edu_digital;

import org.springframework.boot.SpringApplication;

public class TestEduDigitalApplication {

	public static void main(String[] args) {
		SpringApplication.from(EduDigitalApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
