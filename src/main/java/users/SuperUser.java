package users;

import deliveryservice.*;
import sqlhandler.sqlHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class SuperUser extends User {


    public SuperUser(String acc){
        super(acc);
    }

    public void make_store(String store,String revenue, String managerID, String location_x, String location_y)  {
        String insertSql = "INSERT INTO Store (name, revenue, purchase, overload, " +
                "transfers, repairs, location_x, " +
                "location_y, manager, storeCosts, angryBirdID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        sqlHandler.insert(insertSql, store, Integer.parseInt(revenue), 0, 0, 0, 0,
                Integer.parseInt(location_x), Integer.parseInt(location_y), managerID, 0, -1);
        insertSql = "INSERT INTO StoreAttackByBird (store_name) VALUES (?)";
        sqlHandler.insert(insertSql, store);
    }
    public void display_stores() {
        ResultSet resultSet = sqlHandler.query("SELECT s.revenue, u.firstName, u.lastName, s.name " +
                "FROM Store AS s " +
                "JOIN StoreManager AS sm ON s.manager = sm.account " +
                "JOIN User AS u ON sm.account = u.account " +
                "ORDER BY s.name");
        try {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                int revenue = resultSet.getInt("revenue");
                System.out.println("Store name: " + name + ", Manager name: " + firstName + " " + lastName + ", Revenue: " + revenue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void display_efficiency(Map<String, Store> stores){
        for (Map.Entry<String,Store> entry : stores.entrySet())
            entry.getValue().printEfficiency();
    }

    public void display_customers(){
        ResultSet resultSet = sqlHandler.query("SELECT u.account, u.firstName, u.lastName, u.phone, " +
                "c.rating, c.credits FROM Customer AS c INNER JOIN User AS u ON c.account = u.account " +
                " ORDER BY c.account");

        try {
            while (resultSet.next()) {
                String account = resultSet.getString("account");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String phone = resultSet.getString("phone");
                String rating = resultSet.getString("rating");
                String credits = resultSet.getString("credits");

                System.out.println("account:"+ account + ", name:" + firstName + lastName + ", phone:" + phone + ", rating:" + rating + ", credit:" + credits);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void make_pilot(String account, String firstname, String lastName,
                           String phone, String tax, String license, int experience ){

        makeUser(account, "", UserType.pilot.toString(),firstname, lastName,phone );
        String insertSql = "INSERT INTO Pilot (account, taxID, licenseID, numberSuccessfulDelivery, current_drone_id) " + " VALUES (?, ?, ?, ? ,?)";
        sqlHandler.insert(insertSql, account, tax, license, experience, -1);
    }
    public void add_birds(String newBirdCount){
        int count = Integer.parseInt(newBirdCount);
        for(int i = 0; i < count; i++){
//            System.out.println(i);
            Random random = new Random();
            float attackRate = (float) Math.min(0.7 + random.nextFloat(), 1.0);
            String insertSql = "INSERT INTO AngryBird (attackRate) VALUES (?)";
            sqlHandler.insert(insertSql, attackRate);
        }
    }


    public void makeStoreManager(String account, String password,  String firstname, String lastName,

                                 String phone, String storename) {
        makeUser(account, password, UserType.storemanager.toString(),firstname, lastName,phone );
        String insertSql = "INSERT INTO StoreManager (account, storename) " + " VALUES (?, ?)";
        sqlHandler.insert(insertSql, account, storename);
    }

    public void make_customer(String account, String password, String firstName, String lastName,String phone, int rating, int credits, int x, int y ){

        makeUser(account, password, UserType.customer.toString(),firstName, lastName,phone );
        String insertSql = "INSERT INTO Customer (account, rating, credits, available_credits, location_x, location_y, angryBirdID)  " +
                " VALUES (?, ?, ?, ?, ?, ?, ?);";
        sqlHandler.insert(insertSql, account, rating, credits, credits, x, y, -1);
        insertSql = "INSERT INTO CustomerAttackByBird (account) VALUES (?)";
        sqlHandler.insert(insertSql, account);
    }

}
