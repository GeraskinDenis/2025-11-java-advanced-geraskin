package ru.geraskindenis.service;

import org.springframework.stereotype.Service;
import ru.geraskindenis.entity.User;
import ru.geraskindenis.repository.UserRepository;
import ru.geraskindenis.security.PasswordEncoderComponent;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoderComponent passwordEncoder;

    private static final List<byte[]> GARBAGE = new ArrayList<>();

    public UserServiceImpl(UserRepository userRepository, PasswordEncoderComponent passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(String login, String rawPassword) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = new User();
        user.setLogin(login);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        GARBAGE.add(new byte[1024 * 512]);

        return userRepository.save(user);
    }
}
