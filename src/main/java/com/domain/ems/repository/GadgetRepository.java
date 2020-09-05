package com.domain.ems.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.domain.ems.models.electonics.Gadgets;


@Repository
public interface GadgetRepository extends CrudRepository<Gadgets, Long>{

}
