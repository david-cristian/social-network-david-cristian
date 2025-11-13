package org.example.repository;
import org.example.model.Duck;
import org.example.model.Person;
import org.example.model.User;
import org.example.service.ConcreteUserFactory;

import java.io.*;
import java.util.HashMap;

public class FileUserRepository extends UserRepository {
        private final String fName;
        private final ConcreteUserFactory factory =  new ConcreteUserFactory();

    public FileUserRepository(String fName) {
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
                User user;

                if (type.equals("person")) {
                    user = factory.create("person",  f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9]);
                } else if (type.equals("duck")) {
                    user = factory.create("duck", f[1], f[2], f[3], f[4], f[5], f[6], f[7]);
                } else continue;

                super.save(user);
            }
        } catch (IOException e){
            System.out.println("Error loading CSV: "  + e.getMessage());
        }
    }

    private void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fName))) {
            bw.newLine();
            for (User u : findAll()) {
                if (u instanceof Person p) {
                    bw.write(String.join(",", "person",
                            String.valueOf(p.getId()), p.getUsername(), p.getPassword(), p.getEmail(),
                            p.getNume(), p.getPrenume(), p.getDataNasterii().toString(),
                            p.getOcupatie(), String.valueOf(p.getNivelEmpatie())));
                } else if (u instanceof Duck d) {
                    bw.write(String.join(",", "duck",
                            String.valueOf(d.getId()), d.getUsername(), d.getPassword(), d.getEmail(),
                            d.getTip().name(),
                            String.valueOf(d.getViteza()),
                            String.valueOf(d.getRezistenta())));
                }
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing CSV: " + e.getMessage());
        }
    }

    @Override
    public void save(User user) {
        super.save(user);
        saveData();
    }

    @Override
    public void delete(String username){
        super.delete(username);
        saveData();
    }
}
