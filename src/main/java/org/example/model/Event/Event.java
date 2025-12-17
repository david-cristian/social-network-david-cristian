package org.example.model.Event;

import org.example.model.Users.User;
import org.example.utils.ModelException.EvException;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private final List<Observer> subscribers = new ArrayList<>();

    public void subscribe(Observer observer) {
        for (Observer obs : subscribers) {
            System.out.println(obs);
        }
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

    public List<Observer> getSubscribers() {
        return subscribers;
    }
}
