package com.alexk.storagemanagererp;

import java.util.Random;

public class BarcodeGenerator {
    public static String generateEAN13Barcode() {
        Random random = new Random();

        // Generate the first 12 digits randomly
        StringBuilder barcodeBuilder = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            barcodeBuilder.append(random.nextInt(10));
        }

        String barcodeDigits = barcodeBuilder.toString();

        // Calculate and append the check digit
        int checkDigit = calculateEAN13CheckDigit(barcodeDigits);
        barcodeDigits += checkDigit;

        return barcodeDigits;
    }

    public static int calculateEAN13CheckDigit(String barcodeDigits) {
        int sum = 0;

        // Calculate the sum of odd and even digits
        for (int i = 0; i < barcodeDigits.length(); i++) {
            int digit = Character.getNumericValue(barcodeDigits.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        // Calculate the check digit
        int checkDigit = (10 - (sum % 10)) % 10;

        return checkDigit;
    }
}
