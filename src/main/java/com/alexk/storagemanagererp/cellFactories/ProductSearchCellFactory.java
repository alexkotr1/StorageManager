package com.alexk.storagemanagererp.cellFactories;

import com.alexk.storagemanagererp.Helper;
import com.alexk.storagemanagererp.Sale;
import com.alexk.storagemanagererp.storage.StorageItem;
import com.alexk.storagemanagererp.storage.SaleStorageItem;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProductSearchCellFactory implements Callback<TableColumn<SaleStorageItem, String>, TableCell<SaleStorageItem, String>> {

    private TableView<SaleStorageItem> tableView;
    private Stage stage;
    private Consumer<SaleStorageItem> addItem;

    public ProductSearchCellFactory(TableView<SaleStorageItem> tableView,  Consumer<SaleStorageItem> addItem) {
        this.tableView = tableView;
        this.stage = stage;
        this.addItem = addItem;
    }

    @Override
    public TableCell<SaleStorageItem, String> call(TableColumn<SaleStorageItem, String> param) {
        return new TableCell<SaleStorageItem, String>() {
            private TextField textField;

            {
                textField = new TextField();
                textField.setPromptText("Αναζήτηση...");
                textField.setOnAction(event -> {
                    String newValue = textField.getText();
                    Helper.log(newValue);
                    commitEdit(newValue);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (isEmpty() || getTableRow() == null || getTableRow().getItem().getItemName() != null) {
                    setText(item);
                    setGraphic(null);
                } else {
                    setText(null);
                    setGraphic(textField);
                }
            }

            @Override
            public void startEdit() {
                if (!isEmpty() && getTableRow().getIndex() == tableView.getItems().size() - 1) {
                    super.startEdit();
                    textField.setText(getString());
                    setText(null);
                    setGraphic(textField);
                    textField.requestFocus();
                    textField.selectAll();
                }
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getString());
                setGraphic(null);
            }

            @Override
            public void commitEdit(String newValue) {
                super.commitEdit(newValue);
                List<StorageItem> items = StorageItem.DBSearchItem(newValue);
                if (items.size() == 0) {
                    Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή","Δεν βρέθηκαν αποτελέσματα!");
                    return;
                }
                else if (items.size() == 1){
                    addItem.accept(SaleStorageItem.fromStorageItem(items.get(0)));
                    return;
                }
                StorageItem.TableViewFromSearch(items,item ->{
                    addItem.accept(SaleStorageItem.fromStorageItem(item));
                    return null;
                });

            }

            private String getString() {
                return getItem() == null ? "" : getItem().toString();
            }
        };
    }
}
