package org.example.repository.FileRepository;

import org.example.model.Friendhsip.Friendship;
import org.example.model.Users.User;
import org.example.repository.DatabaseRepo.UserDBRepository;
import org.example.repository.InMemoryRepo.FriendshipRepository;
import org.example.repository.InMemoryRepo.UserRepository;
import org.example.utils.ModelException.UserException;

import java.io.*;
import java.util.HashSet;

public class FileFriendshipRepository extends FriendshipRepository {
    private final String fName;
    private final UserDBRepository userRepo;

    public FileFriendshipRepository(String fName, UserDBRepository userRepo) {
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

                User user1 = userRepo.findOne(username1).orElseThrow(() -> new UserException("User not found"));
                User user2 = userRepo.findOne(username2).orElseThrow(() -> new UserException("User not found"));

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
                bw.write(f.getIdUser1() + "," + f.getIdUser2());
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

