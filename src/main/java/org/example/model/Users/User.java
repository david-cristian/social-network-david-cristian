package org.example.model.Users;

import org.example.model.Event.Observer;
import org.example.validation.ValidationStrategy;

import java.util.Objects;


public abstract class User implements Observer {

    protected long id;
    protected final String username;
    protected final String password;
    protected final String email;
    protected ValidationStrategy validationStrategy;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public void setValidationStrategy(ValidationStrategy validationStrategy) {
        this.validationStrategy = validationStrategy;
    }

    public ValidationStrategy getValidationStrategy() {
        return validationStrategy;
    }

    @Override
    public void update(String message) {
        System.out.println(username + " received:  " + message);
    }
}
