package org.example.service;

import org.example.model.Card;

public interface CardFactory {
    Card create(String type, String... args);
}
