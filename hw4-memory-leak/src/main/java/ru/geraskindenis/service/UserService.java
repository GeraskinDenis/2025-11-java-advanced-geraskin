package ru.geraskindenis.service;

import ru.geraskindenis.entity.User;

public interface UserService {
    User register(String login, String rawPassword);
}
