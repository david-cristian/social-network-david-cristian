package org.example.repository.DatabaseRepo;

import org.example.model.Event.RaceEvent;
import org.example.model.Users.*;
import org.example.utils.DBManager;
import org.example.utils.ModelException.RaceEventException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RaceEventDBRepository {

    public Optional<RaceEvent> save(RaceEvent raceEvent) {
        String insertSQL = "insert into race_events (name, M, result, executed) values (?, ?, ?, ?)";
        String insertDistanceSQL = "insert into distances (race_id, distance) values (?, ?)";
        String insertDuckSQL = "insert into selected_ducks (race_id, duck_id) values (?, ?)";

        Connection connection = null;
        try {
            connection = DBManager.getConnection();
            connection.setAutoCommit(false);
            long generatedId = -1;

            try (PreparedStatement eventStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                eventStatement.setString(1, raceEvent.getNume());
                eventStatement.setInt(2, raceEvent.getM());
                eventStatement.setDouble(3, raceEvent.getResult());
                eventStatement.setBoolean(4, raceEvent.isExecuted());
                int affectedRows = eventStatement.executeUpdate();
                if (affectedRows == 0)
                    throw new RaceEventException("Race event insert failed");
                try (ResultSet rs = eventStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getLong(1);
                    } else
                        throw new RaceEventException("Race event insert failed (no ID)");
                }
            }
            if (generatedId == -1)
                throw new RaceEventException("ID still -1.");

            try (PreparedStatement distStatement = connection.prepareStatement(insertDistanceSQL)) {
                for (Double dist : raceEvent.getDistances()) {
                    distStatement.setLong(1, generatedId);
                    distStatement.setDouble(2, dist);
                    distStatement.addBatch();
                }
                distStatement.executeBatch();
            }
            try (PreparedStatement duckStatement = connection.prepareStatement(insertDuckSQL)) {
                for (var duck : raceEvent.getSelectedDucks()) {
                    duckStatement.setLong(1, generatedId);
                    duckStatement.setLong(2, duck.getId());
                    duckStatement.addBatch();
                }
                duckStatement.executeBatch();
            }
            connection.commit();

            raceEvent.setId(generatedId);
            return Optional.of(raceEvent);

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Optional<RaceEvent> findOne(String name) {
        String selectSQL = "select * from race_events where name = ?";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectSQL)) {
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                long id = rs.getLong("id");
                int M = rs.getInt("M");
                double result = rs.getDouble("result");
                boolean executed = rs.getBoolean("executed");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

                List<Double> distances = getDistancesForRace(id, connection);
                List<Duck> ducks;

                if (executed) {
                    ducks = getHistoryParticipants(id, connection);
                } else {
                    ducks = getDucksForRace(id, connection);
                }

                RaceEvent event = new RaceEvent(id, name, ducks, distances, M, createdAt, executed, result);

                loadSubscribers(event, connection);

                return Optional.of(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<RaceEvent> findAll() {
        String selectSQL = "select * from race_events";
        List<RaceEvent> raceEvents = new ArrayList<>();

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectSQL);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong("id");
                String nume = rs.getString("name");
                int M = rs.getInt("M");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                Boolean executed = rs.getBoolean("executed");
                Double result = rs.getDouble("result");

                List<Double> distances = getDistancesForRace(id, connection);
                List<Duck> ducks;

                if (executed) {
                    ducks = getHistoryParticipants(id, connection);
                } else {
                    ducks = getDucksForRace(id, connection);
                }

                RaceEvent raceEvent = new RaceEvent(id, nume, ducks, distances, M, createdAt, executed, result);

                loadSubscribers(raceEvent, connection);

                raceEvents.add(raceEvent);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return raceEvents;
    }

    public boolean delete(Long id) {
        String deleteSQL = "delete from race_events where id = ?";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //helpers
    private List<Double> getDistancesForRace(long id, Connection connection) throws SQLException {
        List<Double> distances = new ArrayList<>();
        String selectSQL = "select distance from distances where race_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    distances.add(rs.getDouble("distance"));
                }
            }
        }
        return distances;
    }

    private List<Duck> getDucksForRace(long id, Connection connection) throws SQLException {
        List<Duck> ducks = new ArrayList<>();
        String selectSQL = """
                select u.id, u.username, u.email, u.password, 
                       d.duck_type, d.speed, d.stamina
                from users u
                join selected_ducks sd on u.id = sd.duck_id
                join ducks d on u.id = d.id
                where sd.race_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    long duckId = rs.getLong("id");
                    String username = rs.getString("username");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    String duckType = rs.getString("duck_type");
                    double speed = rs.getDouble("speed");
                    double stamina = rs.getDouble("stamina");

                    if (duckType == null) duckType = "SWIMMING"; // Default safe

                    Duck duck;
                    switch (duckType) {
                        case "SWIMMING" -> duck = new SwimmingDuck(username, password, email, "SWIMMING", speed, stamina);
                        case "FLYING" -> duck = new FlyingDuck(username, password, email, "FLYING", speed, stamina);
                        case "FLYING_AND_SWIMMING" -> duck = new FlyingAndSwimmingDuck(username, password, email, "FLYING_AND_SWIMMING", speed, stamina);
                        default -> throw new RaceEventException("Invalid duck type in getting all ducks.");
                    }
                    duck.setId(duckId);
                    ducks.add(duck);
                }
                return ducks;
            }
        }
    }

    public void finalizeRace(RaceEvent event) {
        String updateEventSQL = "update race_events set result = ?, executed = ? where id = ?";
        String insertHistorySQL = "insert into race_history_participants (race_id, duck_name, duck_speed, duck_stamina, duck_type) values (?, ?, ?, ?, ?)";

        Connection connection = null;
        try {
            connection = DBManager.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement updateStatement = connection.prepareStatement(updateEventSQL)) {
                updateStatement.setDouble(1, event.getResult());
                updateStatement.setBoolean(2, true);
                updateStatement.setLong(3, event.getId());
                updateStatement.executeUpdate();
            }

            try (PreparedStatement historyStatement = connection.prepareStatement(insertHistorySQL)) {
                for (Duck duck : event.getSelectedDucks()) {
                    historyStatement.setLong(1, event.getId());
                    historyStatement.setString(2, duck.getUsername());
                    historyStatement.setDouble(3, duck.getViteza());
                    historyStatement.setDouble(4, duck.getRezistenta());
                    historyStatement.setString(5, duck.getTip().toString());
                    historyStatement.addBatch();
                }
                historyStatement.executeBatch();
            }
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Nu s-a putut salva istoricul cursei: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<Duck> getHistoryParticipants(long id, Connection connection) throws SQLException {
        List<Duck> ducks = new ArrayList<>();
        String selectSQL = "select duck_name, duck_type, duck_speed, duck_stamina from race_history_participants where race_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String duckName = rs.getString("duck_name");
                    String duckType = rs.getString("duck_type");
                    double speed = rs.getDouble("duck_speed");
                    double stamina = rs.getDouble("duck_stamina");

                    if (duckType == null) {
                        duckType = "SWIMMING";
                    }

                    Duck duck;
                    switch (duckType) {
                        case "SWIMMING" -> duck = new SwimmingDuck(duckName, "password", "history@gmail.com", "SWIMMING", speed, stamina);
                        case "FLYING" -> duck = new FlyingDuck(duckName, "password", "history@gmail.com", "FLYING", speed, stamina);
                        case "FLYING_AND_SWIMMING" -> duck = new FlyingAndSwimmingDuck(duckName, "password", "history@gmail.com", "FLYING_AND_SWIMMING", speed, stamina);
                        default -> duck = new SwimmingDuck(duckName, "password", "history@gmail.com", "SWIMMING", speed, stamina);
                    }
                    duck.setId(0L);
                    ducks.add(duck);
                }
            }
        }
        return ducks;
    }

    public void subscribe(String username, String raceName) {
        String insertSubSQL = """
                insert into subscribers (event_id, user_id)
                values
                ((select id from race_events where name = ?),
                (select id from users where username = ?))
                """;
        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSubSQL)) {
            statement.setString(1, raceName);
            statement.setString(2, username);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RaceEventException("Subscriber insertion failed: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("DB Error: " + e.getMessage());
        }
    }

    public void unsubscribe(String username, String raceName) {
        String deleteSubSQL = """
                delete from subscribers where event_id = (select id from race_events where name = ?) AND
                user_id = (select id from users where username = ?) 
        """;
        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteSubSQL)) {
            statement.setString(1, raceName);
            statement.setString(2, username);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RaceEventException("Unsubscribe failed: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RaceEventException("Database error during unsubscribe");
        }
    }

    private void loadSubscribers(RaceEvent event, Connection connection) throws SQLException {
        String selectSQL = """
                select u.id, u.username, u.email
                from users u
                join subscribers s on u.id = s.user_id
                where s.event_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
            statement.setLong(1, event.getId());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    String username = rs.getString("username");
                    String email = rs.getString("email");
                    User subscriber = new User(username, "placeholder", email) {};
                    subscriber.setId(id);
                    try {
                        if(!event.getSubscribers().contains(subscriber)) {
                            event.subscribe(subscriber);
                        }
                    } catch (Exception e) {}
                }
            }
        }
    }
}