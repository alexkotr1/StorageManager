package com.alexk.storagemanagererp;

import com.alexk.storagemanagererp.storage.StorageItem;
import com.alexk.storagemanagererp.storage.EditStorageItemController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class MenuBarManager {
    @FXML
    private static final MenuBar menuBar = new MenuBar();

    public static MenuBar getMenuBar() {
        return menuBar;
    }

    public static void configureMenuBar(Stage stage, FXMLLoader fxmlLoader) {
        menuBar.getMenus().clear();
        /*
        Menu clientMenu = new Menu("Πελάτες");
        MenuItem createClient = new MenuItem("Προσθήκη Πελάτη");
        MenuItem viewClient = new MenuItem("Προβολή Πελάτη");
        MenuItem editClient = new MenuItem("Επεξεργασία Πελάτη");
        createClient.setOnAction(e -> NavigationController.showFXML("clients/add-client-view.fxml","Προσθήκη Πελάτη"));
        clientMenu.getItems().addAll(createClient,viewClient,editClient);
         */
        Menu storageMenu = new Menu("Αποθήκη");
        MenuItem addItem = new MenuItem("Προσθήκη Είδους");
        MenuItem viewItem = new MenuItem("Προβολή Είδους");
        MenuItem editItem = new MenuItem("Επεξεργασία Είδους");
        addItem.setOnAction(e -> {
            try {
                NavigationController.showFXML("storage/add-item.fxml", "Προσθήκη Είδους");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        editItem.setOnAction(e -> search(stage, fxmlLoader));
        viewItem.setOnAction(e -> search(stage, fxmlLoader));
        storageMenu.getItems().addAll(addItem,viewItem,editItem);

        Menu settingsMenu = new Menu("Ρυθμίσεις");
        MenuItem dbSettings = new MenuItem("Βάση δεδομένων");
        dbSettings.setOnAction(e ->{
            try {
                 NavigationController.showFXML("DBSettings.fxml", "Ρυθμίσεις");
            } catch (Exception ex){
                ex.printStackTrace();
            }
        });
        settingsMenu.getItems().addAll(dbSettings);

        Menu retailMenu = new Menu("Λιανική");
        MenuItem salesItem = new MenuItem("Πωλήσεις");
        salesItem.setOnAction(e ->{
            try {
                Checkout controller = NavigationController.showFXML("checkout.fxml", "Ταμείο");
                controller.load(stage);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        });
        retailMenu.getItems().addAll(salesItem);

        menuBar.getMenus().addAll(retailMenu,storageMenu,settingsMenu);
    }
    public static void search(Stage stage, FXMLLoader fxmlLoader){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Αναζήτηση");
        dialog.setHeaderText("Εισάγετε barcode ή όνομα προϊόντος:");
        dialog.showAndWait().ifPresent(text -> {
            List<StorageItem> results = StorageItem.DBSearchItem(text);
            if (results.isEmpty()) {
                Helper.showAlert(Alert.AlertType.ERROR, "Προσοχή", "Δεν βρέθηκαν αποτελέσματα!");
                return;
            }
            TableView<StorageItem> tableView = StorageItem.toTableView(results);
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteMenuItem = new MenuItem("Delete");
            deleteMenuItem.setOnAction(event -> {
                StorageItem item = tableView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationDialog.setTitle("Επιβεβαίωση");
                    confirmationDialog.setHeaderText("Διαγραφή προϊόντος;");
                    confirmationDialog.setContentText("Είστε σίγουροι πως θέλετε να διαγράψετε αυτό το προϊόν;");

                    Optional<ButtonType> result = confirmationDialog.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            item.delete();
                            tableView.getItems().remove(item);
                        } catch (SQLException e) {
                            Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή",e.getMessage());
                        }
                    }
                }
            });
            contextMenu.getItems().addAll(deleteMenuItem);
            tableView.setContextMenu(contextMenu);
            tableView.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    StorageItem item = tableView.getSelectionModel().getSelectedItem();
                    if (item != null) {
                        System.out.println("Double-clicked row: " + item.getItemName());
                        openEditView(fxmlLoader,stage,item);
                    }
                }
            });
            tableView.setRowFactory(tv ->{
                TableRow<StorageItem> row = new TableRow<>();
                row.setOnContextMenuRequested(event ->{
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                });
                return row;
            });
            VBox root = new VBox(tableView);
            BorderPane borderPane = new BorderPane();
            borderPane.setTop(MenuBarManager.getMenuBar());
            borderPane.setCenter(root);
            Scene scene = new Scene(borderPane, stage.getWidth(), stage.getHeight());
            MenuBarManager.configureMenuBar(stage, fxmlLoader);
            stage.setScene(scene);
            stage.setTitle("Αποτελέσματα");
            stage.show();
        });
    }
    private static void openEditView(FXMLLoader fxmlLoader, Stage stage, StorageItem item) {
        try {
            FXMLLoader editLoader = new FXMLLoader(NavigationController.class.getResource("storage/edit-item.fxml"));
            Parent editRoot = editLoader.load();

            EditStorageItemController editController = editLoader.getController();
            editController.setItem(item);
            BorderPane borderPane = new BorderPane();
            borderPane.setTop(MenuBarManager.getMenuBar());
            borderPane.setCenter(editRoot);
            Scene scene = new Scene(borderPane, stage.getWidth(), stage.getHeight());
            MenuBarManager.configureMenuBar(stage, fxmlLoader);
            stage.setScene(scene);
            stage.setTitle("Αποτελέσματα");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
