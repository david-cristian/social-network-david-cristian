package org.example.model.Carduri;

import org.example.model.Users.Duck;
import org.example.utils.ModelException.CardException;

import java.util.ArrayList;
import java.util.List;

public abstract class Card<T extends Duck> {
    private long id;
    private final String nume;
    protected List<T> membri = new ArrayList<>();

    public Card(String nume) {
        this.id = -1;
        this.nume = nume;
    }

    public long getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    public List<T> getMembri() {
        return membri;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPerformantaMedie(){
        if (membri.isEmpty())
            return 0;
        double performanta = 0;
        for (T memb : membri)
            performanta += (memb.getViteza() + memb.getViteza()) / 2;
        return performanta / membri.size();
    }

    public void adaugaMembru(T duck){
        if (membri.contains(duck))
           throw new CardException("Member already exists.");
        membri.add(duck);
    }

    public void removeMembru(T duck){
        if (!membri.contains(duck))
            throw new CardException("Member does not exist.");
        membri.remove(duck);
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", nume='" + nume + '\'' +
                ", membri=" + membri +
                '}';
    }
}
