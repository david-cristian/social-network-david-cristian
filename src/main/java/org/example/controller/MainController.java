package org.example.controller;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.model.FriendRequest.FriendRequest;
import org.example.model.FriendRequest.RequestStatus;
import org.example.model.Messages.Message;
import org.example.model.Users.User;
import org.example.service.Service;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class MainController {
    private Service service;
    private User currentUser;
    @FXML private Label userLabel;
    @FXML private DuckController duckController;
    @FXML private ListView<String> friendsListView;
    @FXML private TextField friendUsernameField;
    @FXML private Label communitiesCountLabel;
    @FXML private ListView<String> mostSocialCommunityList;
    @FXML private TabPane tabPane;
    @FXML private Tab tabFriends;
    @FXML private Tab tabSettings;
    @FXML private ListView<Message> chatListView;
    @FXML private TextField messageInput;
    @FXML private Label replyLabel;
    @FXML private Tab tabNotifications;
    @FXML private ListView<FriendRequest> requestListView;

    private Timeline autoRefreshTimeline;
    private String selectedChatPartner;
    private Message messageToReply = null;

    public void initialize() {
        friendsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadChatHistory(newValue);
                selectedChatPartner = newValue;
                messageToReply = null;
                replyLabel.setVisible(false);
            }
        });

        chatListView.setCellFactory(param -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String displayText = item.getText();
                    if (item.getReply() != null) {
                        displayText = "[Replying to: " + item.getReply().getText() + "]\n" + displayText;
                    }
                    setText(displayText);

                    if (item.getFrom().getUsername().equals(currentUser.getUsername())) {
                        setStyle("-fx-alignment: CENTER-RIGHT; -fx-background-color: #dcf8c6; -fx-padding: 5px;");
                        setAlignment(Pos.CENTER_LEFT);
                    } else {
                        setStyle("-fx-alignment: CENTER_LEFT; -fx-background-color: #ffffff; -fx-padding: 5px; -fx-background-radius: 5px;");
                        setAlignment(Pos.CENTER_LEFT);
                    }
                }
            }
        });
    }

    public void init(Service service, User currentUser) {
        this.service = service;
        this.currentUser = currentUser;

        if (duckController != null) {
            duckController.setService(service);
        }

        if (currentUser == null) {
            userLabel.setText("Guest Mode (Read-Only)");
            tabFriends.setDisable(true);
            tabSettings.setDisable(true);
            initNotifications();
            startAutoRefresh();
        } else {
            userLabel.setText("Logged in as " +  currentUser.getUsername());
            loadFriends();
            tabFriends.setDisable(false);
            tabSettings.setDisable(false);
            loadFriends();
            initNotifications();
            startAutoRefresh();
        }
    }

    private void startAutoRefresh() {
        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
            loadRequests();
            loadFriends();;
            if (selectedChatPartner != null) {
                refreshConversation();
            }
        }));
        autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        autoRefreshTimeline.play();
    }

    private void initNotifications() {
        requestListView.setCellFactory(param -> new ListCell<FriendRequest>() {
            @Override
            protected void updateItem(FriendRequest request, boolean empty) {
                super.updateItem(request, empty);

                if (empty || request == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox(10);
                    Label label = new Label(request.getFrom().getUsername() + " wants to be friends.");
                    Pane spacer = new Pane();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button btnAcceptFriend = new Button("✔");
                    btnAcceptFriend.setStyle("-fx-text-fill: green; -fx-background-color: #e8f5e9;");
                    btnAcceptFriend.setOnAction(event -> handleAccept(request));

                    Button btnReject = new Button("✘");
                    btnReject.setStyle("-fx-text-fill: red; -fx-background-color: #ffebee;");
                    btnReject.setOnAction(event -> handleReject(request));

                    hBox.getChildren().addAll(label, spacer, btnAcceptFriend, btnReject);
                    setGraphic(hBox);
                }
            }
        });
        loadRequests();
    }

    private void handleAccept(FriendRequest request) {
        requestListView.getItems().remove(request);
        updateNotificationBadge(requestListView.getItems().size());
        try {
            service.respondToRequest(request, RequestStatus.APPROVED);
            loadRequests();
            loadFriends();
        } catch (Exception e) {
            MessageAlert.showMessage(null, "Error in approval: " + e.getMessage());
            loadRequests();
        }
    }
    private void handleReject(FriendRequest request) {
        requestListView.getItems().remove(request);
        updateNotificationBadge(requestListView.getItems().size());
        try {
            service.respondToRequest(request, RequestStatus.REJECTED);
            //loadRequests();
        } catch (Exception e) {
            MessageAlert.showMessage(null, "Error in rejecting: " + e.getMessage());
            loadRequests();
        }
    }

    private void loadRequests() {
        List<FriendRequest> pending = service.getPendingRequests(currentUser.getUsername());
        requestListView.getItems().setAll(pending);
        updateNotificationBadge(pending.size());
    }

    private void updateNotificationBadge(int count) {
        if (count == 0) {
            tabNotifications.setText("Notifications");
            tabNotifications.setGraphic(null);
            return;
        }

        Label titleLabel = new Label("Notifications");
        titleLabel.setStyle("-fx-text-fill: black;");

        Label badgeLabel = new Label(String.valueOf(count));
        badgeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");

        StackPane badgePane = new StackPane(new Circle(8, Color.RED), badgeLabel);

        HBox content = new HBox(5, titleLabel, badgePane);
        content.setAlignment(Pos.CENTER);

        tabNotifications.setText("");
        tabNotifications.setGraphic(content);
    }


    @FXML
    public void onSendMessage() {
        String text = messageInput.getText();

        if (text.equals("")) {
            MessageAlert.showMessage(null, "You can't send empty message.");
            return;
        }
        if (selectedChatPartner == null) {
            MessageAlert.showMessage(null, "Select a friend to chat with.");
            return;
        }

        try {
            service.sendMessage(currentUser.getUsername(), List.of(selectedChatPartner), text, messageToReply);
            messageInput.clear();
            messageToReply = null;
            replyLabel.setText("Reply to: ");
            replyLabel.setVisible(false);
            refreshConversation();
        } catch (Exception e) {
            MessageAlert.showMessage(null, "Error sending message: " + e.getMessage());
        }
    }

    private void refreshConversation() {
        if (selectedChatPartner != null) {
            List<Message> conversation = service.getConversation(currentUser.getUsername(), selectedChatPartner);
            if (conversation.size() != chatListView.getItems().size()) {
                chatListView.getItems().setAll(conversation);
                chatListView.scrollTo(conversation.size() - 1);
            }
        }
    }

    @FXML
    public void onMessageSelected() {
        Message selected = chatListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            this.messageToReply = selected;
            replyLabel.setText("Reply to: " + selected.getText());
            replyLabel.setVisible(true);
        }
    }
    @FXML

    private void loadChatHistory (String friendUsername) {
        if (currentUser == null) return;
        try {
            List<Message> conversation = service.getConversation(currentUser.getUsername(), friendUsername);
            chatListView.getItems().setAll(conversation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFriends() {
        if (currentUser == null) return;
        try {
            Set<String> friends = service.getFriends(currentUser.getUsername());
            if (friends.size() != friendsListView.getItems().size()) {
                friendsListView.setItems(FXCollections.observableArrayList(friends));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onAddFriend() {
        if (currentUser == null) {
            MessageAlert.showMessage(null, "Log in required!");
            return;
        }

        String friendUsername = friendUsernameField.getText();
        try {
            service.sendFriendRequest(currentUser.getUsername(), friendUsername);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Success", "Request sent!");
            friendUsernameField.clear();
        } catch (Exception e) {
            MessageAlert.showMessage(null, e.getMessage());
        }
    }

    @FXML
    public void onRemoveFriend() {
        String selectedFriend = friendsListView.getSelectionModel().getSelectedItem();
        if (selectedFriend == null) {
            MessageAlert.showMessage(null, "Select a friend!");
            return;
        }
        try {
            service.removeFriend(currentUser.getUsername(), selectedFriend);
            loadFriends();
        } catch (Exception e) {
            MessageAlert.showMessage(null, e.getMessage());
        }
    }

    @FXML
    public void onCommunitiesTabSelected() {
        try {
            int count = service.getNumberOfCommunities();
            communitiesCountLabel.setText(String.valueOf(count));
            var mostSocial = service.getMostSociableCommunity();
            if (mostSocial != null) {
                mostSocialCommunityList.setItems(FXCollections.observableArrayList(mostSocial.first));
            }
        } catch (Exception e) {
            communitiesCountLabel.setText("0");
        }
    }

    @FXML
    public void onDeleteAccount() {
        if (currentUser == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this account?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                service.deleteUser(currentUser.getUsername());
                onLogout();
            } catch (Exception e) {
                MessageAlert.showMessage(null, e.getMessage());
            }
        }
    }

    @FXML
    public void onLogout() {
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop();
        }
        try {
            Stage currentStage = (Stage) userLabel.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/views/login-view.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setService(this.service);
            Stage loginStage = new Stage();
            loginStage.setTitle("Duck Social Network - Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();

            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
