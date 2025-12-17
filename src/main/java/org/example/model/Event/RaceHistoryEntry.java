package org.example.model.Event;

import java.util.List;

public class RaceHistoryEntry {
    private final String raceName;
    private final double resultTime;
    private final List<String> participantNames;
    private final boolean executed;

    public RaceHistoryEntry(String raceName, double resultTime, List<String> participantNames, boolean executed) {
        this.raceName = raceName;
        this.resultTime = resultTime;
        this.participantNames = participantNames;
        this.executed = executed;
    }

    public String getRaceName() {
        return raceName;
    }

    public double getResultTime() {
        return resultTime;
    }

    public List<String> getParticipantNames() {
        return participantNames;
    }

    public boolean isExecuted() {
        return executed;
    }

    @Override
    public String toString() {
        return "Race: " + raceName +
                " | Time: " + resultTime +
                " | Participants: " + String.join(", ", participantNames) +
                " | Executed: " + executed;
    }

}
