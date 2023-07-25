package users;

import sqlhandler.sqlHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    protected String account;
    protected String firstName;
    protected String lastName;
    protected String phone;
    protected String role;
    protected String password;

    public User(String account, String firstName, String lastName, String phone, String role, String password) {
        this.account = account;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = role;
        this.password = password;
    }

    public User(String account, String firstName, String lastName, String phone, String role) {
        this.account = account;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = role;
    }

    public User(String account) {
        ResultSet resultSet = sqlHandler.query("SELECT * FROM User WHERE account = ? ", account);
        try {
            while (resultSet.next()) {
                this.account  = resultSet.getString("account");
                this.password = resultSet.getString("password");
                this.role = resultSet.getString("role");
                this.firstName = resultSet.getString("firstName");
                this.lastName = resultSet.getString("lastName");
                this.phone = resultSet.getString("phone");;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    public static void displayItems(String storeName) {
        ResultSet resultSet = sqlHandler.query("SELECT i.* FROM Item AS i JOIN Store AS s " +
                "ON i.store_name = s.name WHERE s.name = ? ORDER BY i.name ASC", storeName);
        try {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int weight = resultSet.getInt("weight");
                System.out.println("name:" + name + ",weight:" + weight);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setAccount(String account) {
        this.account = account;
    }
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    public void setLastName(String lastName){
        this.lastName = lastName;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public String getName(){
        return this.firstName + "_" + this.lastName;
    }

    public String getAccount() {
        return this.account;
    }

    public static void display_pilots(){
        ResultSet resultSet = sqlHandler.query("SELECT Pilot.account, firstname, lastname, phone, taxID, licenseID, numberSuccessfulDelivery FROM User inner join Pilot on User.account = Pilot.account ORDER BY Pilot.account");
        try {
            while (resultSet.next()) {
                String acc = resultSet.getString("Pilot.account");
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                String phone = resultSet.getString("phone");
                String taxID = resultSet.getString("taxID");
                String licenseID = resultSet.getString("licenseID");
                String experience = resultSet.getString("numberSuccessfulDelivery");
                System.out.println("account:" + acc + ", name:" + firstname + "_" + lastname + ",phone:" + phone + ",taxID:" + taxID + ",licenseID:" + licenseID + ",experience:" + experience);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void makeUser(String account, String password, String role, String firstname, String lastName, String phone) {
        String insertSql = "INSERT INTO User (account, password, role, " +
                "firstName, lastName, phone)  " + " VALUES (?, ?, ?, ?, ?, ?)";
        sqlHandler.insert(insertSql, account, password, role, firstname, lastName, phone);
    }

}
