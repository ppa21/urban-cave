package com.urbancave.repository;

import com.urbancave.domain.Role;
import com.urbancave.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(Role role);
}
