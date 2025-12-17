package org.example.service.Factory;
import org.example.model.Users.User;

public interface UserFactory {
    User create(String type, String username, String password, String email, String... args);
}
