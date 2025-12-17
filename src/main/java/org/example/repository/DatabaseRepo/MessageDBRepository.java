package org.example.repository.DatabaseRepo;

import org.example.model.Messages.Message;
import org.example.model.Users.User;
import org.example.utils.DBManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageDBRepository {

    private UserDBRepository userRepo;

    public MessageDBRepository(UserDBRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void save (Message message){
        String sqlMsg = "insert into messages (from_user_id, message_text, data, reply_id) values (?, ?, ?, ?) returning id";
        String sqlReceivers = "insert into message_receivers (message_id, receiver_id) values (?, ?)";

        try (Connection connection = DBManager.getConnection()) {
            connection.setAutoCommit(false);

            Long msgId = 0L;
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlMsg)) {
                preparedStatement.setLong(1, message.getFrom().getId());
                preparedStatement.setString(2, message.getText());
                preparedStatement.setTimestamp(3, Timestamp.valueOf(message.getTime()));
                if (message.getReply() != null) preparedStatement.setLong(4, message.getReply().getId());
                else preparedStatement.setNull(4, Types.BIGINT);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) msgId = resultSet.getLong(1);
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(sqlReceivers)) {
                for (User user : message.getTo()) {
                    ps.setLong(1, msgId);
                    ps.setLong(2, user.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            connection.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<Message> findAll () {
            List<Message> messages = new ArrayList<>();
            Map<Long, Long> replyMap = new HashMap<>();
            String sql = "select * from messages";
            try (Connection connection = DBManager.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    Long fromUserId = rs.getLong("from_user_id");
                    String text = rs.getString("message_text");
                    LocalDateTime date = rs.getTimestamp("data").toLocalDateTime();
                    Long replyId = rs.getLong("reply_id");
                    if (rs.wasNull()) replyId = null;
                    User sender = userRepo.findOne(fromUserId).orElse(null);
                    List<User> receivers = getReceiversForMessage(id);

                    Message msg = new Message(sender, receivers, text, date);
                    msg.setId(id);

                    messages.add(msg);

                    if (replyId != null)
                        replyMap.put(id, replyId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            for (Message message : messages) {
                if (replyMap.containsKey(message.getId())) {
                    Long parentId = replyMap.get(message.getId());

                    Message parentMsg = messages.stream()
                            .filter(m -> m.getId().equals(parentId))
                            .findFirst()
                            .orElse(null);
                    message.setReply(parentMsg);
                }
            }
            return messages;
    }

    private List<User> getReceiversForMessage(Long messageId) {
        List<User> receivers = new ArrayList<>();
        String sql = "select * from message_receivers where message_id = ?";

        try (Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, messageId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    Long userId = rs.getLong("receiver_id");
                    userRepo.findOne(userId).ifPresent(receivers::add);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receivers;
    }
}
