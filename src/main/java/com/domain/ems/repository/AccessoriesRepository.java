package com.domain.ems.repository;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.domain.ems.models.electonics.Accessories;


@Repository
public interface AccessoriesRepository extends CrudRepository<Accessories, Long>{
	
	@Query("select a from Accessories a")
	Stream<Accessories> findAllAccQueryAndStream();
	
}
