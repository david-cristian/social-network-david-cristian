package org.example.model.Event;

import org.example.model.Users.Duck;
import org.example.utils.ModelException.RaceEventException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RaceEvent extends Event {
    private long id = -1;
    private final String nume;
    private List<Duck> ducks;
    private List<Double> distances;
    private int M;
    private List<Duck> selectedDucks = new ArrayList<>();
    private double raceResult = 0;
    private boolean executed = false;
    private LocalDateTime createdAt;

    public RaceEvent(String nume, List<Duck> ducks, List<Double> distances, int m) {
        this.nume = nume;
        this.ducks = ducks;
        this.distances = distances;
        this.M = m;
        selectParticipants();
    }

    public RaceEvent(long id, String nume, List<Duck> ducks, List<Double> distances, int m, LocalDateTime createdAt, boolean executed, double raceResult) {
        this.id = id;
        this.nume = nume;
        this.selectedDucks = ducks;
        this.M = m;
        this.distances = distances;
        this.createdAt = createdAt;
        this.raceResult = raceResult;
        this.executed = true;
    }

    public long getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    public List<Double> getDistances() {
        return distances;
    }

    public int getM() {
        return M;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void removeDuck(Duck duck) {
            selectedDucks.remove(duck);
    }

    public List<Duck> getSelectedDucks() {
        return selectedDucks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getResult() {
        return raceResult;
    }

    public void setResult(double raceResult) {
        this.raceResult = raceResult;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean validateEvent() {
        return !executed && distances != null && !distances.isEmpty() && selectedDucks.size() == M;
    }

    public void selectParticipants() {
        List<Duck> sorted = new ArrayList<>(ducks);
        sorted.sort(Comparator.comparingDouble(Duck::getViteza).reversed());

        List<Duck> temp = new ArrayList<>();
        for (Duck duck : sorted) {
            if (temp.isEmpty() || duck.getRezistenta() <= temp.get(temp.size() - 1).getRezistenta()) {
                temp.add(duck);
                if (temp.size() == M)
                    break;
            }
        }

        if (temp.size() < M)
            throw new RaceEventException("Too few ducks for this event");

        this.selectedDucks = temp;
    }

    public double calculateRaceTime(){
        double maxTime = 0;
        for (Duck duck : selectedDucks) {
            for (double d :  distances) {
                double time = 2 * d / duck.getViteza();
                if (time > maxTime)
                    maxTime = time;
            }
        }
        return maxTime;
    }

    public void startRace() {
        if (!validateEvent())
            throw new RaceEventException("Race not valid");
        double time = calculateRaceTime();
        raceResult = time;
        executed = true;
        String message = "Race finished! Time: " + time + " seconds with " + selectedDucks.size() + " ducks.";
        notifySubscribers(message);
    }

    @Override
    public String toString() {
        return "Race: \n" +
                "\nNume: " + nume +
                "\nDistances: " + distances.stream().map(String::valueOf).collect(Collectors.joining(", ")) +
                "\nNumber of lanes: " + M +
                "\nParticipants: " + selectedDucks.stream().map(String::valueOf).collect(Collectors.joining(", ")) +
                "\nExecuted: " + executed;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RaceEvent raceEvent)) return false;
        return Objects.equals(nume, raceEvent.nume);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nume);
    }
}
