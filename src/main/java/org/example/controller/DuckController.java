package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Users.Duck;
import org.example.model.Users.DuckType;
import org.example.model.Users.User;
import org.example.service.Service;
import org.example.utils.Events.EntityChangeEvent;
import org.example.utils.Events.EntityChangeEventType;
import org.example.utils.Observer.Observer;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

import java.util.List;
import java.util.stream.Collectors;

public class DuckController implements Observer<EntityChangeEvent> {

    private Service service;
    private int pageSize = 5;
    private int currentPage = 0;

    @FXML
    private TableView<Duck> tableView;

    @FXML
    private TableColumn<Duck, String> tableColumnUsername;

    @FXML
    private TableColumn<Duck, String> tableColumnEmail;

    @FXML
    private TableColumn<Duck, DuckType> tableColumnType;

    @FXML
    private TableColumn<Duck, Double> tableColumnSpeed;

    @FXML
    private TableColumn<Duck, Double> tableColumnStamina;

    @FXML
    private Pagination pagination;

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        initPagination();
    }

    @FXML
    public void initialize() {
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        tableColumnType.setCellValueFactory(new PropertyValueFactory<>("tip"));

        tableColumnSpeed.setCellValueFactory(new PropertyValueFactory<>("viteza"));
        tableColumnStamina.setCellValueFactory(new PropertyValueFactory<>("rezistenta"));
    }

    private void initPagination() {
        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            currentPage = newValue.intValue();
            loadPage();
        });
        loadPage();
    }

    private void loadPage() {
        Pageable pageable = new Pageable(currentPage, pageSize);
        Page<User> page = service.findAllUsers(pageable);
        int totalItems = page.getTotalElemenCount();
        int pageCount = (int) Math.ceil((double) totalItems / pageSize);
        if (pageCount == 0)
            pageCount = 1;
        pagination.setPageCount(pageCount);

        List<Duck> ducksOnPage = page.getElementsOnPage()
                .stream()
                .filter(u -> u instanceof Duck)
                .map(u -> (Duck) u)
                .collect(Collectors.toList());

        tableView.setItems(FXCollections.observableArrayList(ducksOnPage));
    }

    @Override
    public void update(EntityChangeEvent event) {
        loadPage();
    }
}