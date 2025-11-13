package org.example.repository;

import org.example.model.Friendship;
import org.example.model.User;

import java.io.*;
import java.util.HashSet;

public class FileFriendshipRepository extends FriendshipRepository {
    private final String fName;
    private final UserRepository userRepo;

    public FileFriendshipRepository(String fName, UserRepository userRepo) {
        super(new HashSet<>());
        this.fName = fName;
        this.userRepo = userRepo;
        loadData();
    }

    private void loadData() {
        File file = new File(fName);
        if(!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(fName))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                String username1 = parts[0].trim();
                String username2 = parts[1].trim();

                User user1 = userRepo.findOne(username1);
                User user2 = userRepo.findOne(username2);

                if (user1 != null && user2 != null) {
                    super.addFriendship(user1, user2);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading friendships file: " + e.getMessage());
        }
    }

    private void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fName))) {
            bw.write("username1,username2");
            bw.newLine();

            for (Friendship f : getFriendships()) {
                bw.write(f.getUser1().getUsername() + "," + f.getUser2().getUsername());
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error writing friendships file: " + e.getMessage());
        }
    }
    @Override
    public void addFriendship(User user1, User user2) {
        super.addFriendship(user1, user2);
        saveData();
    }

    @Override
    public void removeFriendship(User user1, User user2) {
        super.removeFriendship(user1, user2);
        saveData();
    }
}

