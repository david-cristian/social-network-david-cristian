package org.example.repository;

import org.example.model.Friendship;
import org.example.model.User;
import org.example.utils.RepoException;

import java.util.*;
import java.util.stream.Collectors;

public abstract class FriendshipRepository {
    private Set<Friendship> friendships = new HashSet<>();

    public void addFriendship(User user1, User user2) {
        if (friendships.contains(new Friendship(user1, user2)))
            throw new RepoException("Friendship already exists.");
        friendships.add(new Friendship(user1, user2));
    }

    public FriendshipRepository(Set<Friendship> friendships) {
        this.friendships = friendships;
    }

    public void removeFriendship(User user1, User user2) {
        if (!friendships.contains(new Friendship(user1, user2)))
            throw new RepoException("Friendship doesn't exists.");
        friendships.remove(new Friendship(user1, user2));
    }
    public Set<User> getFriends(User user) {
        return friendships.stream()
                .filter(f -> f.involves(user))
                .map(f -> f.getOther(user))
                .collect(Collectors.toSet());
    }

    public boolean areFriends(User user1, User user2) {
        return friendships.contains(new Friendship(user1, user2));
    }

    public Map<String, Set<String>> getAdjacencyMap(){
        Map<String, Set<String>> adjacencyMap = new HashMap<>();

        for(Friendship friendship : friendships){
            String usrn1 = friendship.getUser1().getUsername();
            String usrn2 = friendship.getUser2().getUsername();
            adjacencyMap.putIfAbsent(usrn1, new HashSet<>());
            adjacencyMap.putIfAbsent(usrn2, new HashSet<>());
            adjacencyMap.get(usrn1).add(usrn2);
            adjacencyMap.get(usrn2).add(usrn1);
        }
        return adjacencyMap;
    }

    public List<Friendship> getFriendships(){
        if (friendships.isEmpty())
            throw new RepoException("No friendships exist.");
        return new ArrayList<>(friendships);
    }
}