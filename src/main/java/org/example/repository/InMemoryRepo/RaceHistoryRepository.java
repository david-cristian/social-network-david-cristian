package org.example.repository.InMemoryRepo;

import org.example.model.Event.RaceHistoryEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaceHistoryRepository {
    private final Map<String, RaceHistoryEntry> history = new HashMap<>();

    public void add(RaceHistoryEntry entry) {
        history.put(entry.getRaceName(), entry);
    }

    public RaceHistoryEntry findRace(String name){
        if (history.containsKey(name)){
            return history.get(name);
        }
        return null;
    }
    public List<RaceHistoryEntry> getAll() {
        return new ArrayList<>(history.values());
    }

    public void printAll() {
        for (RaceHistoryEntry entry: history.values()) {
            System.out.println(entry);
        }
    }
}
