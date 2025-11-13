package org.example.model;

public class FlyingDuck extends Duck implements Zburator{
    public FlyingDuck(long id, String username, String password, String email, String tip, double viteza, double rezistenta) {
        super(id, username, password, email, tip, viteza, rezistenta);
    }

    @Override
    public void zboara(){
        return;
    }
}
