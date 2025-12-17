package org.example.model.Users;

public class SwimmingDuck extends Duck implements Inotator {
    public SwimmingDuck(String username, String password, String email, String tip, double viteza, double rezistenta) {
        super(username, password, email, tip, viteza, rezistenta);
    }

    @Override
    public void inoata(){
        return;
    }
}
