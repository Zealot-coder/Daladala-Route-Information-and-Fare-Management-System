package com.daladala.repository;

import com.daladala.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Find admin by username — used in the login check
    // Optional: safely returns empty instead of null if username not found
    Optional<Admin> findByUsername(String username);
}
