package deliveryservice;
import users.User;
import deliveryservice.Pilot;
import sqlhandler.sqlHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

// TODO: need to implement the database version or review and approve as is
public class Drone {
    private int id;
    private String storename;
    private int capacity;
    private int remaining_cap;
    private int delivery_before_refuel;
    private static int number_orders = 0;
    private String current_pilot = null;

    public Drone(String storename,int id,int capacity,int n, int number_orders,int cap, String current_pilot ){
        this.storename = storename;
        this.id = id;
        this.capacity  = capacity;
        this.delivery_before_refuel = n;
        this.number_orders = number_orders;
        this.remaining_cap = cap;
        this.current_pilot = current_pilot;
    }

    public static Drone getDroneByID(String storename, int droneId) throws SQLException {
        ResultSet resultSet = sqlHandler.query("SELECT * FROM Drone WHERE store_name = ? and id = ?", storename,droneId);
        if (resultSet.next()) {
            Drone drone = new Drone(
                    resultSet.getString("store_name"),
                    resultSet.getInt("id"),
                    resultSet.getInt("capacity"),
                    resultSet.getInt("delivery_before_refuel"),
                    resultSet.getInt("number_orders"),
                    resultSet.getInt("remaining_cap"),
                    resultSet.getString("current_pilot_id")
            );
            return drone;
        }
        return null;
    }

    public static Drone getDroneByJustID(int droneID)  throws SQLException {
            ResultSet resultSet = sqlHandler.query("SELECT * FROM Drone WHERE id = ?", droneID);
            if (resultSet.next()) {
                Drone drone = new Drone(
                        resultSet.getString("storename"),
                        resultSet.getInt("id"),
                        resultSet.getInt("capacity"),
                        resultSet.getInt("delivery_before_refuel"),
                        resultSet.getInt("number_orders"),
                        resultSet.getInt("remaining_cap"),
                        resultSet.getString("current_pilot_id")
                );
                return drone;
            }
            return null;
    }



    public static void remove_pilot(String store_name, int droneID) throws SQLException {
        ResultSet resultSet = sqlHandler.query("SELECT current_pilot_id FROM Drone WHERE store_name = ? and id = ?", store_name,droneID);
        if (resultSet != null) {
            String pilotId = resultSet.getString("current_pilot_id");
            String updateSql = "UPDATE Pilot SET current_drone_id = ? WHERE account = ? ";
            sqlHandler.update(updateSql, "", pilotId);
        }
        String updateSql = "UPDATE Drone SET current_pilot_id = ? WHERE store_name = ? and id = ? ";
        sqlHandler.update(updateSql, "",store_name, droneID);
    }

    public String getCurrentPilot(){
        return current_pilot;
    }


    public int getRemainingCap(){
        return remaining_cap;
    }

    public int getRemainingCap(int droneID) throws SQLException {
        ResultSet resultSet = sqlHandler.query("SELECT remaining_cap  From Drone WHERE id = ?", droneID);
        if (resultSet != null) {
            int newVal= resultSet.getInt("remaining_cap")  ;
            return newVal;
        }
        return -1;
    }
    public int getId() { return this.id; }

    public String getStorename() {return this.storename;}

    public int getNumberOrdersSQL() throws SQLException {
        ResultSet resultSet = sqlHandler.query("SELECT number_orders From Drone WHERE store_name = ? and id = ?", this.storename, this.id);
        if (resultSet != null) {
            return  resultSet.getInt("number_orders") ;
        }
        return -1;
    }

    public int getNumber_orders(){
        return number_orders;
    }
    public static int getNumberOrdersSQL(String storename, int id) throws SQLException {
        ResultSet resultSet = sqlHandler.query("SELECT number_orders From Drone WHERE store_name = ? and id = ?", storename, id);
        if (resultSet != null) {
            return  resultSet.getInt("number_orders") ;
        }
        return -1;
    }

    public void addRemainingCap(int weight) throws SQLException {
        ResultSet resultSet = sqlHandler.query("SELECT remaining_cap From Drone WHERE store_name = ? and id = ?", this.storename, this.id);
        if (resultSet.next()) {
            int newVal= resultSet.getInt("remaining_cap") + weight ;
            String updateSql = "Update Drone Set remaining_cap = ? WHERE store_name = ? and id = ?";
            sqlHandler.update(updateSql, newVal, storename, id);
        }
    }
    public void addOrderNumber(int n) throws SQLException {
        ResultSet resultSet = sqlHandler.query("SELECT number_orders From Drone WHERE store_name = ? and id = ?", this.storename, this.id);
        if (resultSet.next()) {
            int newVal= resultSet.getInt("number_orders") + n;
            String updateSql = "Update Drone Set number_orders = ? WHERE store_name =? and id = ?";
            sqlHandler.update(updateSql, newVal, storename, id);
        }
    }

    public boolean checkPilot(){
        return !(this.current_pilot == null) ;
    }
    public boolean checkFuel(){
        return (this.delivery_before_refuel > 0);
    }
    public static void deductFuel(int droneID, String storename) throws SQLException {
        ResultSet resultSet = sqlHandler.query("SELECT delivery_before_refuel From Drone WHERE id = ? and store_name = ?", droneID, storename);
        while (resultSet.next()) {
            int newDeliveryCount= resultSet.getInt("delivery_before_refuel") - 1;
            String updateSql = "Update Drone Set delivery_before_refuel = ? WHERE id = ?";
            sqlHandler.update(updateSql, newDeliveryCount, droneID);
        }
    }
    public void printInfo(){

        //TODO: new info printing with sql
        String output = "droneID:"+this.id+",total_cap:"+Integer.toString(this.capacity)+",num_orders:"+Integer.toString(this.number_orders)+",remaining_cap:" +
                this.remaining_cap+",trips_left:"+Integer.toString(delivery_before_refuel);
        if(this.current_pilot != null){
            output = output + ",flown_by:" + this.current_pilot;
        }
        System.out.println(output);
    }

    public static int getNumberOrders() {
        return number_orders;
    }
}
