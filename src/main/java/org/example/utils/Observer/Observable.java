package org.example.utils.Observer;
import org.example.utils.Events.Event;

public interface Observable <E extends Event> {
    void addObserver(Observer<E> observer);
    void removeObserver(Observer<E> observer);
    void notifyObservers(E t);
}
