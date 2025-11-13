package org.example.repository;

import org.example.model.RaceEvent;
import org.example.utils.RaceEventException;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class RaceEventRepository {
    private final Map<String, RaceEvent> raceEvents = new HashMap<>();

    public void addRaceEvent(RaceEvent raceEvent) {
        if (raceEvents.containsKey(raceEvent.getNume())) {
            throw new RaceEventException("Race already exists");
        }
        raceEvents.put(raceEvent.getNume(), raceEvent);
    }

    public RaceEvent findRaceEvent(String nume) {
        if (!raceEvents.containsKey(nume)) {
            throw new RaceEventException("Race does not exist");
        }
        return raceEvents.get(nume);
    }

    public void deleteRaceEvent(String name) {
        raceEvents.remove(name);
    }

    public List<RaceEvent> getRaceEvents() {
        return new ArrayList<>(raceEvents.values());
    }

}
