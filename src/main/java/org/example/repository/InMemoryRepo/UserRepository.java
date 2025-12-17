package org.example.repository.InMemoryRepo;
import org.example.model.Users.Duck;
import org.example.model.Users.User;
import org.example.utils.RepoException.RepoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class UserRepository implements Repository<String, User> {
    private final Map<String, User> users;

    public UserRepository(Map<String, User> users) {
        this.users = users;
    }

    @Override
    public void save(User user) {
        if (users.containsKey(user.getUsername()))
            throw new RepoException("User with username " + user.getUsername() + " already exists");
        for (User value : users.values())
            if (value.getId() == user.getId())
                throw new RepoException("User with id " + user.getId() + " already exists");
        users.put(user.getUsername(), user);
    }

    @Override
    public void delete(String username) {
        if(!users.containsKey(username))
            throw new RepoException("User with username " + username + " does not exist");
        users.remove(username);
    }

    @Override
    public User findOne(String username) {
        if (!users.containsKey(username))
            throw new RepoException("User with username " + username + " does not exist");
        return users.get(username);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public List<Duck> getDucks() {
        List<Duck> ducks = new ArrayList<>();
        for (User user : users.values())
            if (user instanceof Duck)
                ducks.add((Duck) user);
        return ducks;
    }
}
