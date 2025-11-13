package org.example.service;
import org.example.model.User;

public interface UserFactory {
    User create(String type, String... args);
}
