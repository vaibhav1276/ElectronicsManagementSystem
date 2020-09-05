package com.domain.ems.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.domain.ems.models.User;


@Repository
public interface UserRepository extends CrudRepository<User, Long>{

    User findByUsername(String username);
	
}
