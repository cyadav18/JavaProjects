package org.guidewire.login.repository;

import org.guidewire.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    // Option 1: Using @Param annotation to explicitly name the parameter
    @Query("SELECT DISTINCT u FROM User u WHERE u.id IN :id")
    Optional<User> findAllByUserId(@Param("id") List<UUID> id);


    Optional<User> findByEmail(String email);
}
