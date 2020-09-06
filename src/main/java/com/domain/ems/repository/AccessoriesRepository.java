package com.domain.ems.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.domain.ems.models.electonics.Accessories;


@Repository
public interface AccessoriesRepository extends CrudRepository<Accessories, Long>{
	
}
