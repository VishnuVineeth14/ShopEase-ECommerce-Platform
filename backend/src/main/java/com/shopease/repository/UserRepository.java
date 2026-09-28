package com.shopease.repository;

import com.shopease.model.Role;
import com.shopease.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

    long countByRole(Role role);
}
