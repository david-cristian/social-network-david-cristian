package org.example.service;
import org.example.model.Card;
import org.example.model.CardInotatori;
import org.example.model.CardZburatori;
import org.example.utils.CardException;

public class ConcreteCardFactory implements CardFactory {
    public Card create(String type, String... args) {
        switch (type.toLowerCase()) {
            case "zburatori" -> {
                CardZburatori c = new CardZburatori(
                        Long.parseLong(args[0]),
                        args[1]
                );
                return c;
            }
            case "inotatori" -> {
                CardInotatori c = new CardInotatori(
                        Long.parseLong(args[0]),
                        args[1]
                );
                return c;
            }
            default -> throw new CardException("Invalid flock type");
        }
    }
}
