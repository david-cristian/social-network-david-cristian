package org.example;

import org.example.repository.*;
import org.example.service.ConcreteCardFactory;
import org.example.service.ConcreteUserFactory;
import org.example.service.Service;
import org.example.ui.Ui;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        FileUserRepository userRepo = new FileUserRepository("users.csv");
        FileFriendshipRepository friendRepo = new FileFriendshipRepository("friendships.csv", userRepo);
        FileCardRepository cardRepo = new FileCardRepository("cards.csv");
        RaceEventRepository raceEventRepo = new RaceEventRepository();
        RaceHistoryRepository raceHistoryRepo = new RaceHistoryRepository();
        ConcreteUserFactory factory = new ConcreteUserFactory();
        ConcreteCardFactory cardFactory = new ConcreteCardFactory();
        Service service = new Service(userRepo, friendRepo, cardRepo, raceEventRepo, raceHistoryRepo, factory, cardFactory);
        Ui ui = new Ui(service);
        ui.run();
    }
}
