package org.example.model;

import java.util.Objects;

public class Friendship {
    private User user1;
    private User user2;

    public Friendship(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public boolean involves(User user) {
        return user1.equals(user) || user2.equals(user);
    }

    public User getOther(User user) {
        if (user.equals(user1)) return user2;
        else if (user.equals(user2)) return user1;
        else return null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Friendship f)) return false;
        return (user1.equals(f.user1) && user2.equals(f.user2)) ||
                (user1.equals(f.user2) && user2.equals(f.user1));
    }

    @Override
    public int hashCode() {
        return Objects.hash(user1.getId() + user2.getId());
    }
}
