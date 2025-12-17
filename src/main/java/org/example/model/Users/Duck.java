package org.example.model.Users;

public abstract class Duck extends User {
    private final DuckType tip;
    private final double viteza;
    private final double rezistenta;

    public Duck(String username, String password, String email, String tip, double viteza, double rezistenta) {
        super(username, password, email);
        this.tip = DuckType.valueOf(tip);
        this.viteza = viteza;
        this.rezistenta = rezistenta;
    }

    public DuckType getTip() {
        return tip;
    }

    public double getViteza() {
        return viteza;
    }

    public double getRezistenta() {
        return rezistenta;
    }
}
