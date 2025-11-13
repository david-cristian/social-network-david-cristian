package org.example.service;
import org.example.model.*;
import org.example.utils.UserException;
import org.example.utils.ValidationException;
import org.example.validation.DuckValidationStrategy;
import org.example.validation.PersonValidationStrategy;

import java.time.LocalDate;

public class ConcreteUserFactory implements UserFactory {
    @Override
    public User create(String type, String... args) {
        switch (type.toLowerCase()) {
            case "person" -> {
                Person p = new Person(
                        Long.parseLong(args[0]), //id
                        args[1], //username
                        args[2], //password
                        args[3], //email
                        args[4], //nume
                        args[5], //prenume
                        LocalDate.parse(args[6]), //data nasterii
                        args[7], //ocupatie
                        Integer.parseInt(args[8]) //empatie
                );
                p.setValidationStrategy(new PersonValidationStrategy());
                p.getValidationStrategy().validate(p);
                return p;
            }
            case "duck" -> {
                switch (args[4].toUpperCase()) {
                    case "FLYING" -> {
                        Duck d = new FlyingDuck(
                                Long.parseLong(args[0]), //id
                                args[1], //username
                                args[2], //password
                                args[3], //email
                                args[4].toUpperCase(), //tip
                                Double.parseDouble(args[5]), //viteza
                                Double.parseDouble(args[6])//rezistenta
                        );
                        d.setValidationStrategy(new DuckValidationStrategy());
                        d.getValidationStrategy().validate(d);
                        return d;
                    }
                    case "SWIMMING" -> {
                        Duck d = new SwimmingDuck(
                                Long.parseLong(args[0]), //id
                                args[1], //username
                                args[2], //password
                                args[3], //email
                                args[4].toUpperCase(), //tip
                                Double.parseDouble(args[5]), //viteza
                                Double.parseDouble(args[6])//rezistenta
                        );
                        d.setValidationStrategy(new DuckValidationStrategy());
                        d.getValidationStrategy().validate(d);
                        return d;
                    }
                    case "FLYING_AND_SWIMMING" -> {
                        Duck d = new FlyingAndSwimmingDuck(
                                Long.parseLong(args[0]), //id
                                args[1], //username
                                args[2], //password
                                args[3], //email
                                args[4].toUpperCase(), //tip
                                Double.parseDouble(args[5]), //viteza
                                Double.parseDouble(args[6])//rezistenta
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