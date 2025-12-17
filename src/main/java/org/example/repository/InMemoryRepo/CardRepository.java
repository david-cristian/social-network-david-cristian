package org.example.repository.InMemoryRepo;

import org.example.model.Carduri.Card;
import org.example.model.Users.Duck;
import org.example.utils.RepoException.CardRepoException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class CardRepository {
    private Map<String, Card> carduri;

    public CardRepository(Map<String, Card> carduri) {
        this.carduri = carduri;
    }

    public void save(Card card) {
        if (carduri.containsKey(card.getNume()))
            throw new CardRepoException("Flock already exists");
        carduri.put(card.getNume(), card);
    }

    public void deleteCard(Card card) {
        if (!carduri.containsKey(card.getNume()))
            throw new CardRepoException("Flock not found");
        carduri.remove(card.getNume());
    }

    public List<Card> getCarduri() {
        return new ArrayList<>(carduri.values());
    }

    public <T extends Duck> Card<T> findOneCard(String nume) {
        if (carduri.containsKey(nume))
            return carduri.get(nume);
        throw new CardRepoException("No flock found");
    }


}
