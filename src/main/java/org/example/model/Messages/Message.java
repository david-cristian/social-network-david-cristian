package org.example.model.Messages;

import org.example.model.Users.User;

import java.time.LocalDateTime;
import java.util.List;

public class Message {
    private Long id;
    private User from;
    private List<User> to;
    private String text;
    private LocalDateTime time;
    private Message reply;

    public Message(User from, List<User> to, String text, LocalDateTime time) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.time = time;
        this.reply = null;
    }

    public Long getId() {
        return id;
    }

    public User getFrom() {
        return from;
    }

    public List<User> getTo() {
        return to;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return from + " : " + text + " (" + time + ") ";
    }
}
