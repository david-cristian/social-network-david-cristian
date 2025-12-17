package org.example.repository.InMemoryRepo;

import org.example.model.Friendhsip.Friendship;
import org.example.model.Users.User;
import org.example.utils.RepoException.RepoException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public abstract class FriendshipRepository {
    private Set<Friendship> friendships = new HashSet<>();

    public void addFriendship(User user1, User user2) {
        if (friendships.contains(new Friendship(user1.getId(), user2.getId(), LocalDateTime.now())))
            throw new RepoException("Friendship already exists.");
        friendships.add(new Friendship(user1.getId(), user2.getId(), LocalDateTime.now()));
    }

    public FriendshipRepository(Set<Friendship> friendships) {
        this.friendships = friendships;
    }

    public void removeFriendship(User user1, User user2) {
        if (!friendships.contains(new Friendship(user1.getId(), user2.getId(), LocalDateTime.now())))
            throw new RepoException("Friendship doesn't exists.");
        friendships.remove(new Friendship(user1.getId(), user2.getId(), LocalDateTime.now()));
    }
    public Set<Long> getFriends(User user) {
        return friendships.stream()
                .filter(f -> f.involves(user))
                .map(f -> f.getOther(user))
                .collect(Collectors.toSet());
    }

    public boolean areFriends(User user1, User user2) {
        return friendships.contains(new Friendship(user1.getId(), user2.getId(), LocalDateTime.now()));
    }

    public List<Friendship> getFriendships(){
        if (friendships.isEmpty())
            throw new RepoException("No friendships exist.");
        return new ArrayList<>(friendships);
    }
}