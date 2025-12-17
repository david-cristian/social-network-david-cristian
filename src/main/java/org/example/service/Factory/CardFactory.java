package org.example.service.Factory;

import org.example.model.Carduri.Card;

public interface CardFactory {
    Card create(String type, String... args);
}
