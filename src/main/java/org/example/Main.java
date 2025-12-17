package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.LoginController;
import org.example.repository.DatabaseRepo.*;
import org.example.service.Factory.CardFactory;
import org.example.service.Factory.ConcreteCardFactory;
import org.example.service.Factory.ConcreteUserFactory;
import org.example.service.Factory.UserFactory;
import org.example.service.Service;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        UserDBRepository userRepo = new UserDBRepository();
        FriendshipDBRepository friendRepo = new FriendshipDBRepository();
        CardDBRepository cardRepo = new CardDBRepository();
        RaceEventDBRepository raceRepo = new RaceEventDBRepository();
        MessageDBRepository messageDBRepository = new MessageDBRepository(userRepo);
        RequestDBRepository requestDBRepository = new RequestDBRepository(userRepo);
        UserFactory userFactory = new ConcreteUserFactory();
        CardFactory cardFactory = new ConcreteCardFactory();

        Service service = new Service(userRepo, friendRepo, cardRepo, raceRepo, messageDBRepository, requestDBRepository, userFactory, cardFactory);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/views/login-view.fxml"));
        Parent root = loader.load();

        LoginController controller = loader.getController();
        controller.setService(service);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Duck Social Network - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}