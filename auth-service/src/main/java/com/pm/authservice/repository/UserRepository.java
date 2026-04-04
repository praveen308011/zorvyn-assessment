package com.pm.authservice.repository;

import com.pm.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("""
SELECT u FROM User u JOIN FETCH u.roles r JOIN FETCH r.permissions WHERE u.email = :email
""")
    Optional<User> findByEmailWithRolesAndPermissions(String email);

    Optional<User> findByEmail(String email);
}
