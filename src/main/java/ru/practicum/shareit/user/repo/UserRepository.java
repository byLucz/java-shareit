package ru.practicum.shareit.user.repo;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.UserServiceException;
import ru.practicum.shareit.user.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    public User save(User user) {
        if (user.getId() == null) {
            if (emailExists(user.getEmail())) {
                throw new UserServiceException("Такая почта уже существует");
            }
            user.setId(idCounter++);
        } else {
            User existingUser = findById(user.getId());
            if (existingUser != null && !existingUser.getEmail().equals(user.getEmail()) && emailExists(user.getEmail())) {
                throw new UserServiceException("Такая почта уже существует");
            }
        }
        users.put(user.getId(), user);
        return user;
    }

    public User findById(Integer userId) {
        return users.get(userId);
    }

    public void deleteById(Integer userId) {
        users.remove(userId);
    }

    public List<User> findAll() {
        return users.values().stream().collect(Collectors.toList());
    }

    private boolean emailExists(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
