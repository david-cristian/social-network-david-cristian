package org.example.model;

import org.example.utils.EvException;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private final List<Observer> subscribers = new ArrayList<>();

    public void subscribe(Observer observer) {
        if (subscribers.contains(observer))
            throw new EvException("User already subsribed");
        subscribers.add(observer);
    }

    public void unsubscribe(Observer observer) {
        if (!subscribers.contains(observer))
            throw new EvException("User not subscribed");
        subscribers.remove(observer);
    }

    public void notifySubscribers(String message) {
        for  (Observer observer : subscribers) {
            observer.update(message);
        }
    }
}
