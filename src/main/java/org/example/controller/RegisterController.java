package org.example.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.example.service.Service;
import org.example.utils.ValidationException;
import javafx.stage.Stage;

public class RegisterController {
    private Service service;
    private boolean isDuckMode = true;

    @FXML private TextField usernameField, emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private ToggleButton togglePasswordBtn;

    @FXML private VBox duckFieldsContainer;
    @FXML private ComboBox<String> duckTypeComboBox;
    @FXML private TextField speedField;
    @FXML private TextField staminaField;

    @FXML private VBox personFieldsContainer;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField occupationField;
    @FXML private TextField empathyField;
    @FXML private DatePicker birthdayPicker;

    @FXML private Button btnLeft, btnRight;
    @FXML private Label userTypeLabel;

    public void setService(Service service) {
        this.service = service;

        duckTypeComboBox.getItems().setAll("FLYING", "SWIMMING", "FLYING_AND_SWIMMING");
        duckTypeComboBox.getSelectionModel().selectFirst();

        updateSwitcherUI(false);

        initPasswordToggle();
    }

    private void initPasswordToggle() {
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

            passwordTextField.setVisible(true);
            passwordField.setManaged(true);
        }
    }

    @FXML
    public void onSwitchRight() {
        isDuckMode = false;
        updateSwitcherUI(true);
    }

    @FXML
    public void onSwitchLeft() {
        isDuckMode = true;
        updateSwitcherUI(true);
    }

    private void updateSwitcherUI(boolean animate) {
        if(isDuckMode) {
            userTypeLabel.setText("Duck");
            btnLeft.setVisible(false);
            btnRight.setVisible(true);

            if (animate) {
                switchContainers(personFieldsContainer, duckFieldsContainer);
            } else {
                showContainer(duckFieldsContainer, true);
                showContainer(personFieldsContainer, false);
            }
        } else {
            userTypeLabel.setText("Person");
            btnLeft.setVisible(true);
            btnRight.setVisible(false);

            if (animate) {
                switchContainers(duckFieldsContainer, personFieldsContainer);
            } else {
                showContainer(personFieldsContainer, true);
                showContainer(duckFieldsContainer, false);
            }
        }
    }

    private void switchContainers(VBox toHide, VBox toShow) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), toHide);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(event -> {
            showContainer(toHide, false);
            showContainer(toShow, true);
            toShow.setOpacity(0.0);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(100), toShow);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void showContainer(VBox container, boolean show) {
        container.setVisible(show);
        container.setManaged(show);
        if (show) {
            container.setOpacity(1.0);
        }
    }

    @FXML private void onRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();

        try {
            if (isDuckMode) {
                String duckType = duckTypeComboBox.getValue().toString();
                String speed = speedField.getText();
                String stamina = staminaField.getText();
                service.add("duck", username, password, email, duckType, speed, stamina);
            } else {
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String birthday = birthdayPicker.getValue() != null ? birthdayPicker.getValue().toString() : "";
                String occupation = occupationField.getText();
                String empathy = empathyField.getText();

                service.add("person", username, password, email, firstName, lastName, birthday, occupation, empathy);
            }

            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Success", "User created!");
            onCancel();
        } catch (ValidationException ve) {
            MessageAlert.showMessage(null, ve.getMessage());
        } catch (NumberFormatException nfe) {
            MessageAlert.showMessage(null, "Please insert valid numbers for speed/stamina/empathy!");
        } catch (Exception e) {
            MessageAlert.showMessage(null, "Error: " +  e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
