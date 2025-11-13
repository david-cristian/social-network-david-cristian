package org.example.model;

import org.example.validation.PersonValidationStrategy;
import org.example.validation.ValidationStrategy;

import java.time.LocalDate;

public class Person extends User{
    private final String nume;
    private final String prenume;
    private final LocalDate dataNasterii;
    private final String ocupatie;
    private final int nivelEmpatie;

    public Person(long id, String username, String password, String email, String nume, String prenume, LocalDate dataNasterii, String ocupatie, int nivelEmpatie) {
        super(id, username, password, email);
        this.nume = nume;
        this.prenume = prenume;
        this.ocupatie = ocupatie;
        this.dataNasterii = dataNasterii;
        this.nivelEmpatie = nivelEmpatie;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public String getOcupatie() {
        return ocupatie;
    }

    public LocalDate getDataNasterii() {
        return dataNasterii;
    }

    public int getNivelEmpatie() {
        return nivelEmpatie;
    }

    @Override
    public String toString() {
        return "Person{" +
                "nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", ocupatie='" + ocupatie + '\'' +
                ", dataNasterii=" + dataNasterii +
                ", nivelEmpatie=" + nivelEmpatie +
                '}';
    }
}
