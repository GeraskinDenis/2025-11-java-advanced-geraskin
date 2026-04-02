package ru.geraskindenis.dto;

import ru.geraskindenis.entity.User;

public record UserResponse(Long id, String login, String uuid) {
    public static UserResponse fromUser(User user) {
        return new UserResponse(user.getId(), user.getLogin(), user.getUuid());
    }
}
