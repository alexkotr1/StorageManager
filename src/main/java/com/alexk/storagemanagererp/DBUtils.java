package com.alexk.storagemanagererp;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.*;

public class DBUtils {
    private static final String url = "jdbc:postgresql://localhost:5432/mydatabase";
    private static final String username = "alex";
    private static final String password = "alex";

    private static DataSource dataSource;
    private static DataSource testSource;

    public static void initDB() throws SQLException {
        dataSource = createDataSource();
    }

    public static Connection testConnection(String url, String username, String password) throws SQLException{
        testSource = createTestSource(url, username, password);
        return testSource.getConnection();
    }
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            dataSource = createDataSource();
        }
        return dataSource.getConnection();
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Failed to close the connection: " + e.getMessage());
            }
        }
    }

    public static boolean tableExists(String tableName) throws SQLException {
        try (Connection connection = getConnection()) {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet resultSet = metadata.getTables(null, null, tableName, null)) {
                return resultSet.next();
            }
        }
    }

    public static void createStorageItemTable() throws SQLException {
        String query = "CREATE TABLE storage_items (" +
                "id VARCHAR(255) PRIMARY KEY," +
                "item_name VARCHAR(255) NOT NULL," +
                "price DOUBLE PRECISION NOT NULL," +
                "quantity INTEGER NOT NULL," +
                "barcode VARCHAR(255) NOT NULL," +
                "times_sold INTEGER," +
                "last_purchase DATE" +
                ")";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }

    public static void createSalesTable() throws SQLException {
        String query = "CREATE TABLE sales (" +
                "id SERIAL PRIMARY KEY," +
                "total NUMERIC(10, 2)," +
                "date TIMESTAMP," +
                "item_ids VARCHAR(255)[] " +
                "CHECK (array_length(item_ids, 1) > 0)" +
                ")";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }

    private static DataSource createDataSource() {
        String settingsDBName = UserPreferences.getPreference("db_name");
        String settingsDBUsername = UserPreferences.getPreference("db_username");
        String settingsDBPassword = UserPreferences.getPreference("db_password");
        String settingsDBAddress = UserPreferences.getPreference("db_address");
        String settingsDBPort = UserPreferences.getPreference("db_port");

        BasicDataSource basicDataSource = new BasicDataSource();
        if (settingsDBName != null && settingsDBUsername != null && settingsDBPassword != null && settingsDBAddress != null && settingsDBPort != null){
            basicDataSource.setUrl("jdbc:postgresql://" + settingsDBAddress + ":" + settingsDBPort + "/" + settingsDBName);
            basicDataSource.setUsername(settingsDBUsername);
            basicDataSource.setPassword(settingsDBPassword);
        }
        else {
            basicDataSource.setUrl(url);
            basicDataSource.setUsername(username);
            basicDataSource.setPassword(password);
        }

        basicDataSource.setInitialSize(5);
        return basicDataSource;
    }
    private static DataSource createTestSource(String url, String username, String password){
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
        basicDataSource.setInitialSize(5);
        return basicDataSource;
    }
}
