package ru.geraskindenis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.geraskindenis.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);
}
