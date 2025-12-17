package org.example.repository.DatabaseRepo;

import org.example.model.Friendhsip.Friendship;
import org.example.utils.DBManager;
import org.example.utils.RepoException.DBFriendshipRepoException;
import org.example.model.Friendhsip.FriendshipDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FriendshipDBRepository {

    public Optional<Friendship> save(String username1, String username2) {
        String insertSQL = """
            insert into friendships (user1_id, user2_id)
            select least(u1.id, u2.id), greatest(u1.id, u2.id)
            FROM
                (select id from users where username = ?) as u1,
                (select id from users where username = ?) as u2
            on conflict do nothing
            returning user1_id, user2_id, created_at
            """;

        Connection connection = null;
        try {
            connection = DBManager.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setString(1, username1);
                preparedStatement.setString(2, username2);

                try (ResultSet res = preparedStatement.executeQuery()) {
                    if (res.next()) {
                        Long idUser1 = res.getLong("user1_id");
                        Long idUser2 = res.getLong("user2_id");

                        if (idUser1 == 0 || idUser2 == 0) {
                            throw new SQLException("Unul dintre utilizatori nu există.");
                        }

                        LocalDateTime createdAt = res.getTimestamp("created_at").toLocalDateTime();
                        Friendship friendship = new Friendship(idUser1, idUser2, createdAt);

                        connection.commit();
                        return Optional.of(friendship);
                    } else {
                        connection.commit();
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Nu s-a putut salva prietenia: " + e.getMessage());
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

    public void delete (String username1, String username2) {
        String u1 = username1.compareTo(username2) < 0 ? username1 : username2;
        String u2 = username1.compareTo(username2) < 0 ? username2 : username1;

        String deleteSQL = """
               delete from friendships
               where
               user1_id = (select id from users where username = ?) and
               user2_id = (select id from users where username = ?)
               """;
        try (var connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setString(1, u1);
            preparedStatement.setString(2, u2);
            int rows = preparedStatement.executeUpdate();
            if (rows == 0)
                throw new DBFriendshipRepoException("Friendship not deleted");
        } catch (SQLException e) {
            throw new DBFriendshipRepoException(e.getMessage());
        }
    }

    public Optional<Friendship> findOne(String username1, String username2) {
        String u1 = username1.compareTo(username2) < 0 ? username1 : username2;
        String u2 = username1.compareTo(username2) < 0 ? username2 : username1;

        String selectSQL = """
                select user1_id, user2_id, created_at from friendships
                where
                user1_id = (select id from users where username = ?) and
                user2_id = (select id from users where username = ?)
        """;
        try (var connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                Long user1Id = resultSet.getLong("user1_id");
                Long user2Id = resultSet.getLong("user2_id");
                LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                return Optional.of(new Friendship(user1Id, user2Id, createdAt));
            } else return Optional.empty();
        }  catch (SQLException e) {
            throw new DBFriendshipRepoException(e.getMessage());
        }
    }

    public List<Friendship> findAll() {
        String findAllSQL = """
                select user1_id, user2_id, created_at from friendships
        """;
        List<Friendship> friendships = new ArrayList<>();
        try (var connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(findAllSQL);
        ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Long idUser1 = resultSet.getLong("user1_id");
                Long idUser2 = resultSet.getLong("user2_id");
                LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                Friendship friendship = new Friendship(idUser1, idUser2, createdAt);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            throw new DBFriendshipRepoException(e.getMessage());
        }
    }

    public List<FriendshipDTO> findAllUsername() {
        String findAllSQL = """
        select 
            u1.username as username1,
            u2.username as username2,
            f.created_at
        from friendships f
        join users u1 on f.user1_id = u1.id
        join users u2 on f.user2_id = u2.id
        order by username1, username2
    """;

        List<FriendshipDTO> friendships = new ArrayList<>();

        try (var connection = DBManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(findAllSQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String username1 = rs.getString("username1");
                String username2 = rs.getString("username2");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                friendships.add(new FriendshipDTO(
                        username1,
                        username2,
                        createdAt
                ));
            }
            return friendships;
        } catch (SQLException e) {
            throw new DBFriendshipRepoException(e.getMessage());
        }
    }

    public Map<String, Set<String>> getAdjacencyMap(){
        Map<String, Set<String>> adjacencyMap = new HashMap<>();
        String findAllSQL = """
            select u1.username as user1, u2.username as user2
            from friendships f
            join users u1 on f.user1_id = u1.id
            join users u2 on f.user2_id = u2.id
        """;
        try (var connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(findAllSQL);
        ResultSet resultSet = preparedStatement.executeQuery()){
            while (resultSet.next()) {
                String u1 = resultSet.getString("user1");
                String u2 = resultSet.getString("user2");

                adjacencyMap.putIfAbsent(u1, new HashSet<>());
                adjacencyMap.putIfAbsent(u2, new HashSet<>());

                adjacencyMap.get(u1).add(u2);
                adjacencyMap.get(u2).add(u1);
            }
            return adjacencyMap;
        } catch (SQLException e) {
            throw new DBFriendshipRepoException(e.getMessage());
        }
    }

    public Set<String> findFriends(String username) {
        String findFriendsSQL = """
                    select u2.username as friend
                    from friendships f
                    join users u1 on f.user1_id = u1.id
                    join users u2 on f.user2_id = u2.id
                    where u1.username = ?
                    union
                    select u1.username as friend
                    from friendships f
                    join users u1 on f.user1_id = u1.id
                    join users u2 on f.user2_id = u2.id
                    where u2.username = ?
                """;

        Set<String> friends = new HashSet<>();
        try (var connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(findFriendsSQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                friends.add(resultSet.getString("friend"));
            }
            return friends;
        } catch (SQLException e) {
            throw new DBFriendshipRepoException(e.getMessage());
        }
    }
}
