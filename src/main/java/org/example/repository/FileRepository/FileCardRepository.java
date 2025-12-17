package org.example.repository.FileRepository;

import org.example.model.Carduri.Card;
import org.example.model.Carduri.CardInotatori;
import org.example.model.Carduri.CardZburatori;
import org.example.repository.InMemoryRepo.CardRepository;
import org.example.service.Factory.ConcreteCardFactory;

import java.io.*;
import java.util.HashMap;

public class FileCardRepository extends CardRepository {
    private final String fName;
    private final ConcreteCardFactory cardFactory = new ConcreteCardFactory();

    public FileCardRepository(String fName) {
        super(new HashMap<>());
        this.fName = fName;
        loadData();
    }

    private void loadData(){
        try (BufferedReader br = new BufferedReader(new FileReader(fName))) {
            String line;
            br.readLine();
            while((line = br.readLine()) != null){
                String[] f = line.split(",");
                String type = f[0];
                Card card;

                if (type.equals("zburatori")) {
                    card = cardFactory.create("zburatori",  f[1], f[2]);
                } else if (type.equals("duck")) {
                    card = cardFactory.create("inotatori", f[1], f[2]);
                } else continue;

                super.save(card);
            }
        } catch (IOException e){
            System.out.println("Error loading CSV: "  + e.getMessage());
        }
    }

    private void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fName))) {
            bw.newLine();
            for (Card c : getCarduri()) {
                if (c instanceof CardZburatori cz) {
                    bw.write(String.join(",", "person",
                            String.valueOf(cz.getId()), cz.getNume()));
                } else if (c instanceof CardInotatori ci) {
                    bw.write(String.join(",", "duck",
                            String.valueOf(ci.getId()), ci.getNume()));
                }
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing CSV: " + e.getMessage());
        }
    }

    public void save(Card card) {
        super.save(card);
        saveData();
    }

    public void deleteCard(Card card) {
        super.deleteCard(card);
        saveData();
    }
}
