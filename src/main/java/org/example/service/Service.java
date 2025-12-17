package org.example.service;
import org.example.model.Carduri.Card;
import org.example.model.Event.RaceEvent;
import org.example.model.FriendRequest.FriendRequest;
import org.example.model.FriendRequest.RequestStatus;
import org.example.model.Friendhsip.Friendship;
import org.example.model.Friendhsip.FriendshipDTO;
import org.example.model.Messages.Message;
import org.example.model.Users.Duck;
import org.example.model.Users.User;
import org.example.repository.DatabaseRepo.*;
import org.example.repository.InMemoryRepo.*;
import org.example.service.Factory.CardFactory;
import org.example.service.Factory.UserFactory;
import org.example.utils.*;
import org.example.utils.Events.EntityChangeEvent;
import org.example.utils.Events.EntityChangeEventType;
import org.example.utils.ModelException.RaceEventException;
import org.example.utils.ModelException.UserException;
import org.example.utils.Observer.Observable;
import org.example.utils.Observer.Observer;
import org.example.utils.RepoException.CardRepoException;
import org.example.utils.RepoException.DBRepoCardException;
import org.example.utils.RepoException.RepoException;
import org.example.utils.ServiceException.ServiceException;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Service implements Observable<EntityChangeEvent> {
    private final UserDBRepository userRepo;
    private final FriendshipDBRepository friendshipRepo;
    private final CardDBRepository cardRepo;
    private final RaceEventDBRepository raceEventRepository;
    private final MessageDBRepository messageRepo;
    private final RequestDBRepository requestRepo;
    private final UserFactory userFactory;
    private final CardFactory cardFactory;
    private List<Observer <EntityChangeEvent>> observers =  new ArrayList<Observer<EntityChangeEvent>>();

    @Override
    public void addObserver(Observer <EntityChangeEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer <EntityChangeEvent> observer) {
        observers.remove(observer);
    }

    public void notifyObservers(EntityChangeEvent event) {
        for (Observer <EntityChangeEvent> observer : observers) {
            observer.update(event);
        }
    }

    public Service(UserDBRepository userRepo, FriendshipDBRepository friendshipRepo, CardDBRepository cardRepo, RaceEventDBRepository raceEventRepository, MessageDBRepository messageRepo, RequestDBRepository requestRepo, UserFactory userFactory, CardFactory cardFactory) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
        this.raceEventRepository = raceEventRepository;
        this.messageRepo = messageRepo;
        this.requestRepo = requestRepo;
        this.userFactory = userFactory;
        this.cardFactory = cardFactory;
        this.cardRepo = cardRepo;
    }

    public User add(String type, String username, String rawPassword, String email, String... args) {
        try{
            String hashedPassword = PasswordHasher.hash(rawPassword);
            User user = userFactory.create(type, username, hashedPassword, email, args);
            Optional<User> saved = userRepo.save(user);
            if (saved.isPresent()) {
                notifyObservers(new EntityChangeEvent(EntityChangeEventType.ADD, saved.get()));
            }
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

    public void addMemberToCard(String cardName,  String username) {
        try {
            cardRepo.adaugaMembru(cardName, username);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public RaceEvent addRaceEvent(String nume, List<Double> distances, int m) {
        try {
            RaceEvent raceEvent = new RaceEvent(nume, userRepo.getDucks(), distances, m);
            raceEventRepository.save(raceEvent);
            return raceEvent;
        } catch (RaceEventException e){
            throw new RaceEventException(e.getMessage());
        }
    }

    public void deleteUser(String username) {
        try {
            User user = userRepo.findOne(username).orElse(null);
            userRepo.delete(username);
            if (user != null) {
                notifyObservers(new EntityChangeEvent(EntityChangeEventType.DELETE, user));
            }
            for (RaceEvent raceEvent : raceEventRepository.findAll()) {
                if (!raceEvent.isExecuted()) {
                    if (raceEvent.getSelectedDucks().size() < raceEvent.getM())
                        raceEvent.selectParticipants();
                }
            }
        } catch (Exception e) {
            throw new RepoException(e.getMessage());
        }
    }

    public void deleteCard(String name) {
        try {
            cardRepo.delete(name);
        } catch (Exception e){
            throw new CardRepoException(e.getMessage());
        }
    }

    public void removeMemberCard(String cardName, String username) {
        try {
            cardRepo.removeMembru(cardName, username);
        } catch (Exception e) {
            throw new CardRepoException(e.getMessage());
        }
    }

    public User findUser(String username) {
        try {
            return userRepo.findOne(username).orElseThrow(() -> new UserException("User not found."));
        } catch (Exception e) {
            throw new RepoException(e.getMessage());
        }
    }

    public RaceEvent findRaceEvent(String name) {
        try {

            RaceEvent raceEvent = raceEventRepository.findOne(name).orElseThrow(() -> new RaceEventException("Race event not found."));
            return raceEvent;
        } catch (RaceEventException e){
            throw new RaceEventException(e.getMessage());
        }
    }

    public Card<? extends Duck> findCard(String name) {
        try {
            return cardRepo.findOne(name).orElseThrow(() -> new DBRepoCardException("Card not found."));
        } catch (Exception e){
            throw new CardRepoException(e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        try {
            if (userRepo.findAll().isEmpty())
                throw new ServiceException("No users found");
            return userRepo.findAll();
        } catch (Exception e){
            throw new RepoException(e.getMessage());
        }
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return userRepo.findAllPage(pageable);
    }

    public List<Card<? extends Duck>> getAllCards() {
        try {
            return cardRepo.findAll();
        } catch (Exception e){
            throw new CardRepoException(e.getMessage());
        }
    }

    public List<String> getMembers(String username) {
        try {
            return cardRepo.getMembersDTO(username);
        } catch (Exception e){
            throw new CardRepoException(e.getMessage());
        }
    }

    public List<RaceEvent> getAllRaceEvents() {
        try {
            return raceEventRepository.findAll();
        } catch (Exception e){
            throw new RepoException(e.getMessage());
        }
    }


    public void addFriend(String username1, String username2) {
        try {
            friendshipRepo.save(username1, username2);
        } catch (RepoException e) {
            throw new RepoException(e.getMessage());
        }
    }

    public void removeFriend(String username1, String username2) {
        try {
            friendshipRepo.delete(username1, username2);
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
            return friendshipRepo.findAll();
        } catch (Exception e) {
            throw new RepoException(e.getMessage());
        }
    }

    public List<FriendshipDTO> getAllDTOFriendships() {
        try{
            return friendshipRepo.findAllUsername();
        } catch (Exception e) {
            throw new RepoException(e.getMessage());
        }
    }

    public Set<String> getFriends(String username) {
        try {
            return friendshipRepo.findFriends(username);
        } catch (RepoException e) {
            throw new RepoException(e.getMessage());
        }
    }

    public void startRace(RaceEvent raceEvent) {
        try {
            raceEvent.startRace();
            raceEventRepository.finalizeRace(raceEvent);
        } catch (RaceEventException e) {
            throw new RaceEventException(e.getMessage());
        }
    }

    public void subscribe (User user, RaceEvent raceEvent) {
        try {
            raceEvent.subscribe(user);
            raceEventRepository.subscribe(user.getUsername(), raceEvent.getNume());
        } catch (RaceEventException e) {
            throw new RaceEventException(e.getMessage());
        }
    }

    public void unsubscribe (User user, RaceEvent raceEvent) {
        try {
            raceEvent.unsubscribe(user);
            raceEventRepository.unsubscribe(user.getUsername(), raceEvent.getNume());
        } catch (RaceEventException e) {
            throw new RaceEventException(e.getMessage());
        }
    }

    public void sendMessage(String fromUsername, List<String> toUsernames, String text, Message replyMsg) {
        User sender = userRepo.findOne(fromUsername)
                .orElseThrow(() -> new ServiceException("Sender not found: " + fromUsername));

        List<User> receivers = toUsernames.stream()
                .map(username -> userRepo.findOne(username).orElse(null))
                .filter(user -> user != null) // <--- ASTA E LINIA SALVATOARE!
                .collect(Collectors.toList());

        if (receivers.isEmpty()) {
            throw new ServiceException("Receiver not found (check username spelling).");
        }

        Message msg = new Message(sender, receivers, text, LocalDateTime.now());
        msg.setReply(replyMsg);

        messageRepo.save(msg);

        EntityChangeEvent event = new EntityChangeEvent(EntityChangeEventType.ADD, msg);
        notifyObservers(event);
    }
    public List<Message> getConversation(String username1, String username2) {
        List<Message> allMessages = messageRepo.findAll();
        return allMessages.stream()
                .filter(msg -> {
                    String sender = msg.getFrom().getUsername();
                    List<String> receivers = msg.getTo().stream()
                            .map(User::getUsername)
                            .collect(Collectors.toList());

                    boolean caz1 = sender.equals(username1) && receivers.contains(username2);
                    boolean caz2 = sender.equals(username2) && receivers.contains(username1);
                    return caz1 || caz2;
                }).sorted(Comparator.comparing(Message::getTime))
                .collect(Collectors.toList());
    }

    public void sendFriendRequest(String usernameFrom, String usernameTo) {
        User sender = userRepo.findOne(usernameFrom).orElseThrow();
        User to = userRepo.findOne(usernameTo).orElseThrow();
        FriendRequest existingReq = requestRepo.findOne(sender.getId(), to.getId());

        if (existingReq != null) {
            switch (existingReq.getStatus()) {
                case PENDING -> throw new ServiceException("Friend request already in progress");
                case APPROVED -> throw new ServiceException("Friend request already approved");
                case REJECTED -> {
                    existingReq.setStatus(RequestStatus.PENDING);
                    existingReq.setDate(LocalDateTime.now());

                    requestRepo.update(existingReq);

                    notifyObservers(new EntityChangeEvent<>(EntityChangeEventType.UPDATE, existingReq));
                    return;
                }
            }
        }
        FriendRequest newRequest = new FriendRequest(sender, to);
        requestRepo.save(newRequest);
        notifyObservers(new EntityChangeEvent(EntityChangeEventType.ADD, newRequest));
    }

    public void respondToRequest(FriendRequest request, RequestStatus requestStatus) {
        if (requestStatus == RequestStatus.APPROVED) {
            request.setStatus(RequestStatus.APPROVED);
            requestRepo.update(request);
            try {
                friendshipRepo.save(request.getFrom().getUsername(), request.getTo().getUsername());
            } catch (Exception ex) {
                System.out.println("Friendship already exists.");
            }
        } else {
            request.setStatus(RequestStatus.REJECTED);
            requestRepo.update(request);
        }
        notifyObservers(new EntityChangeEvent<>(EntityChangeEventType.UPDATE, request));
    }

    public List<FriendRequest> getPendingRequests(String receiverUsername) {
        return requestRepo.findAll().stream()
                .filter(req -> req.getTo().getUsername().equals(receiverUsername))
                .filter(req -> req.getStatus() == RequestStatus.PENDING)
                .collect(Collectors.toList());
    }
}
