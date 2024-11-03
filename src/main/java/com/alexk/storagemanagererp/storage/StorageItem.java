package com.alexk.storagemanagererp.storage;

import com.alexk.storagemanagererp.DBUtils;
import com.alexk.storagemanagererp.Helper;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.sql.PreparedStatement;

public class StorageItem {
    private String itemName;
    private double price;
    private int quantity;
    private String barcode;
    private Integer timesSold;
    private Date lastPurchase;
    private String ID;

    public StorageItem(String ID,String itemName, double price, int quantity, String barcode, Integer timesSold, Date lastPurchase) {
        this.ID = ID != null ? ID : UUID.randomUUID().toString();
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.barcode = barcode;
        this.timesSold = timesSold;
        this.lastPurchase = lastPurchase;
    }
    @Override
    public String toString() {
        return "StorageItem{" +
                "itemName='" + itemName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StorageItem other = (StorageItem) obj;
        if (barcode == null || other.barcode == null) return false;
        return barcode.equals(other.barcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(barcode);
    }
    public static TableView<StorageItem> toTableView(List<StorageItem> list) {
        TableView<StorageItem> tableView = new TableView<>(FXCollections.observableList(list));

        TableColumn<StorageItem, String> nameColumn = new TableColumn<>("Όνομα προϊόντος");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<StorageItem, Double> priceColumn = new TableColumn<>("Τιμή");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<StorageItem, String> barcodeColumn = new TableColumn<>("Barcode");
        barcodeColumn.setCellValueFactory(new PropertyValueFactory<>("barcode"));

        TableColumn<StorageItem, Integer> quantityColumn = new TableColumn<>("Διαθέσιμη ποσότητα");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<StorageItem, Integer> timesSoldColumn = new TableColumn<>("Πωλήσεις");
        timesSoldColumn.setCellValueFactory(new PropertyValueFactory<>("timesSold"));

        TableColumn<StorageItem, Date> lastPurchaseColumn = new TableColumn<>("Ημερομηνία τελευταίας πώλησης");
        lastPurchaseColumn.setCellValueFactory(new PropertyValueFactory<>("lastPurchase"));

        tableView.getColumns().addAll(nameColumn, priceColumn, barcodeColumn, quantityColumn, timesSoldColumn, lastPurchaseColumn);
        return tableView;
    }
    public void add() throws SQLException {
        runQuery("INSERT INTO storage_items (item_name, price, quantity, barcode, times_sold, last_purchase, ID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)");
    }

    public void update() throws SQLException {
        runQuery("UPDATE storage_items SET item_name = ?, price = ?, quantity = ?, barcode = ?, " +
                "times_sold = ?, last_purchase = ? WHERE id = ?");
    }
    public void delete() throws SQLException {
        try (Connection connection = DBUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM storage_items WHERE id = ?")) {
            if (!DBUtils.tableExists("storage_items")){
                DBUtils.createStorageItemTable();
                return;
            }
            statement.setString(1, ID);
            statement.executeUpdate();
        }
    }
    public static boolean barcodeExists(String barcode) throws SQLException {
        String query = "SELECT COUNT(*) FROM storage_items WHERE barcode = ?";

        try (Connection connection = DBUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, barcode);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    public static boolean nameExists(String name) throws SQLException{
        String query = "SELECT COUNT(*) FROM storage_items WHERE item_name = ?";
        try (Connection connection = DBUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }
    void runQuery(String query) throws SQLException {
        if (!DBUtils.tableExists("storage_items")){
            DBUtils.createStorageItemTable();
        }
        try (Connection connection = DBUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, itemName);
            statement.setDouble(2, price);
            statement.setInt(3, quantity);
            statement.setString(4, barcode);
            statement.setInt(5, timesSold);
            statement.setDate(6,lastPurchase != null ? new java.sql.Date(lastPurchase.getTime()) : null);
            statement.setString(7, ID);
            statement.executeUpdate();
        }
    }
    public static List<StorageItem> DBSearchItem(String searchText){
        if (Helper.isValidBarcode(searchText)){
            String query = "SELECT * FROM storage_items WHERE barcode = ?";
            List<StorageItem> items = new ArrayList<>();
            try (Connection connection = DBUtils.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, searchText);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String id = resultSet.getString("ID");
                        String itemName = resultSet.getString("item_name");
                        double price = resultSet.getDouble("price");
                        int quantity = resultSet.getInt("quantity");
                        Integer timesSold = resultSet.getInt("times_sold");
                        Date lastPurchase = resultSet.getDate("last_purchase");
                        StorageItem item = new StorageItem(id, itemName, price, quantity, searchText, timesSold, lastPurchase);
                        items.add(item);
                    }
                }
            } catch (SQLException e) {
                Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή",e.getMessage());
                return null;
            }

            return items;
        }
        else {
                String query = "SELECT * FROM storage_items WHERE item_name LIKE ?";
                List<StorageItem> items = new ArrayList<>();

                try (Connection connection = DBUtils.getConnection();
                     PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, "%" + searchText + "%");

                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            String id = resultSet.getString("ID");
                            String foundItemName = resultSet.getString("item_name");
                            double price = resultSet.getDouble("price");
                            int quantity = resultSet.getInt("quantity");
                            String barcode = resultSet.getString("barcode");
                            Integer timesSold = resultSet.getInt("times_sold");
                            Date lastPurchase = resultSet.getDate("last_purchase");
                            StorageItem item = new StorageItem(id, foundItemName, price, quantity, barcode, timesSold, lastPurchase);
                            items.add(item);
                        }
                    }
                } catch (SQLException e) {
                    Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή",e.getMessage());
                    return null;
                }

            return items;
        }
    }
    public static void TableViewFromSearch(List<StorageItem> items, Callback<StorageItem, Void> callback) {
        Stage stage = new Stage();
        TableView<StorageItem> tableView = StorageItem.toTableView(items);
        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                StorageItem item = tableView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    callback.call(item);
                    stage.close();
                }
            }
        });
        VBox root = new VBox(tableView);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(root);
        Scene scene = new Scene(borderPane, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Αποτελέσματα");
        stage.show();
    }
    public static StorageItem retrieveItem(String barcode) throws SQLException {
        String query = "SELECT * FROM storage_items WHERE barcode = ?";
        try (Connection connection = DBUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, barcode);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String itemName = resultSet.getString("item_name");
                    String ID = resultSet.getString("ID");
                    Double price = resultSet.getDouble("price");
                    Integer quantity = resultSet.getInt("quantity");
                    Integer timesSold = resultSet.getInt("times_sold");
                    Date lastPurchase = resultSet.getDate("last_purchase");
                    return new StorageItem(ID,itemName,price,quantity,barcode,timesSold,lastPurchase);
                }
            }
        }
        return null;
    }


    public void setID(String ID){
        this.ID = ID;
    }
    public String getID(){
        return ID;
    }
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Integer getTimesSold() {
        return timesSold;
    }

    public void setTimesSold(Integer timesSold) {
        this.timesSold = timesSold;
    }

    public Date getLastPurchase() {
        return lastPurchase;
    }

    public void setLastPurchase(Date lastPurchase) {
        this.lastPurchase = lastPurchase;
    }

}
