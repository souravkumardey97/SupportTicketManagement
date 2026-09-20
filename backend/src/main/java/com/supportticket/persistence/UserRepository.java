package com.supportticket.persistence;

import com.supportticket.domain.Role;
import com.supportticket.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    long countByRole(Role role);

    List<UserEntity> findByRole(Role role);
}
