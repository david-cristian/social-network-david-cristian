package org.example.validation;

import org.example.model.User;
import org.example.utils.ValidationException;
import org.example.model.Person;

public class PersonValidationStrategy implements ValidationStrategy {
    public void validate(User user) throws ValidationException {
        if (!(user instanceof Person person))
            throw new ValidationException("User is not a Person");

        if (person.getNume() == null || person.getNume().isBlank() || person.getNume().contains(","))
            throw new ValidationException("Name is empty");

        if (person.getNivelEmpatie() < 0 || person.getNivelEmpatie() > 1000)
            throw new ValidationException("Empathy level must be between 0 and 1000");

        if (person.getEmail() == null || !person.getEmail().contains("@") || !person.getEmail().contains(".com") || person.getEmail().contains(","))
            throw new ValidationException("Email invalid");
    }
}
