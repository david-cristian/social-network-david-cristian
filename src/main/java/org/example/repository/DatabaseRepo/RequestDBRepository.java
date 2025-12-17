package org.example.repository.DatabaseRepo;

import org.example.model.FriendRequest.FriendRequest;
import org.example.model.FriendRequest.RequestStatus;
import org.example.model.Users.User;
import org.example.utils.DBManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequestDBRepository {
    private final UserDBRepository userRepo;

    public RequestDBRepository(UserDBRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void save(FriendRequest friendRequest) {
        String sql = "insert into friend_request (from_user_id, to_user_id, status, date) values (?, ?, ?, ?)";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, friendRequest.getFrom().getId());
            preparedStatement.setLong(2, friendRequest.getTo().getId());
            preparedStatement.setString(3, friendRequest.getStatus().toString());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(friendRequest.getDate()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(FriendRequest friendRequest) {
        String sql = "update friend_request set status = ? where id = ?";
        try (Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, friendRequest.getStatus().name());
            preparedStatement.setLong(2, friendRequest.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<FriendRequest> findAll() {
        List<FriendRequest> friendRequests = new ArrayList<>();
        String sql = "select * from friend_request";

        try (Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long fromId = resultSet.getLong("from_user_id");
                Long toId = resultSet.getLong("to_user_id");
                String statusStr = resultSet.getString("status");
                RequestStatus requestStatus = RequestStatus.valueOf(statusStr);
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                User from = userRepo.findOne(fromId).orElse(null);
                User to = userRepo.findOne(toId).orElse(null);

                if (from != null && to != null) {
                    FriendRequest req = new FriendRequest(from, to, requestStatus, date);
                    req.setId(id);
                    friendRequests.add(req);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendRequests;
    }

    public FriendRequest findOne(Long fromId, Long toId) {
        String sql = "select * from friend_request where from_user_id = ? and to_user_id = ?";

        try (Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, fromId);
            preparedStatement.setLong(2, toId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    RequestStatus status = RequestStatus.valueOf(resultSet.getString("status"));
                    LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                    Long id = resultSet.getLong("id");

                    User from = userRepo.findOne(fromId).orElse(null);
                    User to = userRepo.findOne(toId).orElse(null);

                    if (from != null && to != null) {
                        FriendRequest req = new FriendRequest(from, to, status, date);
                        req.setId(id);
                        return req;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
