package org.example.controller;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.model.Users.User;
import org.example.service.Service;
import org.example.utils.PasswordHasher;

import java.io.IOException;

public class LoginController {
    private Service service;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ImageView duckLogoImage;
    @FXML private TextField passwordTextField;
    @FXML private ToggleButton togglePasswordBtn;

    public void setService(Service service){
        this.service = service;
    }

    @FXML
    public void initialize() {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), duckLogoImage);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.LINEAR);
        rotateTransition.setAxis(Rotate.Y_AXIS);
        rotateTransition.play();

        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    public void onTogglePassword() {
        if (togglePasswordBtn.isSelected()) {
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);

            passwordField.setVisible(true);
            passwordField.setManaged(true);
        }
    }

    @FXML
    private void onLoginAction(){
        String username = usernameField.getText();
        String rawPassword = passwordField.getText();

        try {
            User user = service.findUser(username);

            if (user != null) {
                String inputHash = PasswordHasher.hash(rawPassword);

                if (user.getPassword().equals(inputHash)) {
                    openMainView(user);
                } else {
                    MessageAlert.showMessage(null, "Incorrect data!");
                }
            } else {
                    MessageAlert.showMessage(null, "User doesn't exist!");
            }
        } catch (Exception e) {
            MessageAlert.showMessage(null, "Login failed: " + e.getMessage());
        }
    }

    @FXML
    public void onGuestAction(){
        openMainView(null);
    }

    @FXML
    public void onRegisterAction(){
         try {
             FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/views/register-view.fxml"));
             Parent root = loader.load();

             RegisterController registerController = loader.getController();
             registerController.setService(service);
             Stage registerStage = new Stage();
             registerStage.setTitle("Register new user");
             registerStage.setScene(new Scene(root));
            registerStage.show();
         } catch (IOException e) {
             e.printStackTrace();
             MessageAlert.showMessage(null, "Erron on opening regiser window." + e.getMessage());
         }
    }

    private void openMainView(User user){
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/views/main-view.fxml"));
            BorderPane root = loader.load();

            MainController controller = loader.getController();
            controller.init(service, user);

            Stage stage = new Stage();
            stage.setTitle("Duck Social Network - " + (user != null ? user.getUsername() : "Guest"));
            stage.setScene(new Scene(root));
            stage.show();

            ((Stage) usernameField.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
