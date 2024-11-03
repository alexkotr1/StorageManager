package com.alexk.storagemanagererp.storage;

import com.alexk.storagemanagererp.cellFactories.AdjustableQuantityCellFactory;
import com.alexk.storagemanagererp.storage.StorageItem;
import com.alexk.storagemanagererp.cellFactories.ProductSearchCellFactory;
import com.alexk.storagemanagererp.cellFactories.RemoveButtonCellFactory;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class SaleStorageItem extends StorageItem {
    private IntegerProperty timesInCheckoutProperty;
    private Integer timesInCheckout;

    public SaleStorageItem(String ID, String itemName, Double price, Integer quantity, String barcode, Integer timesSold, Date lastPurchase, Integer timesInCheckout) {
        super(ID, itemName, price != null ? price : 0.0, quantity != null ? quantity : 0, barcode, timesSold, lastPurchase);
        this.timesInCheckoutProperty = new SimpleIntegerProperty(timesInCheckout != null ? timesInCheckout : 0);
        this.timesInCheckout = timesInCheckout != null ? timesInCheckout : 0;
    }

    public IntegerProperty timesInCheckoutProperty() {
        return timesInCheckoutProperty;
    }
    public Integer getTimesInCheckout() {
        return timesInCheckoutProperty.get();
    }

    public void setTimesInCheckout(Integer timesInCheckout) {
        this.timesInCheckoutProperty.set(timesInCheckout);
        this.timesInCheckout = timesInCheckout;
    }
    public static SaleStorageItem fromStorageItem(StorageItem storageItem) {
        return new SaleStorageItem(
                storageItem.getID(),
                storageItem.getItemName(),
                storageItem.getPrice(),
                storageItem.getQuantity(),
                storageItem.getBarcode(),
                storageItem.getTimesSold(),
                storageItem.getLastPurchase(),
                1
        );
    }
    public static StorageItem toStorageItem(StorageItem storageItem) {
        return new StorageItem(
                storageItem.getID(),
                storageItem.getItemName(),
                storageItem.getPrice(),
                storageItem.getQuantity(),
                storageItem.getBarcode(),
                storageItem.getTimesSold(),
                storageItem.getLastPurchase()
        );
    }
    public static TableView<SaleStorageItem> convertToTableView(List<SaleStorageItem> list, Consumer<SaleStorageItem> addItem) {
        TableView<SaleStorageItem> tableView = new TableView<>(FXCollections.observableList(list));
        TextField searchField = new TextField("test");

        TableColumn<SaleStorageItem, String> nameColumn = new TableColumn<>("Όνομα προϊόντος");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        nameColumn.setCellFactory(new ProductSearchCellFactory(tableView, addItem));

        TableColumn<SaleStorageItem, Integer> timesInCheckoutColumn = new TableColumn<>("Ποσότητα");
        timesInCheckoutColumn.setCellValueFactory(new PropertyValueFactory<>("timesInCheckout"));
        timesInCheckoutColumn.setCellFactory(new AdjustableQuantityCellFactory(tableView));

        TableColumn<SaleStorageItem, Integer> quantityColumn = new TableColumn<>("Διαθέσιμη ποσότητα");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<SaleStorageItem, Double> priceColumn = new TableColumn<>("Τιμή");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<SaleStorageItem, String> barcodeColumn = new TableColumn<>("Barcode");
        barcodeColumn.setCellValueFactory(new PropertyValueFactory<>("barcode"));

        TableColumn<SaleStorageItem, Integer> timesSoldColumn = new TableColumn<>("Πωλήσεις");
        timesSoldColumn.setCellValueFactory(new PropertyValueFactory<>("timesSold"));

        TableColumn<SaleStorageItem, Date> lastPurchaseColumn = new TableColumn<>("Ημερομηνία τελευταίας πώλησης");
        lastPurchaseColumn.setCellValueFactory(new PropertyValueFactory<>("lastPurchase"));

        TableColumn<SaleStorageItem, Void> removeButtonColumn = new TableColumn<>("");
        removeButtonColumn.setCellFactory(new RemoveButtonCellFactory());

        tableView.getColumns().addAll(nameColumn, priceColumn, timesInCheckoutColumn,quantityColumn,barcodeColumn,timesSoldColumn, lastPurchaseColumn, removeButtonColumn);
        tableView.setSelectionModel(null);
        tableView.setEditable(false);

        return tableView;
    }

}
