package deliveryservice;

import sqlhandler.sqlHandler;
import users.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Pilot extends User {
    //private String account;
    //private String firstName;
    //private String lastName;
    //private String phone;
    private String taxID;
    private String licenseID;
    private int numberSuccessfulDelivery;
    private int current_drone_id;
    private static Set<String> registered_license = new HashSet<String>();








    public Pilot(String acc){
        super(acc);
        ResultSet rs = sqlHandler.query("SELECT * FROM Pilot WHERE account = ?", acc);
        try {
            this.licenseID = rs.getString("licenseID");
            this.current_drone_id = rs.getInt("current_drone_id");
            this.numberSuccessfulDelivery = rs.getInt("numberSuccessfulDelivery");
            this.taxID = rs.getString("taxID ");
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public Pilot(String acc, String firstName, String lastName, String phone,
                 String taxID, String licenseID, int numberSuccessfulDelivery){
        super(acc);
        this.account = acc;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.taxID = taxID;
        this.licenseID = licenseID;
        this.numberSuccessfulDelivery = numberSuccessfulDelivery;
    }

    public static boolean addLicense(String s){
        if(registered_license.contains(s)){ return false;}
        registered_license.add(s);
        return true;
    }

//    public void clearDrone(Drone new_drone) throws SQLException {
//        int new_drone_id = new_drone.getId();
//        String new_drone_store = new_drone.getStorename();
//        new_drone.remove_pilot(new_drone_store,new_drone_id);
//        Drone oldDrone = Drone.getDroneByJustID(current_drone_id);
        // TODO: ask lefan the logic of clearDrone
        // TODO: pilot might also need storename or not discuss at 9pm
//        String updateSql = "UPDATE Pilot SET current_drone_id = ? WHERE account = ? ";
//        sqlHandler.update(updateSql, new_drone_id, this.getAccount());
//
//        updateSql = "UPDATE Drone SET current_pilot_id = ? WHERE id = ? ";
//        sqlHandler.update(updateSql, this.getAccount(), new_drone_id);
//    }
    public void removeDrone(String account){
        String updateSql = "UPDATE Pilot SET current_drone_id = ? WHERE account = ? ";
        sqlHandler.update(updateSql, "", account);
    }
    public void addExperience(){ this.numberSuccessfulDelivery = this.numberSuccessfulDelivery + 1; }
    public void printInfo(){
        System.out.println("name:"+this.firstName+"_"+this.lastName+",phone:"+this.phone+",taxID:"+this.taxID+",licenseID:"+
                        this.licenseID+",experience:"+Integer.toString(this.numberSuccessfulDelivery));
    }
    //public String getName(){ return this.firstName+"_"+this.lastName; }


}
