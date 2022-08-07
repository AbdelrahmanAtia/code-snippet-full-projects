package com.javaworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SecondIntegrationTest {

	@Container
	private static MySQLContainer database = new MySQLContainer("mysql:5.7.32");

	@Autowired
	private ProductRepository repository;

	private ProductEntity savedEntity;

	/**
	 * this function overrides any properties defined in the application.properties
	 * file..we replace those properties with the properties from the container
	 */
	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		
		registry.add("spring.datasource.url", database::getJdbcUrl);
		registry.add("spring.datasource.username", database::getUsername);
		registry.add("spring.datasource.password", database::getPassword);
		
	}

	@BeforeEach
	void setupDb() {
		repository.deleteAll();

		ProductEntity entity = new ProductEntity(1, "n", 1);
		savedEntity = repository.save(entity);

		assertEqualsProduct(entity, savedEntity);
	}

	@Test
	void update() {
		savedEntity.setName("n2");
		repository.save(savedEntity);

		ProductEntity foundEntity = repository.findById(savedEntity.getId()).get();
		assertEquals(1, (long) foundEntity.getVersion());
		assertEquals("n2", foundEntity.getName());
	}

	private void assertEqualsProduct(ProductEntity expectedEntity, ProductEntity actualEntity) {
		assertEquals(expectedEntity.getId(), actualEntity.getId());
		assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
		assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
		assertEquals(expectedEntity.getName(), actualEntity.getName());
		assertEquals(expectedEntity.getWeight(), actualEntity.getWeight());
	}

}