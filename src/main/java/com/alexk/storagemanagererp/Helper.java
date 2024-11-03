package com.alexk.storagemanagererp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Helper {
    private static Stage stage = null;
    private static Scene scene = null;
    public static StackPane stackPane = null;

    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        try {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public static void initStage(){
        stage = new Stage();
    }

    public static <T> void log(T value) {
            System.out.println(value);
    }


    public static boolean isValidBarcode(String barcode) {
        String digitsOnly = barcode.replaceAll("\\D", "");
        if (digitsOnly.length() != 13) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < digitsOnly.length() - 1; i++) {
            int digit = Character.getNumericValue(digitsOnly.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checksum = 10 - (sum % 10);

        return checksum == Character.getNumericValue(digitsOnly.charAt(digitsOnly.length() - 1));
    }
}
