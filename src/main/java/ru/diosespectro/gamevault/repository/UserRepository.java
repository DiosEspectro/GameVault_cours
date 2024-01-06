package ru.diosespectro.gamevault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.diosespectro.gamevault.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}