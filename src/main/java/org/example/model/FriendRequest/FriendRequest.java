package org.example.model.FriendRequest;

import org.example.model.Users.User;

import java.time.LocalDateTime;

public class FriendRequest {
    private Long id;
    private User from;
    private User to;
    private RequestStatus status;
    private LocalDateTime date;

    public FriendRequest(User from, User to) {
        this.from = from;
        this.to = to;
        this.status = RequestStatus.PENDING;
        this.date = LocalDateTime.now();
    }

    public FriendRequest(User from, User to, RequestStatus status, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.status = status;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public User getFrom() {
        return from;
    }

    public User getTo() {
        return to;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Request from " + from.getUsername() + ": " + status;
    }
}
