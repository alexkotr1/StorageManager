package com.alexk.storagemanagererp;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.security.Key;
import java.util.Date;

public class BarcodeReader {

    private static String barcode = "";
    private Runnable barcodeListener;
    private Date lastKeyEventTime = null;
    public BarcodeReader(Scene scene, Runnable barcodeListener) {
        this.barcodeListener = barcodeListener;
        scene.addEventHandler(KeyEvent.KEY_TYPED, this::handleBarcodeInput);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, this::handleBarcodeInput);
    }

    private void handleBarcodeInput(KeyEvent event) {
        Date currentTime = new Date();
        if (lastKeyEventTime != null && currentTime.getTime() - lastKeyEventTime.getTime() > 100) {
            barcode = "";
            lastKeyEventTime = null;
            return;
        }


        if (event.getCode() == KeyCode.ENTER) {
            lastKeyEventTime = null;
                barcodeListener.run();
                barcode = ""; // Reset the barcode for the next input
        } else {
            String input = event.getText();
            if (input.matches("\\d")) {
                lastKeyEventTime = currentTime;
                barcode += input;
            }
        }
        event.consume();
    }

    public String getBarcode() {
        return barcode.toString();
    }
}
