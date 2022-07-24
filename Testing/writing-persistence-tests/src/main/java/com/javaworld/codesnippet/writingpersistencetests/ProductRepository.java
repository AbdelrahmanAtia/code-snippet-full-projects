package com.javaworld.codesnippet.writingpersistencetests;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<ProductEntity, Integer> {

	Optional<ProductEntity> findByProductId(int productId);

}