package org.example.service.Factory;
import org.example.model.Carduri.Card;
import org.example.model.Carduri.CardInotatori;
import org.example.model.Carduri.CardInotatoriSiZburatori;
import org.example.model.Carduri.CardZburatori;
import org.example.utils.ModelException.CardException;

public class ConcreteCardFactory implements CardFactory {
    public Card create(String type, String... args) {
        switch (type.toLowerCase()) {
            case "zburatori" -> {
                return new CardZburatori(args[0]);
            }
            case "inotatori" -> {
                return new CardInotatori(args[0]);
            }
            case "inotatori_si_zburatori" -> {
                return new CardInotatoriSiZburatori(args[0]);
            }
            default -> throw new CardException("Invalid flock type");
        }
    }
}
