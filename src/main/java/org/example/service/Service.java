package org.example.service;
import org.example.model.*;
import org.example.repository.*;
import org.example.utils.*;
import org.example.validation.ValidationStrategy;
import org.example.repository.RaceHistoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Service {
    private final UserRepository userRepo;
    private final FriendshipRepository  friendshipRepo;
    private final CardRepository cardRepo;
    private final RaceEventRepository raceEventRepository;
    private final RaceHistoryRepository raceHistoryRepository;
    private final UserFactory userFactory;
    private final CardFactory cardFactory;

    public Service(UserRepository userRepo, FriendshipRepository friendshipRepo, CardRepository cardRepo, RaceEventRepository raceEventRepository, RaceHistoryRepository raceHistoryRepository,  UserFactory userFactory, CardFactory cardFactory) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
        this.raceEventRepository = raceEventRepository;
        this.raceHistoryRepository = raceHistoryRepository;
        this.userFactory = userFactory;
        this.cardFactory = cardFactory;
        this.cardRepo = cardRepo;
    }

    public User add(String type, String... args) {
        try{
            User user = userFactory.create(type, args);
            userRepo.save(user);
            return user;
        } catch (UserException | ValidationException e){
            throw new ValidationException(e.getMessage());
        }
    }

    public <T extends Duck> Card<T> addCard(String type, String... args) {
        try{
            Card card = cardFactory.create(type, args);
            cardRepo.save(card);
            return card;
        } catch (CardRepoException e){
            throw new CardRepoException(e.getMessage());
        }
    }

    public RaceEvent addRaceEvent(long id, String nume, List<Double> distances, int m) {
        try {
            RaceEvent raceEvent = new RaceEvent(id, nume, userRepo.getDucks(), distances, m);
            raceEventRepository.addRaceEvent(raceEvent);
            return raceEvent;
        } catch (RaceEventException e){
            throw new RaceEventException(e.getMessage());
        }
    }

    public void deleteUser(String username) {
        try {
            User user = userRepo.findOne(username);
            for (Friendship f : friendshipRepo.getFriendships())
                friendshipRepo.removeFriendship(f.getUser1(), f.getUser2());

            for (RaceEvent r : raceEventRepository.getRaceEvents())
                r.unsubscribe(user);

            if (user instanceof Duck d)
                for (Card c : cardRepo.getCarduri())
                    if (c.getMembri().contains(d))
                        c.removeMembru(d);

            userRepo.delete(username);
        } catch (RepoException e) {
            throw new RepoException(e.getMessage());
        }
    }

    public void deleteCard(String name) {
        try{
            Card card = cardRepo.findOneCard(name);
            cardRepo.deleteCard(card);
        } catch (CardRepoException e){
            throw new CardRepoException(e.getMessage());
        }
    }

    public User findUser(String username) {
        try {
            return userRepo.findOne(username);
        } catch (RepoException e) {
            throw new RepoException(e.getMessage());
        }
    }

    public RaceEvent findRaceEvent(String name) {
        try {

            RaceEvent raceEvent = raceEventRepository.findRaceEvent(name);
            return raceEvent;
        } catch (RaceEventException e){
            throw new RaceEventException(e.getMessage());
        }
    }

    public String findAnyRaceEvent(String name) {
        try {
            RaceHistoryEntry entry = raceHistoryRepository.findRace(name);
            if (entry == null) {
                RaceEvent raceEvent = findRaceEvent(name);
                return raceEvent.toString();
            } else return entry.toString();
        } catch (Exception e){
            throw new RaceEventException(e.getMessage());
        }
    }


    public <T extends Duck> Card<T> findCard(String name) {
        try {
            return cardRepo.findOneCard(name);
        } catch (CardRepoException e){
            throw new CardRepoException(e.getMessage());
        }
    }

    public List<User> getAllUsers(){
        if (userRepo.findAll().isEmpty())
            throw new ServiceException("No users found");
        return userRepo.findAll();
    }

    public List<Card> getAllCards(){
        return cardRepo.getCarduri();
    }

    public List<RaceEvent> getAllRaceEvents(){
        return raceEventRepository.getRaceEvents();
    }

    public void updateUser(User user){
        ValidationStrategy validator = user.getValidationStrategy();
        if(validator != null) {
            validator.validate(user);
        }
        userRepo.save(user);
    }

    public void addFriend(String username1, String username2) {
        try {
            User user1 = findUser(username1);
            User user2 = findUser(username2);
            friendshipRepo.addFriendship(user1, user2);
        } catch (RepoException e) {
            throw new RepoException(e.getMessage());
        }
    }

    public void removeFriend(String username1, String username2) {
        try {
            User user1 = findUser(username1);
            User user2 = findUser(username2);
            friendshipRepo.removeFriendship(user1, user2);
        }  catch (RepoException e) {
            throw new RepoException(e.getMessage());
        }
    }

    public int getNumberOfCommunities(){
        if (GraphUtils.getConnectedComponents(friendshipRepo.getAdjacencyMap()).isEmpty())
            throw new ServiceException("Nu am gasit nicio comunitate :(");
        return GraphUtils.getConnectedComponents(friendshipRepo.getAdjacencyMap()).size();
    }

    public GraphUtils.Pair<Set<String>, Integer> getMostSociableCommunity(){
        if (GraphUtils.getMostSociableCommunity(friendshipRepo.getAdjacencyMap()) == null)
            throw new ServiceException("Nu am gasit nicio comunitate :(");
        return GraphUtils.getMostSociableCommunity(friendshipRepo.getAdjacencyMap());
    }

    public List<Friendship> getAllFriendships() {
        try {
            return friendshipRepo.getFriendships();
        } catch (RepoException e) {
            throw new RepoException(e.getMessage());
        }
    }

    public Set<User> getFriends(String username) {
        try {
            User user = findUser(username);
            return friendshipRepo.getFriends(user);
        } catch (RepoException e) {
            throw new RepoException(e.getMessage());
        }
    }

    public void startRace(RaceEvent raceEvent) {
        try {
            raceEvent.startRace(raceHistoryRepository);
            List<String> participantNames = raceEvent.getSelectedDucks().stream().map(Duck::getUsername).toList();
            RaceHistoryEntry entry = new RaceHistoryEntry(
                    raceEvent.getNume(),
                    raceEvent.getResult(),
                    participantNames,
                    raceEvent.isExecuted()
            );
            raceHistoryRepository.add(entry);
            raceEventRepository.deleteRaceEvent(raceEvent.getNume());
        } catch (RaceEventException e) {
            throw new RaceEventException(e.getMessage());
        }
    }

    public List<Object> getAllAnyRaceEvents(){
        List<Object> all = new ArrayList<>();
        all.addAll(raceEventRepository.getRaceEvents());
        all.addAll(raceHistoryRepository.getAll());
        return all;
    }

}
