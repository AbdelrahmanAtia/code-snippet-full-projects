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
class FirstIntegrationTest extends MySqlTestBase {

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
	void create() {

		ProductEntity newEntity = new ProductEntity(2, "n", 2);
		repository.save(newEntity);

		ProductEntity foundEntity = repository.findById(newEntity.getId()).get();
		assertEqualsProduct(newEntity, foundEntity);

		assertEquals(2, repository.count());
	}

	private void assertEqualsProduct(ProductEntity expectedEntity, ProductEntity actualEntity) {
		assertEquals(expectedEntity.getId(), actualEntity.getId());
		assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
		assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
		assertEquals(expectedEntity.getName(), actualEntity.getName());
		assertEquals(expectedEntity.getWeight(), actualEntity.getWeight());
	}

}