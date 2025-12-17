package org.example.service.Factory;
import org.example.model.Users.*;
import org.example.utils.ModelException.UserException;
import org.example.utils.ValidationException;
import org.example.validation.DuckValidationStrategy;
import org.example.validation.PersonValidationStrategy;

import java.time.LocalDate;

public class ConcreteUserFactory implements UserFactory {
    @Override
    public User create(String type, String username, String password, String email, String... args) {
        switch (type.toLowerCase()) {
            case "person" -> {
                Person p = new Person(
                        username, //username
                        password, //password
                        email, //email
                        args[0], //nume
                        args[1], //prenume
                        LocalDate.parse(args[2]), //data nasterii
                        args[3], //ocupatie
                        Integer.parseInt(args[4]) //empatie
                );
                p.setValidationStrategy(new PersonValidationStrategy());
                p.getValidationStrategy().validate(p);
                return p;
            }
            case "duck" -> {
                switch (args[0].toUpperCase()) {
                    case "FLYING" -> {
                        Duck d = new FlyingDuck(
                                username, //username
                                password, //password
                                email, //email
                                args[0].toUpperCase(), //tip
                                Double.parseDouble(args[1]), //viteza
                                Double.parseDouble(args[2])//rezistenta
                        );
                        d.setValidationStrategy(new DuckValidationStrategy());
                        d.getValidationStrategy().validate(d);
                        return d;
                    }
                    case "SWIMMING" -> {
                        Duck d = new SwimmingDuck(
                                username, //username
                                password, //password
                                email, //email
                                args[0].toUpperCase(), //tip
                                Double.parseDouble(args[1]), //viteza
                                Double.parseDouble(args[2])//rezistenta
                        );
                        d.setValidationStrategy(new DuckValidationStrategy());
                        d.getValidationStrategy().validate(d);
                        return d;
                    }
                    case "FLYING_AND_SWIMMING" -> {
                        Duck d = new FlyingAndSwimmingDuck(
                                username, //username
                                password, //password
                                email, //email
                                args[0].toUpperCase(), //tip
                                Double.parseDouble(args[1]), //viteza
                                Double.parseDouble(args[2])//rezistenta
                        );
                        d.setValidationStrategy(new DuckValidationStrategy());
                        d.getValidationStrategy().validate(d);
                        return d;
                    }
                    default -> throw new ValidationException("Invalid type");
                }
            }
            default -> throw new UserException("Invalid user type");
        }

    }
}