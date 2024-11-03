package com.alexk.storagemanagererp;

import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import com.alexk.storagemanagererp.storage.StorageItem;
import com.alexk.storagemanagererp.storage.SaleStorageItem;
import javafx.collections.ObservableList;


public class Sale {
    private Double total;
    private Date date;
    private static ObservableList<SaleStorageItem> items;

    public Sale(ObservableList<SaleStorageItem> items) {
        this.items = items;
        this.date = Date.from((LocalDateTime.now()).atZone(ZoneId.systemDefault()).toInstant());
        this.total = calculateTotal();
    }

    public void add(SaleStorageItem item){
        this.items.add(item);
        this.total = calculateTotal();
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ObservableList<SaleStorageItem> getItems() {
        return items;
    }

    public void setItems(ObservableList<SaleStorageItem> items) {
        this.items = items;
        this.total = calculateTotal();
    }
    public void execute() throws Exception{
        for (SaleStorageItem product : items){
            StorageItem item = SaleStorageItem.toStorageItem(product);
            if (item.getQuantity() < product.getTimesInCheckout()){
                throw new Exception("Το προϊόν " + item.getItemName() + " δεν είναι διαθέσιμο" + (item.getQuantity() == 0 ? "." : " στην επιθυμητή ποσότητα." ));
            }
            else {
                item.setQuantity(item.getQuantity() - product.getTimesInCheckout());
                item.setLastPurchase(new Date());
                item.setTimesSold(item.getTimesSold() + product.getTimesInCheckout());
                item.update();
                this.saveSale();
            }
        }
    }
    public List<String> getItemIds() {
        List<String> itemIds = new ArrayList<>();
        for (SaleStorageItem item : items) {
            itemIds.add(item.getID());
        }
        return itemIds;
    }
    public void saveSale() throws SQLException {
        try (Connection connection = DBUtils.getConnection()) {
            if (!DBUtils.tableExists("sales")){
                DBUtils.createSalesTable();
            }
            String insertSaleQuery = "INSERT INTO sales (total, date, item_ids) VALUES (?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertSaleQuery, Statement.RETURN_GENERATED_KEYS)) {
                insertStatement.setDouble(1, this.getTotal());
                insertStatement.setTimestamp(2, new Timestamp(this.getDate().getTime()));
                insertStatement.setArray(3, connection.createArrayOf("VARCHAR", this.getItemIds().toArray(new String[0])));
                insertStatement.executeUpdate();
            }
        }
    }




    private Double calculateTotal() {
        Double total = 0.0;
        for (StorageItem item : items) {
            total += item.getPrice();
        }
        return total;
    }
}
