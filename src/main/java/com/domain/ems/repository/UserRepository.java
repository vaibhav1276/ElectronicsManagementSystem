package com.domain.ems.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.domain.ems.models.UserModel.User;


@Repository
public interface UserRepository extends CrudRepository<User, Long>{

    User findByUsername(String username);
	
}
