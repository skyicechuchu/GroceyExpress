package deliveryservice;

import sqlhandler.sqlHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;


// TODO: need to implement the database version or review and approve as is
// TODO: need to review the implementation, currently, high coupling, store have order, customer has order, and orders also have storeID and customerID
public class Order {
    private String storename;
    private int orderId;
    private int droneId;
    private String customerId;

    private int cost;

    // TODO: need to implement the database version or review and approve as is


    public Order(int orderId,String storename,  int droneId, String customerId, int cost) {
        this.storename = storename;
        this.orderId = orderId;
        this.droneId = droneId;
        this.customerId = customerId;
        this.cost = cost;
    }

    public Order (int id, String store_name){
        this.storename = store_name;
        this.orderId = id;

        ResultSet resultSet = sqlHandler.query("SELECT * FROM Orders WHERE store_name = ? and id = ? ", storename, orderId);
        try {
            while (resultSet.next()) {
                this.droneId = resultSet.getInt("drone_id");
                this.customerId = resultSet.getString("customer_id");
                this.cost = resultSet.getInt("cost");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static Order getOrderByID(String storename, int orderId) throws SQLException {
        ResultSet resultSet = sqlHandler.query("SELECT * FROM Orders WHERE store_name = ? and id = ?", storename,orderId);
        if (resultSet.next()) {
            Order order = new Order(
                    resultSet.getInt("id"),
                    resultSet.getString("store_name"),
                    resultSet.getInt("drone_id"),
                    resultSet.getString("customer_id"),
                    resultSet.getInt("cost")
            );
            return order;
        }
        return null;
    }

    public Drone getDrone() throws SQLException {
        return Drone.getDroneByID(storename,droneId);
    }

    public int getDroneId(){
        return droneId;
    }

    public static int getOrderDroneIDSQL(int orderId, String storename) throws SQLException {
        String querySQL = "SELECT * FROM Orders WHERE id = ? and store_name = ?";
        ResultSet rs = sqlHandler.query(querySQL,orderId,storename);
        if (rs.next()){
            return rs.getInt("drone_id");
        }
        return -1;
    }

    public int getOrderDroneID(){
        return droneId;
    }


    public void addLine(String item_name,Line line){

        String insertSql = "INSERT INTO Line (item_name, quantity, weight, price, order_id, store_name) " + " VALUES (?, ?, ?, ?, ?, ?)";
        sqlHandler.insert(insertSql, item_name, line.getQuantity(), line.getWeight(), line.getPrice(), line.getOrder_id(), line.getStore_name());

    }

    public int getTotalCost() throws SQLException{
        String store_name = this.storename;
        int order_id = this.orderId;
        String querySQL = "SELECT" +
                " SUM(price) AS totalCost" +
                " FROM Line" +
                " WHERE store_name =?" +
                " AND order_id = ?";
        ResultSet resultSet = sqlHandler.query(querySQL,store_name,order_id);
        if(resultSet.next()){
            int totalCost = resultSet.getInt("totalCost");
            return totalCost;
        }
        System.out.println("Something is wrong with total cost calculation");
        return 0;
    }
    public void addCost(int c) throws SQLException{
        String store_name = this.storename;
        int order_id = this.orderId;
        int newCost = c + this.getTotalCost();
        sqlHandler.query("UPDATE Orders SET cost = ? WHERE store_name = ? and id = ?",newCost,store_name,order_id) ;
    }

    public int getTotalWeight() throws SQLException {
        String store_name = storename;
        int order_id = orderId;
        String querySQL = "SELECT" +
                " SUM(weight) as totalWeight" +

                " FROM Line" +
                " WHERE store_name =?" +
                " AND order_id = ?";
        ResultSet resultSet = sqlHandler.query(querySQL,store_name,order_id);
        if (resultSet.next()){
            int totalWeight = resultSet.getInt("totalWeight");
            return totalWeight;
        }
        return 0;
    }
    public int getOrderId(){ return this.orderId; }
//    public void setDrone(Drone newDrone){
//        this.drone = newDrone;
//    }

    public String getStoreName(){return  this.storename;}

    public String getCustomerId() { return customerId; }
    public Drone getOrderDrone() throws SQLException {
        return Drone.getDroneByID(storename, droneId);
    }

    public void setDrone(int newDroneID) throws SQLException {
        int orderID = this.orderId;
        sqlHandler.update("UPDATE Orders set drone_id = ? where store_name = ? and id = ?", newDroneID, storename, orderID);
    }

    //TODO: print order info for display order
    void printInfo(){
        System.out.println("orderID:"+this.orderId);
//        for (Map.Entry<String,Line> entry : this.lines.entrySet())
//            entry.getValue().printInfo();
    }

    public static Order getOrder(int orderID) throws SQLException {
        Order order;
        String queryString = "Select * From Order where id = ?";
        ResultSet orderQueried = sqlHandler.query(queryString,orderID);
        if(orderQueried.next()){
            order = new Order(
                    orderQueried.getInt("id"),
                    orderQueried.getString("store_name"),
                    orderQueried.getInt("drone_id"),
                    orderQueried.getString("customer_id"),
                    orderQueried.getInt("cost")
            );
            return order;
        }
        return null;
    }


    public int getDroneID() {
        return droneId;
    }
}
