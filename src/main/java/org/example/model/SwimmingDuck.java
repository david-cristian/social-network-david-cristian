package org.example.model;

public class SwimmingDuck extends Duck implements Inotator{
    public SwimmingDuck(long id, String username, String password, String email, String tip, double viteza, double rezistenta) {
        super(id, username, password, email, tip, viteza, rezistenta);
    }

    @Override
    public void inoata(){
        return;
    }
}
