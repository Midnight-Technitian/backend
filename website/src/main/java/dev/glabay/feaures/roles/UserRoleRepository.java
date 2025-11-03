package dev.glabay.feaures.roles;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByEmailIgnoreCase(String email);
}