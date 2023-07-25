package users;

import deliveryservice.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import sqlhandler.sqlHandler;

public class Customer extends User {
    private String account;
    private String firstName;
    private String lastName;
    private String phone;
    private int angryBirdID;
    private int rating;
    private int credits;
    private int available_credits;
    private int[] location;

//    public Customer(String account, String firstName, String lastName,String phone, int rating, int credits){
//        this.account = account;
//        this.firstName =firstName;
//        this.lastName = lastName;
//        this.phone = phone;
//        this.rating = rating;
//        this.credits = credits;
//        this.available_credits = this.credits;
//        this.angryBirdID = -1;
//        this.location = new int[2];
//    }



    public Customer(String account, int rating, int credits, int available_credits, int location_x, int location_y, int angryBirdID){
        super(account);
        this.account = account;
        this.rating = rating;
        this.credits = credits;
        this.available_credits = available_credits;
        this.angryBirdID = angryBirdID;
        this.location = new int[]{location_x, location_y};
    }

    public Customer(String account){
        super(account);
        ResultSet resultSet = sqlHandler.query("SELECT * FROM Customer WHERE account = ? ", account);
        try {
            while (resultSet.next()) {
                this.account  = resultSet.getString("account");
                this.rating = resultSet.getInt("rating");
                this.credits = resultSet.getInt("credits");
                this.available_credits = resultSet.getInt("available_credits");
                this.angryBirdID = resultSet.getInt("angryBirdID");
                this.location = new int[]{resultSet.getInt("location_x"), resultSet.getInt("location_y")};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Customer getCustomerByID(String account) throws SQLException {
        return new Customer(account);
    }

    public void removeLines(int orderID, String storename) throws SQLException {
        String deleteSql = "DELETE FROM Line WHERE order_id = ? AND store_name = ? ";
        sqlHandler.update(deleteSql, orderID, storename);

    }

    public void removeOrder(int orderID, String storename) throws SQLException {
        removeLines(orderID,storename);
        String deleteSql = "DELETE FROM Orders WHERE id = ? AND store_name = ? ";
        sqlHandler.update(deleteSql, orderID, storename);
    }
    public void cancelOrder(int orderID, String storename) throws SQLException {
        ResultSet queryCredit = sqlHandler.query("SELECT cost FROM Orders WHERE id = ? AND store_name = ?", orderID, storename);
        if(queryCredit.next()) {
            int cost = queryCredit.getInt("cost");
            this.changeAvailable_credits(cost);
            this.removeOrder(orderID, storename);
        }
    }
    public void requestItem(Order currentOrder, String item_name, Line newLine) throws SQLException {
        // need to change order info etc
        currentOrder.addLine(item_name,newLine);
        this.changeAvailable_credits(-1*newLine.getTotalCost());
    }

    public void removeLines(int orderID) throws SQLException {
        String deleteSql = "DELETE FROM Line WHERE order_id = ? ";
        sqlHandler.update(deleteSql, orderID);
    }

    public void changeCredit(int c) throws SQLException {
        //query curr credit
        ResultSet queryCredit = sqlHandler.query("SELECT credits FROM Customer WHERE account = ?", this.account);
        if(queryCredit.next()) {
            int newCredit = queryCredit.getInt("credits") - c;
            String updateSql = "UPDATE Customer SET credits = ? WHERE account = ? ";
            sqlHandler.update(updateSql, newCredit, account);
        }
        //update credit
    }
    public void changeAvailable_credits(int c) throws SQLException {
        this.available_credits += c;
        ResultSet resultSet = sqlHandler.query("SELECT available_credits FROM Customer WHERE account = ?", this.account);
        if( resultSet.next()) {
            int newCredit = resultSet.getInt("available_credits") + c;
            String updateSql = "UPDATE Customer SET available_credits = ? WHERE account = ? ";
            sqlHandler.update(updateSql, newCredit, this.account);
        }
    }

    public int getAvailable_credits(){
        return this.available_credits;
    }

    public static int getAvailable_creditsByID(String account) throws SQLException {
        ResultSet resultSet = sqlHandler.query("SELECT available_credits FROM Customer WHERE account = ?", account);
        return resultSet.getInt("available_credits");
    }

    public String getAccount() {
        return this.account;
    }
    public void printInfo(){
        System.out.println("name:"+this.firstName + "_" + this.lastName + ",phone:" + this.phone + ",rating:" +
                Integer.toString(this.rating) + ",credit:" + Integer.toString(this.credits));
    }

    public int[] getLocation() throws SQLException {
        int[] res = new int[2];
        ResultSet query1 = sqlHandler.query("SELECT location_x, location_y FROM Customer WHERE account = ?", account);
        if (query1.next()) {
            res[0] = query1.getInt("location_x");
            res[1] = query1.getInt("location_y");
        }
        return res;
    }

    public static int[] getLocation(String account) throws SQLException {
        ResultSet resultSet = sqlHandler.query("SELECT available_credits FROM Customer WHERE account = ?", account);
        int x = resultSet.getInt("location_x");
        int y = resultSet.getInt("location_y");
        return new int[]{x, y};
    }

    public void setLocation(int[] location) {
        this.location = location;
        String updateSql = "UPDATE Customer SET location_x = ?, location_y = ? WHERE account = ? ";
        sqlHandler.update(updateSql, location[0], location[1], this.account);
    }

    public void startOrder(String storeName, String customerID) throws SQLException {
        ResultSet resultDrone = sqlHandler.query("SELECT d.id " +
                "FROM Drone AS d " +
                "JOIN Store AS s ON d.store_name = s.name " +
                "WHERE s.name = ? " +
                "ORDER BY d.remaining_cap DESC " +
                "LIMIT 1", storeName);
        if (!resultDrone.next()) {
            System.out.println("ERROR:available_drone_does_not_exist");
            return;
        }
        int droneId = resultDrone.getInt("id");
        String insertsql = "INSERT INTO Orders (store_name, drone_id, customer_id) VALUES(?, ?, ?)";
        sqlHandler.insert(insertsql, storeName, droneId, customerID);
        String queryID = "SELECT max(id) as id FROM Orders WHERE store_name = ? AND customer_id=? ";
        ResultSet rs = sqlHandler.query(queryID,storeName,customerID);
        if(rs.next()){
            int orderID = rs.getInt("id");
            System.out.println("OK:Change Completed. " +
                    "A new order is created for you at "+ storeName +
                    ". OrderID is "+ orderID);
        }
    }

    public void displayCustomerOrder() throws SQLException {
            String query = "SELECT o.*, u.firstName, u.lastName, u.phone " +
                    "FROM Orders AS o " +
                    "JOIN Store AS s ON o.store_name = s.name " +
                    "JOIN User AS u ON o.customer_id = u.account " +
                    "WHERE u.account = ? " +
                    "ORDER BY o.id";
            ResultSet resultSet = sqlHandler.query(query, account);
            while (resultSet.next()) {
                int orderId = resultSet.getInt("id");
                int droneId = resultSet.getInt("drone_id");
                String storeName = resultSet.getString("store_name");
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
                    line.printLineInfo();
                }
            }
        }


    public void purchaseOrder(String storeName, String orderID) {
        int id = Integer.parseInt(orderID);

    }


//
    public void fulfillOrderAngryBirds(String storeName, int orderID) throws SQLException {
        // 0 let customer know the delivery is happening
        int droneID = -1;
        ResultSet resultSet = sqlHandler.query("SELECT id, drone_id FROM Orders WHERE id = ?", orderID);

        if (resultSet.next()) {
            droneID = resultSet.getInt("drone_id");
            System.out.println("drone " + droneID+ "has left store for delivery");
        }
        Store currentStore = new Store(storeName);

        Order currOrder = Order.getOrderByID(storeName, orderID);
        int totalWeight = currOrder.getTotalWeight();
        int totalCost = currOrder.getTotalCost();
        Drone currDrone = currOrder.getDrone();

        // 1 check whether attack will happen at store
        resultSet = sqlHandler.query("SELECT angryBirdID FROM Store WHERE name = ?", storeName);
        int angryBirdID = -1;
        if (resultSet.next()) {
            angryBirdID = resultSet.getInt("angryBirdID");
        }
        if(angryBirdID != -1) {
            if (AngryBird.willAttack(angryBirdID)) {
                sqlHandler.update("UPDATE StoreAttackByBird SET angryBird_at_store_count = angryBird_at_store_count + 1 " +
                        "WHERE store_name = ?", storeName);
                System.out.println("an angry bird near store has attacked the drone");
                // increase repair count for store
                String updateQuery = "UPDATE Store SET repairs = repairs + 1 WHERE name = ?";
                sqlHandler.update(updateQuery, storeName);
                // will try to transfer the order
                if (currentStore.internalTransfer(totalWeight,orderID, droneID)) {
                    System.out.println("has transferred your order to another drone for quick re-delivery");
                    fulfillOrderAngryBirds(storeName, orderID);
                    return;
                } else {
                    // no transfer available wait until repair complete
                    System.out.println("trying to repair drone for re-delivery");
                    System.out.println("we are sorry for the long wait");
                    System.out.println("repair complete" + "/n" +  "out for re-delivery");
                }
            }
        }
        System.out.println("delivery approaching customer's location");

        //2 check whether the attack will happen at customer's

        String customerAccount = null;
        resultSet = sqlHandler.query("SELECT o.id, c.account " +
                "FROM Orders AS o " +
                "JOIN Customer AS c ON o.customer_id = c.account " +
                "WHERE o.id = ? ", orderID);
        while (resultSet.next()) {
            customerAccount = resultSet.getString("account");
        }
        resultSet = sqlHandler.query("SELECT angryBirdID FROM Customer WHERE account = ?", customerAccount);
        if (resultSet.next()) {
            angryBirdID = resultSet.getInt("angryBirdID");
        }
        if(angryBirdID != -1) {
            if (AngryBird.willAttack(angryBirdID)) {
                sqlHandler.update("UPDATE CustomerAttackByBird SET angryBird_count = angryBird_count + 1 " +
                        "WHERE account = ?", customerAccount);
                sqlHandler.update("UPDATE StoreAttackByBird SET angryBird_at_customer_count = angryBird_at_customer_count + 1 " +
                        "WHERE store_name = ?", storeName);
                int[] storeLocation = currentStore.getLocation();
                int[] customerLocation = getLocation();
                double distance = distanceBetweenLocation(storeLocation, customerLocation);
                int distanceCount = (int) distance;
                sqlHandler.update("UPDATE StoreAttackByBird SET distance_store_customer = angryBird_at_customer_count + ? " +
                        "WHERE store_name = ?", distanceCount, storeName);
                System.out.println("an angry bird near customer location has attacked the drone");
                Drone.deductFuel(droneID,storeName);
                currentStore.addOverload(currOrder.getDrone().getNumberOrders()-1);

                System.out.println("returning to store for repairs");
                currentStore.addRepair(1);

                // will try to transfer the order

                if (currentStore.internalTransfer(totalWeight,orderID,droneID)) {
                    System.out.println("has transferred your order to another drone for re-delivery");
                    fulfillOrderAngryBirds(storeName, orderID);
                    return;
                } else {
                    // no transfer available wait until repair complete
                    System.out.println("trying to repair drone for re-delivery");
                    System.out.println("we are sorry for the long wait");
                    System.out.println("repair complete, out for re-delivery");
                }
            }
        }

        int totalcost = currOrder.getTotalCost();
        int totalweight = currOrder.getTotalWeight();
        // 3 deduct credit, SQL version done by Alice find the code in Customer?
        changeCredit(-totalcost);
        // 2 add revenue, # purchase, SQL version done by Alice find the code in Customer
        addRevenuePurchaseToStore(storeName, totalCost);

        // 3 number of remain deliveries for the drone deduct by one
        // add overload SQL version done by Alice find the code in Drone
        Drone.deductFuel(droneID, storeName);
        // drone change remaining Cap
        currDrone.addRemainingCap(-totalweight);
        currentStore.addOverload(currDrone.getNumberOrders()-1);
        String current_pliot_id = currDrone.getCurrentPilot();
        sqlHandler. update("UPDATE Pilot SET numberSuccessfulDelivery = " +
                "numberSuccessfulDelivery + 1 WHERE account = ?", current_pliot_id);
        // 5 remove order from system, when you remove order, you also need to remove the associated lines
        removeOrder(orderID, storeName);
        removeLines(orderID, storeName);
        System.out.println("Delivery Complete");
    }

    public void addRevenuePurchaseToStore(String storeName, int totalCost) throws SQLException {
        int newCredit = Store.getRevenue(storeName) + totalCost;
        Store.setRevenue(storeName,newCredit);
        Store.increasePurchaseCount(storeName, 1);
    }
    public static double distanceBetweenLocation(int[] location1, int[]location2){
        double distanceSquare = Math.pow(location1[0] - location2[0], 2) + Math.pow(location1[1] - location2[1], 2);
        return Math.sqrt(distanceSquare);
    }
}



