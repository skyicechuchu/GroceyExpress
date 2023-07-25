package users;

import deliveryservice.*;
import sqlhandler.sqlHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StoreManager extends User {

    private String storeName;
    private String account;
    private Store store;

    public StoreManager(String account, String store) {
        super(account);
        ResultSet resultSet = sqlHandler.query("SELECT * FROM StoreManager WHERE account = ? ", account);
        try {
            while (resultSet.next()) {
                this.account  = resultSet.getString("account");
                this.storeName = resultSet.getString("storeName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.account = account;
        this.storeName = store;
    }


    public Store getStore() {
        return new Store(this.storeName);
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void addItem(String itemName, String weight, String storeName) {
        String insertSql = "INSERT INTO Item (name, weight, store_name) VALUES (?, ?, ?)";
        sqlHandler.insert(insertSql, itemName, Integer.parseInt(weight), storeName);
    }


    public void addDrone(String store_name, int id, int capacity, int remaining_cap, int delivery_before_refuel, int number_orders, String current_pilot_id) {
        String insertSql = "INSERT INTO Drone (store_name,id,capacity,remaining_cap,delivery_before_refuel,number_orders,current_pilot_id)" +
                "VALUES (?,?,?,?,?,?,?)";
        sqlHandler.insert(insertSql, store_name, id, capacity, remaining_cap, delivery_before_refuel, number_orders, current_pilot_id);
    }

    public void display_drones() throws SQLException {
        ResultSet resultSet = sqlHandler.query("SELECT * FROM Drone Order by id");

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            int capacity = resultSet.getInt("capacity");
            int number_orders = resultSet.getInt("number_orders");
            int remaining_cap = resultSet.getInt("remaining_cap");
            int delivery_before_refuel = resultSet.getInt("delivery_before_refuel");
            String fly_by = resultSet.getString("current_pilot_id");

            if (fly_by.equals("noPilot")){
                System.out.println("storename:"+ storeName + ", drone id:" + id + ", capacity:" + capacity+ ", number_orders:" +  number_orders
                        + ", remaining_cap: " + remaining_cap + ", delivery_before_refuel:" + delivery_before_refuel);
            } else {
                System.out.println("storename:"+ storeName + ", drone id:" + id + ", capacity:" + capacity+ ", number_orders:" +  number_orders
                        + ", remaining_cap: " + remaining_cap + ", delivery_before_refuel:" + delivery_before_refuel + ", flown by:" + fly_by);
            }

        }

    }

    public void fly_drone(String storeName,int droneID, String account){
        String updatePilot = "UPDATE Pilot SET current_drone_id = ? WHERE account = ? ";
        sqlHandler.update(updatePilot, droneID, account);

        String updateDrone = "UPDATE Drone SET current_pilot_id = ? WHERE store_name =? and id = ? ";
        sqlHandler.update(updateDrone, account,storeName, droneID);
    }

    public void display_orders(String storeName) throws SQLException {
        String queryOrder = "SELECT o.*, u.firstName, u.lastName, u.phone " +
                "FROM Orders AS o " +
                "JOIN Store AS s ON o.store_name = s.name " +
                "JOIN User AS u ON o.customer_id = u.account " +
                "WHERE s.name = ? " +
                "ORDER BY o.id";
        ResultSet resultSet = sqlHandler.query(queryOrder, storeName);
        while (resultSet.next()) {
            int orderId = resultSet.getInt("id");
            int droneId = resultSet.getInt("drone_id");
            String firstName = resultSet.getString("firstName");
            String lastName = resultSet.getString("lastName");
            String phone = resultSet.getString("phone");
            System.out.println("Order ID: " + orderId + ", Store Name: " + storeName + ", Drone ID: " + droneId +
                    ", Customer: " + firstName + " " + lastName + ", Phone: " + phone);

            String queryLine = "SELECT * FROM Line WHERE store_name = ? AND order_id = ? ORDER BY item_name";
            ResultSet resultSetLines = sqlHandler.query(queryLine, storeName, orderId);
            while (resultSetLines.next()) {
                String item_name = resultSetLines.getString("item_name");
                int quantity = resultSetLines.getInt("quantity");
                int weight = resultSetLines.getInt("weight");
                int price = resultSetLines.getInt("price");
                Line line = new Line(item_name, quantity, weight, price, orderId, storeName);
                System.out.println("item_name:"+item_name+",total_quantity:"+Integer.toString(quantity)+
                        ",total_cost:"+Integer.toString(price)+",total_weight:"+Integer.toString(weight));
            }

        }
    }

    public String getStoreName(){
        return storeName;
    }

}
