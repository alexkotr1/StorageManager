package com.alexk.storagemanagererp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationController {

private static Stage stage = null;
private static Number width = null;
private static Number height = null;
private static Boolean listenerRunning = false;
    public static void setInitialStage(Stage primaryStage) {
        stage = primaryStage;
        stage.setMaximized(true);
        if (listenerRunning == false){
            addSizeListener();
        }
    }

    private static void addSizeListener() {
        listenerRunning = true;
        stage.widthProperty().addListener((obs, oldWidth, newWidth) -> {
               width = newWidth;
        });

        stage.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            height = newHeight;
        });
    }

    public static <T> T showFXML(String fxmlPath, String title) {
        FXMLLoader fxmlLoader = new FXMLLoader(NavigationController.class.getResource(fxmlPath));
        try {
            Parent root = fxmlLoader.load();
            Scene scene = null;
            StackPane stackPane = new StackPane();
            BorderPane borderPane = new BorderPane();
            borderPane.setTop(MenuBarManager.getMenuBar());
            borderPane.setCenter(root);
            scene = new Scene(borderPane);
            stackPane.getChildren().add(scene.getRoot());
            stage.setTitle("ΚΟΤΡΩΤΣΙΟΣ ERP");
            stage.setScene(new Scene(stackPane));
            stage.setWidth(width != null ? (Double) width : Screen.getPrimary().getVisualBounds().getWidth());
            stage.setHeight(height != null ? (Double) height : Screen.getPrimary().getVisualBounds().getHeight());
            stage.show();
            MenuBarManager.configureMenuBar(stage, fxmlLoader);
            return fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
