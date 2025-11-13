package org.example.model;

import org.example.utils.CardException;
import org.example.utils.RepoException;

import java.util.ArrayList;
import java.util.List;

public abstract class Card<T extends Duck> {
    /**
     *
     *
     */
    private final long id;
    private String nume;
    protected List<T> membri = new ArrayList<>();

    public Card(long id, String nume) {
        this.id = id;
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
