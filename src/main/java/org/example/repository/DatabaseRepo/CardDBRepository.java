package org.example.repository.DatabaseRepo;

import org.example.model.Carduri.Card;
import org.example.model.Carduri.CardInotatori;
import org.example.model.Carduri.CardInotatoriSiZburatori;
import org.example.model.Carduri.CardZburatori;
import org.example.model.Users.Duck;
import org.example.model.Users.FlyingAndSwimmingDuck;
import org.example.model.Users.FlyingDuck;
import org.example.model.Users.SwimmingDuck;
import org.example.utils.RepoException.DBRepoCardException;
import org.example.utils.DBManager;
import java.sql.*;
import java.util.*;

public class CardDBRepository {

    public Optional<Card> save(Card card) {
        String insertSQL = "insert into carduri (name, card_type) values (?, ?)";
        String insertMemberSQL = "insert into card_members (card_id, duck_id) values (?, ?)";

        Connection connection = null;
        try {
            connection = DBManager.getConnection();
            connection.setAutoCommit(false);
            long generatedCardId = -1;

            try (PreparedStatement statement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, card.getNume());

                if (card instanceof CardInotatori)
                    statement.setString(2, "inotatori");
                else if (card instanceof CardZburatori)
                    statement.setString(2, "zburatori");
                else if (card instanceof CardInotatoriSiZburatori)
                    statement.setString(2, "inotatori_si_zburatori");

                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating card failed, no rows affected.");
                }

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedCardId = generatedKeys.getLong(1);
                    } else {
                        throw new SQLException("Creating card failed, no ID obtained.");
                    }
                }
            }

            try (PreparedStatement statementMember = connection.prepareStatement(insertMemberSQL)) {
                for (Object d : card.getMembri()) {
                    if (d instanceof Duck duck) {
                        statementMember.setLong(1, generatedCardId);
                        statementMember.setLong(2, duck.getId());
                        statementMember.addBatch();
                    }
                }
                statementMember.executeBatch();
            }

            connection.commit();
            return Optional.of(card);

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

    public Optional<Card> findOne(String username) {
        try (var connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("select * from carduri where name = ?")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String cardType = resultSet.getString("card_type");
                if (cardType.equals("inotatori")) {
                    CardInotatori cardInotatori = new CardInotatori(username);
                    cardInotatori.setId(id);
                    return Optional.of(cardInotatori);
                } else if (cardType.equals("zburatori")) {
                    CardZburatori cardZburatori = new CardZburatori(username);
                    cardZburatori.setId(id);
                    return Optional.of(cardZburatori);
                } else if (cardType.equals("inotator_si_zburatori")) {
                    CardInotatoriSiZburatori cardInotatoriSiZburatori = new CardInotatoriSiZburatori(username);
                    cardInotatoriSiZburatori.setId(id);
                    return Optional.of(cardInotatoriSiZburatori);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DBRepoCardException(e.getMessage());
        }
    }

    public Optional<Card> delete(String username) {
        if (username.isEmpty())
            throw new DBRepoCardException("Username is empty");
        String deleteSQL = "delete from carduri where name = ?";
        try (var connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
            statement.setString(1, username);
            Optional<Card> cardToDelete = findOne(username);
            int response = 0;
            if (cardToDelete.isPresent()) {
                response = statement.executeUpdate();
            }
            return response == 0 ? Optional.empty() : cardToDelete;
        } catch (SQLException e) {
            throw new DBRepoCardException(e.getMessage());
        }
    }

    public List<Card<? extends Duck>> findAll() {
        String findAllSQL = """
                   select c.id as card_id, c.name as card_name, c.card_type,
                          u.id as user_id, u.username, u.email, u.password,
                          d.id, d.duck_type, d.speed, d.stamina
                   from carduri c
                   left join card_members cm on c.id = cm.card_id
                   left join users u on cm.duck_id = u.id
                   left join ducks d on d.id = u.id
                   order by c.id
                """;
        Map<Long, Card<? extends Duck>> cardMap = new HashMap<>();

        try (var connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(findAllSQL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long cardId = resultSet.getLong("card_id");
                String cardName = resultSet.getString("card_name");
                String cardType = resultSet.getString("card_type");

                Card<? extends Duck> card = cardMap.computeIfAbsent(cardId, id -> switch (cardType) {
                        case "inotatori" -> new CardInotatori(cardName);
                        case "zburatori" -> new CardZburatori(cardName);
                        case "inotatori_si_zburatori" -> new CardInotatoriSiZburatori(cardName);
                        default -> throw new DBRepoCardException("Invalid card type");
                    });
                card.setId(cardId);
                Long userId = resultSet.getLong("user_id");
                if (!resultSet.wasNull()){
                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    String duckType = resultSet.getString("duck_type");
                    Double speed = resultSet.getDouble("speed");
                    Double stamina = resultSet.getDouble("stamina");
                    Duck duck;
                    switch (duckType) {
                        case "SWIMMING" -> duck = new SwimmingDuck(username, password, email, "SWIMMING", speed, stamina);
                        case "FLYING" -> duck = new FlyingDuck(username, password, email, "FLYING", speed, stamina);
                        case "FLYING_AND_SWIMMING" -> duck = new FlyingAndSwimmingDuck(username, password, email, "FLYING_AND_SWIMMING", speed, stamina);
                        default -> throw new DBRepoCardException("Invalid duck type in finding all ducks.");
                    }
                    duck.setId(userId);
                    Card<Duck> cardCasted = (Card<Duck>) card;
                    cardCasted.adaugaMembru(duck);
                }

            }
            return new ArrayList<>(cardMap.values());
        } catch (SQLException e) {
            throw new DBRepoCardException(e.getMessage());
        }
    }

    public void adaugaMembru(String cardName, String username){
        String insertMemberSQL = """
            insert into card_members (card_id, duck_id)
            values (
                    (select id from carduri where name = ?),
                    (select id from users where username = ?)
            )
        """;

        try (var connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(insertMemberSQL)) {
            preparedStatement.setString(1, cardName);
            preparedStatement.setString(2, username);

            int rows = preparedStatement.executeUpdate();
            if (rows == 0)
                throw new DBRepoCardException("Add failed. User or flock don't exist.");
        } catch (SQLException e) {
            throw new DBRepoCardException(e.getMessage());
        }
    }

    public void removeMembru(String cardName, String username){
        String removeMembruSQL = """
                delete from card_members 
                where card_id = (select id from carduri where name = ?)
                and duck_id = (select id from users u where username = ?)
                """;

        try (var connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(removeMembruSQL)) {
            preparedStatement.setString(1, cardName);
            preparedStatement.setString(2, username);

            int rows = preparedStatement.executeUpdate();
            if (rows == 0)
                throw new DBRepoCardException("Duck with username " + username + " not found or invalid data");
        } catch (SQLException e) {
            throw new DBRepoCardException(e.getMessage());
        }
    }

    public List<String> getMembersDTO(String username){
        String selectMemberSQL = """
                select u.username as duck_username
                from carduri c
                join card_members cm on c.id = cm.card_id
                join users u on cm.duck_id = u.id
                where c.name = ?
        """;
        List<String> members = new ArrayList<>();
        try (var connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(selectMemberSQL)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                members.add(resultSet.getString("duck_username"));
            }
            return members;
        } catch (SQLException e) {
            throw new DBRepoCardException(e.getMessage());
        }
    }
}
