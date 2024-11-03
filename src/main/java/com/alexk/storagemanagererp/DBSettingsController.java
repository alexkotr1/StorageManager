package com.alexk.storagemanagererp;

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

public class DBSettingsController {
    @FXML
    private TextField dbName;

    @FXML
    private TextField dbUsername;

    @FXML
    private TextField dbPassword;

    @FXML
    private TextField dbAddress;

    @FXML
    private TextField dbPort;

    @FXML
    private void handleSaveButtonAction(){
        String name = dbName.getText();
        String username = dbUsername.getText();
        String password = dbPassword.getText();
        String address = dbAddress.getText();
        String port = dbPort.getText();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || address.isEmpty() || port.isEmpty()){
            Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή","Κανένα πεδίο δεν μπορεί να είναι άδειο!");
        }

         String url = "jdbc:postgresql://" + dbAddress.getText() + ":" + dbPort.getText() + "/" + dbName.getText();
        Helper.log(url);
        try{
            Helper.log(dbUsername.getText());
            Helper.log(dbPassword.getText());
            DBUtils.testConnection(url,dbUsername.getText(),dbPassword.getText());
            Helper.showAlert(Alert.AlertType.CONFIRMATION,"Επιτυχία","Η σύνδεση ήταν επιτυχής!");
        } catch (SQLException ex){
            Helper.showAlert(Alert.AlertType.ERROR,"Προσοχή",ex.getMessage());
        }
            UserPreferences.savePreference("db_name",name);
            UserPreferences.savePreference("db_username",username);
            UserPreferences.savePreference("db_password",password);
            UserPreferences.savePreference("db_address",address);
            UserPreferences.savePreference("db_port",port);


    }
}
