package com.javaworld;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

public abstract class MySqlTestBase {

	private static MySQLContainer database = new MySQLContainer("mysql:5.7.32");

	static {
		database.start();
	}

	/**
	 * this function overrides any properties defined in the application.properties
	 * file..we replace those properties with the properties from the container
	 */
	@DynamicPropertySource
	static void databaseProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", database::getJdbcUrl);
		registry.add("spring.datasource.username", database::getUsername);
		registry.add("spring.datasource.password", database::getPassword);
	}

}