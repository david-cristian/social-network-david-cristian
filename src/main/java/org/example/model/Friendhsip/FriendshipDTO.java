package org.example.model.Friendhsip;

import java.security.Timestamp;
import java.time.LocalDateTime;

public record FriendshipDTO (
    String username1,
    String username2,
    LocalDateTime createdAt
) {}
