package com.alexk.storagemanagererp.cellFactories;

import com.alexk.storagemanagererp.storage.SaleStorageItem;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class RemoveButtonCellFactory implements Callback<TableColumn<SaleStorageItem, Void>, TableCell<SaleStorageItem, Void>> {

    @Override
    public TableCell<SaleStorageItem, Void> call(TableColumn<SaleStorageItem, Void> param) {
        return new TableCell<SaleStorageItem, Void>() {
            private Button removeButton = null;

            {
                // No need to initialize removeButton here
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView() == null || getTableView().getItems().get(getIndex()).getItemName() == null) {
                    setGraphic(null);
                } else {
                    if (removeButton == null) {
                        removeButton = new Button("Αφαίρεση");
                        removeButton.setOnAction(event -> {
                            SaleStorageItem product = getTableView().getItems().get(getIndex());
                            getTableView().getItems().remove(product);
                        });
                    }
                    setGraphic(removeButton);
                }
            }
        };
    }
}
