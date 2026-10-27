package com.ejelaboral.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /** Búsqueda insensible a mayúsculas (FR-4, US2-AC3). */
    Optional<User> findByEmailIgnoreCase(String email);

    /** Unicidad de email case-insensitive (FR-4, US1-AC2). */
    boolean existsByEmailIgnoreCase(String email);
}
