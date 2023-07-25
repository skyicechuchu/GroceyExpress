package deliveryservice;

import sqlhandler.sqlHandler;
import users.StoreManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

public class Store {
    public Store(String name) {
        this.name = name;
    }

    public static int COST_PER_REPAIR = 1;
    public static double COST_PER_DISTANCE = 0.1;
    public static int COST_PER_DRONE = 10;
    public static double COST_PER_REFUEL = 20;
    private String name;
    private int revenue;
    private int purchase = 0;
    private int overload = 0;
    private int transfers = 0;
    private int repairs = 0;
    private int[] location;

    // TODO: implement storeCosts for purchasedrone, refueling/repairs, angry bird attacks etc,
    private int storeCosts = 0;

    private int angryBirdID = -1;
    private Map<String,Item> items = new TreeMap<String,Item>();
    private Map<String,Drone> drones = new TreeMap<String,Drone>();

    // TODO: need to implement the database version or review and approve as is
    public Map<String,Order> orders = new TreeMap<String,Order>();
    public StoreManager manager;

    public static void increasePurchaseCount(String storename, int increment) throws SQLException {
        String querySql = "SELECT * FROM Store Where name = ?";
        ResultSet storeQuery = sqlHandler.query(querySql, storename);

        if(storeQuery.next()){
            int purchaseCount = storeQuery.getInt("purchase");
            String updateSql =  "UPDATE Store SET purchase = ? WHERE name = ? ";
            sqlHandler.update(updateSql, purchaseCount + increment, storename);
        }
    }




    // TODO: need to implement the database version
    public void displayOrders(){
//        for (Map.Entry<String,Order> entry : this.getOrders().entrySet())
//            entry.getValue().printInfo();
    }


    public void transferOrder(int orderID, int newDroneID) throws SQLException {
        Order transferredOrder = Order.getOrderByID(name,orderID);
        Drone currDrone = transferredOrder.getDrone();
        Drone newDrone = Drone.getDroneByID(this.name,newDroneID);
        //update current drone
        currDrone.addOrderNumber(-1);
        currDrone.addRemainingCap(-transferredOrder.getTotalWeight());
        //update future drone
        newDrone.addOrderNumber(1);
        newDrone.addRemainingCap(transferredOrder.getTotalWeight());
        //update order table drone info
        transferredOrder.setDrone(newDroneID);
        this.addTransfer(1);
    }
    public void addPurchase(int p){ this.purchase += p; }

    public void addRepair(int r){this.repairs +=r ; this.storeCosts += r*COST_PER_REPAIR; }
    public void addOverload(int o){

    }
    public void addTransfer(int t) throws SQLException {
        ResultSet resultset = sqlHandler.query("SELECT transfers FROM Store where name = ?",this.name);
        if(resultset.next()){
            int transfers = resultset.getInt("transfers");
            int newTransfers = transfers + t;
            sqlHandler.update("UPDATE Store SET transfers = ? where name = ?",newTransfers,this.name);
        }
    }
    public void addOrder(String orderId, Order newOrder){
        this.orders.put(orderId,newOrder);
    }

    // TODO: need to implement the database version
    public void printEfficiency(){
        System.out.println("name:"+this.name+",purchases:"+Integer.toString(this.purchase)+",overloads:"+
                Integer.toString(this.overload)+",transfers:"+Integer.toString(this.transfers));
    }
    public int getRevenue(){
        return this.revenue;
    }
    public void addRevenue(int r){ this.setRevenue(this.revenue+r); }

    private void setRevenue(int i) {
        this.revenue = i;
    }

    public boolean addDrone(String id,Drone d){
        if(this.drones.containsKey(id)){
            return false;
        }
        else{
            this.storeCosts += COST_PER_DRONE;
            this.drones.put(id,d);
            return true;
        }
    }

    // TODO: need to implement the database version
    public Map<String,Item> getItems(){ return this.items; }

    // TODO: need to implement the database version
    public Map<String,Drone> getDrones(){ return this.drones; }

    // TODO: need to implement the database version
    public Map<String,Order> getOrders(){ return this.orders; }
    public String getName(){ return this.name; }


     // TODO: need to implement the database version
     public boolean internalTransfer(int weight, int orderID, int oldDrone) throws SQLException {
        // find drone with capacity and with pilot

        String queryDrone = "SELECT * FROM Drone WHERE remaining_cap >= ? AND store_name = ? AND current_pilot_id != ? AND id !=?";
        ResultSet rs = sqlHandler.query(queryDrone, weight, name, "noPilot", oldDrone);
        if (rs.next()){
            int newID = rs.getInt("id");
//            Drone newDrone = Drone.getDroneByID(name,newID);
            transferOrder(orderID,newID);
            return true;
        } else {
            return false;
        }
     }




    public static int getRevenue(String storename) throws SQLException {
        String querySql = "SELECT * FROM Store Where name = ?";
        ResultSet storeQuery = sqlHandler.query(querySql, storename);
        if(storeQuery.next()){
            int revenue = storeQuery.getInt("revenue");
            return revenue;
        }
        System.out.println("store not found");
        return 0;
    }

    public static void setRevenue(String storename, int newCredit){
        String updateSql =  "UPDATE Store SET revenue = ? WHERE name = ? ";
        sqlHandler.update(updateSql, newCredit, storename);
    }

    public int[] getLocation() throws SQLException {
        int[] res = new int[2];
        ResultSet query1 = sqlHandler.query("SELECT location_x, location_y FROM Store WHERE name = ?", name);
        if (query1.next()) {
            res[0] = query1.getInt("location_x");
            res[1] = query1.getInt("location_y");
        }
        return res;
    }

    public void setLocation(int[] location) {
        this.location = location;
    }

    public int getStoreCosts() {
        return storeCosts;
    }

    public void setStoreCosts(int storeCosts) {
        this.storeCosts = storeCosts;
    }
}
