package org.example.validation;

import org.example.model.Duck;
import org.example.model.DuckType;
import org.example.utils.ValidationException;
import org.example.model.User;

public class DuckValidationStrategy implements ValidationStrategy {
    public void validate(User user) throws ValidationException {
        if (!(user instanceof Duck duck))
            throw new ValidationException("Duck invalid");

        if (duck.getTip() == null ||
                (duck.getTip() != DuckType.FLYING && duck.getTip() != DuckType.SWIMMING && duck.getTip() != DuckType.FLYING_AND_SWIMMING))
            throw new ValidationException("Tip invalid");

        if (duck.getViteza() <= 0)
            throw new ValidationException("Viteza invalida");

        if (duck.getRezistenta() <= 0)
            throw new ValidationException("Rezistenta invalida");
    }
}
