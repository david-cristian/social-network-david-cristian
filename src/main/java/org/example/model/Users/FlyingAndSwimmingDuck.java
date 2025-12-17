package org.example.model.Users;

public class FlyingAndSwimmingDuck extends Duck implements InotatorSiZburator {
    public FlyingAndSwimmingDuck(String username, String password, String email, String tip, double viteza, double rezistenta) {
        super(username, password, email, tip, viteza, rezistenta);
    }
    @Override
    public void inoataSauZboara() {
        return;
    }
}
