package com.alexk.storagemanagererp;

import com.alexk.storagemanagererp.storage.SaleStorageItem;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import com.alexk.storagemanagererp.storage.StorageItem;

public class Checkout extends Application {
    private AtomicReference<TableView<SaleStorageItem>> atomicTableView = new AtomicReference<>(new TableView<>());
    private TableView<SaleStorageItem> tableView = new TableView<>();
    private List<SaleStorageItem> items;
    private Label totalLabel = new Label();
    private StackPane root = new StackPane();
    private SaleStorageItem nullItem;
    private Stage stage = null;

    @Override
    public void start(Stage stage) {
        Helper.log("Starting");
        load(stage);
    }
     public void load(Stage stage){
        try {
            this.stage = stage;
            tableView = atomicTableView.get();
            items = tableView.getItems();
            NavigationController.setInitialStage(stage);
            NavigationController.showFXML("checkout.fxml", "Αρχική");
            DBUtils.getConnection();

            totalLabel.setText("Σύνολο: 0€");
            totalLabel.setFont(new Font(24));

            Button checkoutButton = new Button("Ταμείο");
            checkoutButton.setStyle("-fx-font-size: 18px; -fx-min-width: 120px; -fx-min-height: 40px;");
            checkoutButton.setOnAction(event -> {
                Sale sale = new Sale(atomicTableView.get().getItems());
                try {
                    sale.execute();
                    atomicTableView.get().getItems().clear();
                    atomicTableView.get().requestFocus();
                } catch (Exception e) {
                    Helper.showAlert(Alert.AlertType.ERROR, "Προσοχή", e.getMessage());
                }
            });

            HBox totalAndCheckoutBox = new HBox(totalLabel, checkoutButton);
            totalAndCheckoutBox.setAlignment(Pos.BASELINE_RIGHT);
            totalAndCheckoutBox.setSpacing(100);
            totalAndCheckoutBox.setPadding(new Insets(5));

            VBox bottomVBox = new VBox(totalAndCheckoutBox);
            bottomVBox.setAlignment(Pos.BOTTOM_RIGHT);
            bottomVBox.setSpacing(10);
            bottomVBox.setPadding(new Insets(10));

            root = new StackPane(tableView);

            BorderPane borderPane = new BorderPane();
            borderPane.setTop(MenuBarManager.getMenuBar());
            borderPane.setCenter(root);
            borderPane.setBottom(bottomVBox);

            nullItem = new SaleStorageItem(null,null,null,null,null,null,null,null);

            Scene scene = new Scene(borderPane);
            stage.setScene(scene);
            addItem(nullItem);
            tableView.getItems().addListener((ListChangeListener<SaleStorageItem>) change -> updateTotalLabel());
            stage.show();
            final BarcodeReader[] finalBarcodeReader = new BarcodeReader[1];
            finalBarcodeReader[0] = new BarcodeReader(scene, () -> {
                String barcode = finalBarcodeReader[0].getBarcode();
                try {
                    StorageItem item = StorageItem.retrieveItem(barcode);
                    if (item == null){
                        Helper.showAlert(Alert.AlertType.ERROR, "Προσοχή", "Αυτό το προϊόν δεν βρέθηκε!");
                        return;
                    }
                    addItem(item);
                } catch (SQLException e) {
                    Helper.showAlert(Alert.AlertType.ERROR, "Προσοχή", e.getMessage());
                }
            });
        } catch (SQLException e) {
            Helper.showAlert(Alert.AlertType.ERROR, "Προσοχή", e.getMessage());
        }

    }
    public void updateTotalLabel() {
        double total = 0;
        for (SaleStorageItem product : atomicTableView.get().getItems()) {
            total += product.getPrice() * (product.getTimesInCheckout() == 0 || product.getTimesInCheckout() == null ? 1 : product.getTimesInCheckout());
        }
        totalLabel.setText("Σύνολο: " + Double.toString(total) + "€");
    }

    public void addItem(StorageItem item) {
        if (item.getItemName() != null) {
            SaleStorageItem saleItem = SaleStorageItem.fromStorageItem(item);
            saleItem.timesInCheckoutProperty().addListener((observable, oldValue, newValue) -> updateTotalLabel());
            if (items.contains(saleItem)) {
                int index = items.indexOf(saleItem);
                SaleStorageItem existingItem = items.get(index);
                existingItem.setTimesInCheckout(existingItem.getTimesInCheckout() + 1);
            } else atomicTableView.get().getItems().add(saleItem);
        }
        items.remove(nullItem);
        items.add(nullItem);
        tableView.setItems(FXCollections.observableList(items));
        root.getChildren().remove(atomicTableView.get());
        atomicTableView.set(SaleStorageItem.convertToTableView(items, this::addItem));
        root.getChildren().add(atomicTableView.get());
    }

    public static void main(String[] args) {
        launch();
        Helper.log("MAIN");
    }
}
