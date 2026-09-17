package com.example.basa_prof.service;

import com.example.basa_prof.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с пользователями.
 */
public interface IUserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    User save(User user);
    void deleteById(Long id);
}
