package org.example.model;

public class FlyingAndSwimmingDuck extends Duck implements InotatorSiZburator {
    public FlyingAndSwimmingDuck(long id, String username, String password, String email, String tip, double viteza, double rezistenta) {
        super(id, username, password, email, tip, viteza, rezistenta);
    }
    @Override
    public void inoataSauZboara() {
        return;
    }
}
