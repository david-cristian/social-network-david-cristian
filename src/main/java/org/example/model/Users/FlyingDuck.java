package org.example.model.Users;

public class FlyingDuck extends Duck implements Zburator {
    public FlyingDuck(String username, String password, String email, String tip, double viteza, double rezistenta) {
        super(username, password, email, tip, viteza, rezistenta);
    }

    @Override
    public void zboara(){
        return;
    }
}
