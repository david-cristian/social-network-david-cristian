package org.example.ui;

import org.example.model.Carduri.Card;
import org.example.model.Event.RaceEvent;
import org.example.model.Friendhsip.FriendshipDTO;
import org.example.model.Users.*;
import org.example.service.Service;
import org.example.utils.GraphUtils;
import org.example.utils.ModelException.RaceEventException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Ui {
    private Scanner scanner = new Scanner(System.in);
    private final Service srv;

    public Ui(Service srv) {
        this.srv = srv;
    }

    private String login() {
        while (true) {
            System.out.print("Username: ");
            String username = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            try {
                User user = srv.findUser(username);
                if (!user.getPassword().equals(password)) {
                    System.out.println("Wrong password! Try again.");
                } else {
                    System.out.println("Welcome! Logged in as " + username);
                    return user.getUsername();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Login failed. Returning to main menu.");
                return null;
            }
        }
    }

    private void userInterface(String username) {
        while (true) {

            System.out.println("1. Delete user" +
                    "\n2. Find user" +
                    "\n3. Show users " +
                    "\n4. Add friend" +
                    "\n5. Remove friend" +
                    "\n6. Show friends" +
                    "\n7. Number of communities" +
                    "\n8. Most social community" +
                    "\n9. Create flock" +
                    "\n10. Delete flock" +
                    "\n11. Show flocks" +
                    "\n12. Search flock" +
                    "\n13. Add duck to flock" +
                    "\n14. Remove duck from flock" +
                    "\n15. Create race event" +
                    "\n16. Search race event" +
                    "\n17. See all race events" +
                    "\n18. Subscribe to event" +
                    "\n19. Unsubscribe to event" +
                    "\n20. Start race event" +
                    "\nE. Exit");

            String choice;
            while (true) {
                System.out.print(">>> ");
                choice = scanner.nextLine().trim().toUpperCase();
                if (choice.equals("1") || choice.equals("2") || choice.equals("3") || choice.equals("4")
                        || choice.equals("5") || choice.equals("6") || choice.equals("7") || choice.equals("8") || choice.equals("9") ||
                        choice.equals("10") || choice.equals("11") || choice.equals("12") || choice.equals("13") || choice.equals("14") ||
                        choice.equals("15") || choice.equals("16") || choice.equals("17") || choice.equals("18") || choice.equals("19") ||
                        choice.equals("20") || choice.equals("E"))
                    break;
                System.out.println("Invalid choice!");
            }
            switch (choice) {
                case "1": {
                    //delete user
                    try {
                        srv.deleteUser(username);
                        return;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "2": {
                    //find user
                    System.out.print("Enter username: ");
                    String usernameToFind = scanner.nextLine();

                    try {
                        User user = srv.findUser(usernameToFind);

                        if (user instanceof Person p) {
                            System.out.println("Username: " + p.getUsername() +
                                    "\nEmail: " + p.getEmail() +
                                    "\nLast name: " + p.getNume() +
                                    "\nFirst name: " + p.getPrenume() +
                                    "\nBirthday: " + p.getDataNasterii() +
                                    "\nOccupation: " + p.getOcupatie() +
                                    "\nEmpathy: " + p.getNivelEmpatie());
                        } else {
                            System.out.println("Username: " + user.getUsername() +
                                    "\nEmail: " + user.getEmail() +
                                    "\nType: " + ((Duck) user).getTip() +
                                    "\nSpeed: " + ((Duck) user).getViteza() +
                                    "\nStamina: " + ((Duck) user).getRezistenta());
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "3": {
                    //show user
                    try {
                        System.out.println("Username        ||       Email");

                        for (User user : srv.getAllUsers()) {
                            System.out.println(user.getUsername() + " " + user.getEmail());
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "4": {
                    //add friend
                    try {
                        System.out.print("Enter username: ");
                        String usernameToFind = scanner.nextLine();
                        srv.addFriend(username, usernameToFind);
                        System.out.println("Friend added successfully!");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "5": {
                    //remove friend
                    try {
                        System.out.print("Enter username: ");
                        String usernameToFind = scanner.nextLine();
                        srv.removeFriend(username, usernameToFind);
                        System.out.println("Friend removed successfully!");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "6": {
                    //show friends
                    try {
                        for (String us : srv.getFriends(username)) {
                            System.out.println(us);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "7": {
                    //number of communities
                    try {
                        System.out.println("Number of communities: " + srv.getNumberOfCommunities());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "8": {
                    //most social community
                    try {
                        GraphUtils.Pair<Set<String>, Integer> pair = srv.getMostSociableCommunity();
                        for (String us : pair.first) {
                            System.out.println(us);
                        }
                        System.out.println("Diameter: " + pair.second);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "9": {
                    //create flock
                        try {
                            System.out.print("Enter flock type (zburatori/inotatori/inotatori_si_zburatori): ");
                            String type = scanner.nextLine().trim().toLowerCase();

                            System.out.print("Name: ");
                            String name = scanner.nextLine().trim();

                            switch (type) {
                                case "zburatori": {
                                    String[] args = new String[]{name};
                                    Card<Duck> card = srv.addCard("zburatori", args);
                                    System.out.println("Flock has been created");
                                    User user = srv.findUser(username);
                                    if (user instanceof Duck d)
                                        if (d instanceof FlyingDuck flyingDuck)
                                            card.adaugaMembru(flyingDuck);
                                        else if (d instanceof FlyingAndSwimmingDuck flyingAndSwimmingDuck)
                                            card.adaugaMembru(flyingAndSwimmingDuck);
                                    break;
                                }
                                case "inotatori": {
                                    String[] args = new String[]{name};
                                    Card<Duck> card = srv.addCard("inotatori", args);
                                    System.out.println("Flock has been created");
                                    User user = srv.findUser(username);
                                    if (user instanceof Duck d)
                                        if (d instanceof SwimmingDuck swimmingDuck)
                                            card.adaugaMembru(swimmingDuck);
                                        else if (d instanceof FlyingAndSwimmingDuck flyingAndSwimmingDuck)
                                            card.adaugaMembru(flyingAndSwimmingDuck);
                                    break;
                                }
                                case "inotatori_si_zburatori": {
                                    String[] args = new String[]{name};
                                    Card<Duck> card = srv.addCard("inotatori_si_zburatori", args);
                                    System.out.println("Flock has been created");
                                    User user = srv.findUser(username);
                                    if (user instanceof Duck d)
                                        if (d instanceof SwimmingDuck swimmingDuck)
                                            card.adaugaMembru(swimmingDuck);
                                        else if (d instanceof FlyingDuck flyingDuck)
                                            card.adaugaMembru(flyingDuck);
                                         else if (d instanceof FlyingAndSwimmingDuck flyingAndSwimmingDuck)
                                            card.adaugaMembru(flyingAndSwimmingDuck);
                                    break;
                                }
                                default:
                                    throw new Exception("Invalid type!");
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    }
                case "10": {
                    //delete flock
                    while (true) {
                        try {
                            System.out.print("Enter flock name: ");
                            String nameToFind = scanner.nextLine().trim();
                            srv.deleteCard(nameToFind);
                        }  catch (Exception e) {
                            System.out.println(e.getMessage());
                            continue;
                        }
                        break;
                    }
                }
                case "11": {
                    //show flocks
                    try{
                        System.out.println("Name");
                        if (srv.getAllCards().isEmpty())
                            System.out.println("No flocks found");
                        else
                            for (Card card : srv.getAllCards()) {
                                System.out.println(card.getNume());
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "12": {
                    //search flock
                    try{
                        System.out.println("Name: ");
                        String name = scanner.nextLine().trim();
                        Card<? extends Duck> card = srv.findCard(name);

                        System.out.println("Name: " +  card.getNume());

                        System.out.println("Members: ");
                        for (String duck_us : srv.getMembers(card.getNume()))
                            System.out.print(duck_us);
                        System.out.println();
                        System.out.println("Avg. performance: " + card.getPerformantaMedie());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "13": {
                    //add duck to flock
                    try {
                        System.out.print("Flock name: ");
                        String nameToFind = scanner.nextLine().trim();
                        System.out.print("Username: ");
                        String name = scanner.nextLine().trim();

                        srv.addMemberToCard(nameToFind, name);
                        System.out.println("Member added succesfully");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "14": {
                    //remove duck from flock
                    try{
                        System.out.print("Flock name: ");
                        String nameToFind = scanner.nextLine().trim();
                        System.out.print("Username: ");
                        String name = scanner.nextLine().trim();

                        srv.removeMemberCard(nameToFind, name);
                        System.out.println("Member removed succesfully");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "15": {
                    //create race event
                    try {
                        System.out.print("Race name: ");
                        String name = scanner.nextLine();

                        System.out.print("Number of lanes: ");
                        String m = scanner.nextLine().trim();
                        if (Double.valueOf(m) < 0)
                            throw new RaceEventException("Number of lanes must be positive");

                        List<Double> distances = new ArrayList<>();
                        for (int i = 1; i <= Integer.valueOf(m); i++)
                        {
                            System.out.print("Lane " + i + ":");
                            String num = scanner.nextLine().trim();
                            if (Double.valueOf(num) <= 0)
                                throw new RaceEventException("Lane number must be a positive integer");
                            distances.add(Double.valueOf(num));
                        }

                        RaceEvent re = srv.addRaceEvent(name, distances, Integer.valueOf(m));
                        System.out.println("Race created succesfully");
                        System.out.print("Start race event? (y/n): ");
                        while (true) {
                            String raceChoice = scanner.nextLine().trim().toLowerCase();
                            if (raceChoice.equals("y")) {
                                srv.startRace(re);
                                break;
                            } else if (raceChoice.equals("n")) {
                                break;
                            } else {
                                System.out.println("Invalid choice");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "16": {
                    //search race event
                    try{
                        System.out.print("Race name: ");
                        String name =  scanner.nextLine().trim();
                        System.out.println();
                        System.out.println("Info:");
                        RaceEvent raceEvent = srv.findRaceEvent(name);
                        System.out.println(raceEvent.toString());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "17": {
                    //find all race events
                    try{
                        System.out.println("Race events: ");
                        for (Object o : srv.getAllRaceEvents())
                            System.out.println(o.toString());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "18": {
                    //subscribe to event
                    try{
                        User user = srv.findUser(username);
                        System.out.print("Race name: ");
                        String name = scanner.nextLine().trim();
                        RaceEvent re = srv.findRaceEvent(name);
                        srv.subscribe(user, re);
                        System.out.println("Subscribed to " + name);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "19": {
                    //unsubscribe from event
                    try{
                        User user = srv.findUser(username);
                        System.out.print("Race name: ");
                        String name = scanner.nextLine().trim();
                        RaceEvent re = srv.findRaceEvent(name);
                        srv.unsubscribe(user, re);
                        System.out.println("Unsubscribed from " + name);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "20": {
                    //start race event
                    try{
                        System.out.println("Race name: ");
                        String name = scanner.nextLine().trim();
                        RaceEvent re = srv.findRaceEvent(name);
                        srv.startRace(re);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "E": {
                    System.out.println("Returning to main menu");
                    return;
                }
                default:
                    System.out.println("Invalid choice!");
                    break;
            }
        }
    }

    private void readerInterface() {
        while (true) {
            System.out.println("1. Find user" +
                    "\n2. Show users " +
                    "\n3. See friendships" +
                    "\n4. Number of communities" +
                    "\n5. Most social community" +
                    "\n6. Show all flocks" +
                    "\n7. Search flock" +
                    "\n8. Search race event" +
                    "\n9. Show all race events" +
                    "\nE. Exit");

            String choice;
            while (true) {
                System.out.print(">>> ");
                choice = scanner.nextLine();
                if (choice.equals("1") ||   choice.equals("2") || choice.equals("3") || choice.equals("4") || choice.equals("5") || choice.equals("6")
                ||  choice.equals("7") || choice.equals("8") || choice.equals("9") ||  choice.equals("E"))
                    break;
            }
            switch (choice) {
                case "1": {
                    //find user
                    try {
                        System.out.println("Enter username: ");
                        String username = scanner.nextLine();

                        User user = srv.findUser(username);

                        if (user instanceof Person p) {
                            System.out.println("Username: " + p.getUsername() +
                                    "\nEmail: " + p.getEmail() +
                                    "\nLast name: " + p.getNume() +
                                    "\nFirst name: " + p.getPrenume() +
                                    "\nBirthday: " + p.getDataNasterii() +
                                    "\nOccupation: " + p.getOcupatie() +
                                    "\nEmpathy: " + p.getNivelEmpatie());
                        } else {
                            System.out.println("Username: " + user.getUsername() +
                                    "\nEmail: " + user.getEmail() +
                                    "\nType: " + ((Duck) user).getTip() +
                                    "\nSpeed: " + ((Duck) user).getViteza() +
                                    "\nStamina: " + ((Duck) user).getRezistenta());
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "2": {
                    //show users
                    try {
                        System.out.println("Username        ||       Email");
                        for (User user : srv.getAllUsers()) {
                            System.out.println(user.getUsername() + " " + user.getEmail());
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "3": {
                    //show friendships
                    try {
                        for (FriendshipDTO f: srv.getAllDTOFriendships()){
                            System.out.println(f.username1() + "<->" + f.username2() + " friends since " + f.createdAt());
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "4": {
                    //number of communities
                    try {
                        System.out.println("Number of communities: " + srv.getNumberOfCommunities());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "5": {
                    //most social community
                    try {
                        GraphUtils.Pair<Set<String>, Integer> pair = srv.getMostSociableCommunity();
                        for (String username : pair.first) {
                            System.out.println(username);
                        }
                        System.out.println("Diameter: " + pair.second);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "6": {
                    //show all flocks
                    try{
                        System.out.println("Name");
                        for (Card card : srv.getAllCards()) {
                            System.out.println(card.getNume());
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "7": {
                    //search flocks
                    try{
                        System.out.println("Name: ");
                        String name = scanner.nextLine().trim();
                        Card<? extends Duck> card = srv.findCard(name);

                        System.out.print("Name: " +  card.getNume());

                        System.out.println("Membri: ");
                        for (Duck d : card.getMembri())
                            System.out.println(d.getUsername());
                        System.out.println("Avg. performance: " + card.getPerformantaMedie());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "8": {
                    //search race event
                    try{
                        System.out.print("Race name: ");
                        String name =  scanner.nextLine().trim();
                        System.out.println();
                        System.out.println("Info:");
                        RaceEvent raceEvent = srv.findRaceEvent(name);
                        System.out.println(raceEvent);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "9": {
                    //show all race events
                    try{
                        System.out.println("Race events: ");
                        for (Object o : srv.getAllRaceEvents())
                            System.out.println(o.toString());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "E": {
                    return;
                }
                default:
                    System.out.println("Invalid choice");
                    break;
            }
        }
    }

    private void registerInterface() {
        System.out.println("Register new user.");
        try {
            System.out.print("Type (person/duck): ");

            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "person": {
                    System.out.println("Username: (if username contains spaces or capital letters it will be trimmed and converted to lower)");
                    String username = scanner.nextLine().trim().toLowerCase();

                    System.out.print("Password: ");
                    String password = scanner.nextLine();

                    System.out.print("Email: ");
                    String email = scanner.nextLine();

                    System.out.print("Last name: ");
                    String nume = scanner.nextLine();

                    System.out.print("First name: ");
                    String prenume = scanner.nextLine();

                    System.out.print("Birthday (YYYY-MM-DD):  ");
                    String dataNasterii = scanner.nextLine().trim();

                    System.out.print("Occupation: ");
                    String ocupatie = scanner.nextLine().trim();

                    System.out.println("Empathy: (between 0 and 1000)");
                    String nivelEmpatie = scanner.nextLine().trim();

                    String[] args = new String[]{username, password, email, nume, prenume, dataNasterii, ocupatie, nivelEmpatie};
                    //srv.add("person", args);
                    System.out.println("User registred successfully");
                    return;
                }
                case "duck": {
                    System.out.print("Username: ");
                    String username = scanner.nextLine();

                    System.out.print("Password: ");
                    String password = scanner.nextLine();

                    System.out.print("Email: ");
                    String email = scanner.nextLine();

                    System.out.print("Type (FLYING, SWIMMING, FLYING_AND_SWIMMING): ");
                    String tip = scanner.nextLine();

                    System.out.print("Speed: ");
                    String viteza = scanner.nextLine().trim();

                    System.out.print("Stamina: ");
                    String rezistenta = scanner.nextLine().trim();

                    String[] args = new String[]{username, password, email, tip, viteza, rezistenta};
                    //srv.add("duck", args);
                    System.out.println("User registred successfully");
                    return;
                }
                default: {
                    System.out.println("Incompatible user type.");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public void run() {
        System.out.println("Hello!(Quack!)");
        System.out.println(" ");

        while (true) {
            System.out.println("""
                    ~Main menu~
                    1. Login
                    2. Register
                    3. Continue without an account (read-only)
                    E. Exit
                    """);
            String choice;
            while (true) {
                System.out.print(">>> ");
                choice = scanner.nextLine().trim().toUpperCase();
                if (choice.equals("1") ||  choice.equals("2") || choice.equals("3") || choice.equals("4") || choice.equals("E"))
                    break;
            }

            switch (choice) {
                case "1": {
                    String username = login();
                    if (username == null)
                        break;
                    userInterface(username);
                    break;
                }
                case "2": {
                    registerInterface();
                    break;
                }
                case "3": {
                    readerInterface();
                    break;
                }
                case "E": {
                    System.out.println("Bye! (Quack-Quack!)");
                    return;
                }
                default: {
                    System.out.println("Invalid choice");
                    break;
                }
            }
        }
    }
}