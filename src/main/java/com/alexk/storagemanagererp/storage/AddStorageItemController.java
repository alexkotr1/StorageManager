package com.alexk.storagemanagererp.storage;

import com.alexk.storagemanagererp.BarcodeGenerator;
import com.alexk.storagemanagererp.Helper;
import com.alexk.storagemanagererp.storage.StorageItem;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.w3c.dom.Text;

import java.sql.SQLException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class AddStorageItemController {
    @FXML
    private TextField itemNameField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField barcodeField;

    @FXML
    private void handleSaveButtonAction(){
        if (itemNameField.getText().isEmpty()){
            Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή","Το πεδίο «Όνομα» δεν μπορεί να είναι άδειο!");
            return;
        }
        else if (barcodeField.getText().isEmpty()){
            Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή","Το πεδίο «Barcode» δεν μπορεί να είναι άδειο!");
            return;
        }
        else if (!Helper.isValidBarcode(barcodeField.getText())){
            Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή","Το πεδίο «Barcode» δεν είναι έγκυρο!");
            return;
        }
        String itemName = itemNameField.getText();
        Integer quantity = null;
        Double price = null;
        try {
            quantity = Integer.parseInt(quantityField.getText());
        }catch (Exception e){
            Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή","Το πεδίο «Ποσότητα» δεν είναι έγκυρο!");
            return;
        }
        try {
            price = Double.parseDouble(priceField.getText());
        }catch (Exception e){
            Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή","Το πεδίο «Τιμή» δεν είναι έγκυρο!");
            return;
        }
        StorageItem item = new StorageItem(null,itemName,price,quantity,barcodeField.getText(),0,null);
        try {
            if (StorageItem.nameExists(itemName)){
                Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή","Αυτό το προϊόν υπάρχει ήδη!");
                return;
            }
        } catch (SQLException e){
            Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή",e.getMessage());
            return;
        }
        try {
            item.add();
        } catch(SQLException e){
            Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή",e.getMessage());
            return;
        }
        Helper.showAlert(Alert.AlertType.CONFIRMATION,"Επιτυχία","Το προϊόν προστέθηκε επιτυχώς!");
        System.out.println(item);
    }

    @FXML
    private void handleCreateBarcode(){
        String barcode = BarcodeGenerator.generateEAN13Barcode();
        try {
            if (StorageItem.barcodeExists(barcode)){
                handleCreateBarcode();
            }
            else {
                barcodeField.setText(barcode);
            }
        }catch(SQLException e){
            Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή",e.getMessage());
        }
    }
}
