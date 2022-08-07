package com.javaworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SecondIntegrationTest extends MySqlTestBase {

	@Autowired
	private ProductRepository repository;

	private ProductEntity savedEntity;

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