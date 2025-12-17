package org.example.repository.DatabaseRepo;

import org.example.model.Users.*;
import org.example.utils.DBManager;
import org.example.utils.ModelException.UserException;
import org.example.utils.RepoException.DBRepoUserException;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class UserDBRepository {

    public Optional<User> findOne(String username) {
        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("select * from users where username = ?")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return Optional.empty();
            }
            Long id = resultSet.getLong("id");
            String email =  resultSet.getString("email");
            String password = resultSet.getString("password");

            try (PreparedStatement statement1 = connection.prepareStatement("select * from persons where id = ?")) {
                statement1.setLong(1, id);
                ResultSet resultSet1 = statement1.executeQuery();
                if (resultSet1.next()) {
                    String lastName = resultSet1.getString("last_name");
                    String firstName = resultSet1.getString("first_name");
                    LocalDate birthday = resultSet1.getDate("birthday").toLocalDate();
                    String occupation = resultSet1.getString("occupation");
                    int empathy = resultSet1.getInt("empathy");

                    Person person = new Person(username, password, email, firstName, lastName, birthday, occupation, empathy);
                    person.setId(id);
                    return Optional.of(person);
                }
            }

            try (PreparedStatement statement2 = connection.prepareStatement("select * from ducks where id = ?")) {
                statement2.setLong(1, id);
                ResultSet resultSet2 = statement2.executeQuery();
                if (resultSet2.next()) {
                    String duckType = resultSet2.getString("duck_type");
                    double speed = resultSet2.getDouble("speed");
                    double stamina = resultSet2.getDouble("stamina");

                    Duck duck;
                    switch (duckType) {
                        case "SWIMMING" -> duck = new SwimmingDuck(username, password, email, "SWIMMING", speed, stamina);
                        case "FLYING" -> duck = new FlyingDuck(username, password, email, "FLYING", speed, stamina);
                        case "FLYING_AND_SWIMMING" -> duck = new FlyingAndSwimmingDuck(username, password, email, "FLYING_AND_SWIMMING", speed, stamina);
                        default -> throw new DBRepoUserException("Invalid duck type in finding.");
                    }
                    duck.setId(id);
                    return Optional.of(duck);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DBRepoUserException(e.getMessage());
        }
    }

    public Optional<User> findOne(Long id) {
        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("select * from users where id = ?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return Optional.empty();
            }
            String username = resultSet.getString("username");
            String email =  resultSet.getString("email");
            String password = resultSet.getString("password");

            try (PreparedStatement statement1 = connection.prepareStatement("select * from persons where id = ?")) {
                statement1.setLong(1, id);
                ResultSet resultSet1 = statement1.executeQuery();
                if (resultSet1.next()) {
                    String lastName = resultSet1.getString("last_name");
                    String firstName = resultSet1.getString("first_name");
                    LocalDate birthday = resultSet1.getDate("birthday").toLocalDate();
                    String occupation = resultSet1.getString("occupation");
                    int empathy = resultSet1.getInt("empathy");

                    Person person = new Person(username, password, email, firstName, lastName, birthday, occupation, empathy);
                    person.setId(id);
                    return Optional.of(person);
                }
            }

            try (PreparedStatement statement2 = connection.prepareStatement("select * from ducks where id = ?")) {
                statement2.setLong(1, id);
                ResultSet resultSet2 = statement2.executeQuery();
                if (resultSet2.next()) {
                    String duckType = resultSet2.getString("duck_type");
                    double speed = resultSet2.getDouble("speed");
                    double stamina = resultSet2.getDouble("stamina");

                    Duck duck;
                    switch (duckType) {
                        case "SWIMMING" -> duck = new SwimmingDuck(username, password, email, "SWIMMING", speed, stamina);
                        case "FLYING" -> duck = new FlyingDuck(username, password, email, "FLYING", speed, stamina);
                        case "FLYING_AND_SWIMMING" -> duck = new FlyingAndSwimmingDuck(username, password, email, "FLYING_AND_SWIMMING", speed, stamina);
                        default -> throw new DBRepoUserException("Invalid duck type in finding.");
                    }
                    duck.setId(id);
                    return Optional.of(duck);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DBRepoUserException(e.getMessage());
        }
    }

    public List<User> findAll() {
        String findAllSQL = """
                select u.id, u.username, u.email, u.password,
                        p.last_name, p.first_name, p.birthday, p.occupation, p.empathy,
                        d.duck_type, d.speed, d.stamina
                from users u
                left join persons p on u.id = p.id
                left join ducks d on u.id = d.id
                """;
        List<User> users = new ArrayList<>();
        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(findAllSQL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                if (resultSet.getString("last_name") != null) {
                    String lastName = resultSet.getString("last_name");
                    String firstName = resultSet.getString("first_name");
                    Date birthday = resultSet.getDate("birthday");
                    String occupation = resultSet.getString("occupation");
                    int empathy = resultSet.getInt("empathy");
                    Person person = new Person(username, password, email, lastName, firstName, birthday.toLocalDate(), occupation, empathy);
                    person.setId(id);
                    users.add(person);
                } else if (resultSet.getString("duck_type") != null) {
                    String duck_type = resultSet.getString("duck_type");
                    Double speed = resultSet.getDouble("speed");
                    Double stamina = resultSet.getDouble("stamina");
                    Duck duck;
                    switch (duck_type) {
                        case "SWIMMING" -> duck = new SwimmingDuck(username, password, email, "SWIMMING", speed, stamina);
                        case "FLYING" -> duck = new FlyingDuck(username, password, email, "FLYING", speed, stamina);
                        case "FLYING_AND_SWIMMING" -> duck = new FlyingAndSwimmingDuck(username, password, email, "FLYING_AND_SWIMMING", speed, stamina);
                        default ->  throw new DBRepoUserException("Invalid duck type in finding.");
                    }
                    duck.setId(id);
                    users.add(duck);
                }
            }
            return users;
        } catch (SQLException e) {
            throw new DBRepoUserException(e.getMessage());
        }
    }

    public Page<User> findAllPage(Pageable pageable) {
        List<User> users = new ArrayList<>();
        int limit = pageable.getPageSize();
        int offset = pageable.getPageNumber() * limit;

        String sqlData = """
            select u.id, u.username, u.email, u.password,
                        p.last_name, p.first_name, p.birthday, p.occupation, p.empathy,
                        d.duck_type, d.speed, d.stamina
                from users u
                left join persons p on u.id = p.id
                left join ducks d on u.id = d.id
                limit ? offset ?
        """;

        String sqlCount = "select count(*) as count from users";

        try (Connection connection = DBManager.getConnection()) {
            int totalCount = 0;
            try (PreparedStatement psCount = connection.prepareStatement(sqlCount);
            ResultSet rsCount = psCount.executeQuery()) {
                if (rsCount.next())
                    totalCount = rsCount.getInt("count");
            }
            try (PreparedStatement ps = connection.prepareStatement(sqlData)) {
                ps.setInt(1, limit);
                ps.setInt(2, offset);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Long id = rs.getLong("id");
                        String username = rs.getString("username");
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        if (rs.getString("last_name") != null) {
                            String lastName = rs.getString("last_name");
                            String firstName = rs.getString("first_name");
                            Date birthday = rs.getDate("birthday");
                            String occupation = rs.getString("occupation");
                            int empathy = rs.getInt("empathy");
                            Person person = new Person(username, password, email, lastName, firstName, birthday.toLocalDate(), occupation, empathy);
                            person.setId(id);
                            users.add(person);
                        } else if (rs.getString("duck_type") != null) {
                            String duck_type = rs.getString("duck_type");
                            Double speed = rs.getDouble("speed");
                            Double stamina = rs.getDouble("stamina");
                            Duck duck;
                            switch (duck_type) {
                                case "SWIMMING" ->
                                        duck = new SwimmingDuck(username, password, email, "SWIMMING", speed, stamina);
                                case "FLYING" ->
                                        duck = new FlyingDuck(username, password, email, "FLYING", speed, stamina);
                                case "FLYING_AND_SWIMMING" ->
                                        duck = new FlyingAndSwimmingDuck(username, password, email, "FLYING_AND_SWIMMING", speed, stamina);
                                default -> throw new DBRepoUserException("Invalid duck type in finding.");
                            }
                            duck.setId(id);
                            users.add(duck);
                        }
                    }
                }
            }
            return new Page<>(users, totalCount);
        } catch (SQLException e) {
            throw new DBRepoUserException(e.getMessage());
        }
    }

    public Optional<User> save(User user) {
        String insertUserSQL = "insert into users (username, email, password) values (?, ?, ?) returning id";
        String insertDuckSQL = "insert into ducks (id, duck_type, speed, stamina) values (?, ?, ?, ?)";
        String insertPersonSQL = "insert into persons (id, last_name, first_name, birthday, occupation, empathy) values (?, ?, ?, ?, ?, ?)";

        long generatedId = -1;
        Connection connection = null;

        try {
            connection = DBManager.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(insertUserSQL)) {
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getPassword());

                var res = statement.executeQuery();

                if (res.next()) {
                    generatedId = res.getLong("id");
                    user.setId(generatedId);
                } else {
                    connection.rollback();
                    return Optional.empty();
                }
            }

            if (user instanceof Person p) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertPersonSQL)) {
                    preparedStatement.setLong(1, generatedId);
                    preparedStatement.setString(2, p.getNume());
                    preparedStatement.setString(3, p.getPrenume());
                    preparedStatement.setDate(4, Date.valueOf(p.getDataNasterii()));
                    preparedStatement.setString(5, p.getOcupatie());
                    preparedStatement.setInt(6, p.getNivelEmpatie());
                    preparedStatement.executeUpdate();
                }
            } else if (user instanceof Duck duck) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertDuckSQL)) {
                    preparedStatement.setLong(1, generatedId);
                    preparedStatement.setString(2, duck.getTip().toString());
                    preparedStatement.setDouble(3, duck.getViteza());
                    preparedStatement.setDouble(4, duck.getRezistenta());
                    preparedStatement.executeUpdate();
                }
            }
            connection.commit();
            return Optional.of(user);
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

    public Optional<User> delete(String username) {
        if (username.isEmpty())
            throw new DBRepoUserException("Username cannot be null");
        String deleteSQL = "delete from users where username = ?";
        try (var connection = DBManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
            statement.setString(1, username);
            Optional<User> userToDelete = findOne(username);
            int response = 0;
            if (userToDelete.isPresent()) {
                response = statement.executeUpdate();
            }
            return  response == 0 ? Optional.empty() : userToDelete;
        } catch (SQLException e) {
            throw new DBRepoUserException(e.getMessage());
        }
    }

    public List<Duck> getDucks() {
        String selectDucksSQL = """
                select u.id, u.username, u.email, u.password,
                        d.duck_type, d.speed, d.stamina
                from users u
                inner join ducks d on u.id = d.id
                """;

        List<Duck> ducks = new ArrayList<>();

        try (var connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectDucksSQL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                String duckType = resultSet.getString("duck_type");
                Double speed = resultSet.getDouble("speed");
                Double stamina = resultSet.getDouble("stamina");

                Duck duck;
                switch (duckType) {
                    case "SWIMMING" -> duck = new SwimmingDuck(username, password, email, "SWIMMING", speed, stamina);
                    case "FLYING" -> duck = new FlyingDuck(username, password, email, "FLYING", speed, stamina);
                    case "FLYING_AND_SWIMMING" ->
                            duck = new FlyingAndSwimmingDuck(username, password, email, "FLYING_AND_SWIMMING", speed, stamina);
                    default -> throw new DBRepoUserException("Unexpected value in finding ducks: " + duckType);
                }
                duck.setId(id);
                ducks.add(duck);
            }
            return ducks;
        } catch (SQLException e) {
            throw new DBRepoUserException(e.getMessage());
        }
    }
}

