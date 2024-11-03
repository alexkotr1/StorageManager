package com.alexk.storagemanagererp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class UserPreferences {
    private static final String FILE_PATH = "preferences.properties";

    public static void savePreference(String key, String value) {
        Properties properties = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream(FILE_PATH)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            // Handle file not found or other IO exceptions
            e.printStackTrace();
        }

        properties.setProperty(key, value);

        try (FileOutputStream fileOutputStream = new FileOutputStream(FILE_PATH)) {
            properties.store(fileOutputStream, "User Preferences");
        } catch (IOException e) {
            // Handle IO exceptions
            e.printStackTrace();
        }
    }

    public static String getPreference(String key) {
        Properties properties = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream(FILE_PATH)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            // Handle file not found or other IO exceptions
            e.printStackTrace();
        }

        return properties.getProperty(key);
    }
}
