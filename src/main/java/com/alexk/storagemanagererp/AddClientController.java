package com.alexk.storagemanagererp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class AddClientController {
    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private void initialize() {

    }
    @FXML
    private void handleSaveButtonAction() {
        // Handle save button action
        String name = nameField.getText();
        String email = emailField.getText();
        System.out.println("Saving client: " + name + ", " + email);
    }

    @FXML
    private void handleCancelButtonAction() {
        // Handle cancel button action
        System.out.println("Cancel button clicked");
    }
}
