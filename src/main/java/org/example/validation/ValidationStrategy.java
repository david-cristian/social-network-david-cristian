package org.example.validation;

import org.example.model.Users.User;
import org.example.utils.ValidationException;

public interface ValidationStrategy {
    void validate(User user) throws ValidationException;
}
