package deliveryservice;

import com.mysql.cj.protocol.Resultset;
import users.User;
import sqlhandler.sqlHandler;
import users.Customer;
import users.StoreManager;
import users.SuperUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.io.*;

public class DeliveryService {

    public static int REHASH_TIME = 5;

    public static String[] adminCommands =
            new String[] {"show_commands", "eg: show_commands",
                    "make_store","eg: make_store,store_name,revenue,managerID,locationx,locationy,password,firstname,lastName,phone",
                    "display_stores","eg: display_stores",
                    "display_items", "eg: display_items,store_name",
                    "make_pilot","eg: make_pilot,account, firsname, lastname,phone, taxID,license, experience",
                    "display_pilots","eg: display_pilots",
                    "make_customer", "eg: make_customer,account,password,firstname,lastname,phone,rating,credits,locationx,locationy",
                    "display_customers","eg: display_customers" ,
                    "add_birds", "eg: add_birds,count",
                    "display_birds", "eg: display_birds",
                    "rehash_birds","eg: rehash_birds",
                    "display_efficiency","eg: display_efficiency",
                    "display_summary", "eg: display_summary",
                    "logout", "eg: logout"};
    public static String[] customerCommands =
            new String[] {"show_commands", "eg: show_commands",
                    "start_order", "eg: start_order,storeName,customerAccount",
                    "display_customer_order", "eg: display_customer_order,customerAccount",
                    "display_items", "eg: display_items,store_name",
                    "request_item", "eg: request_item,storeName, orderID, item, quantity, unit_price",
                    "purchase_order", "eg: purchase_order,storeName,OrderID",
                    "cancel_order", "eg: cancel_order,storename,orderId",
                    "display_birds", "eg: display_birds",
                    "rehash_birds","eg: rehash_birds",
                    "display_summary", "eg: display_summary",
                    "logout", "eg: logout"};

    public static String[] managerCommands =
            new String[] {"show_commands", "eg: show_commands",
                    "sell_item", "eg: sell_item,storeName,item,quantity",
                    "display_items", "eg: display_items,storeName",
                    "make_drone","eg: make_drone,storeName,droneID,capacity,fuel,",
                    "display_drones","eg: display_drones,storeName",
                    "fly_drone", "eg: fly_drone,storeName, droneID, pilotID",
                    "transfer_order","eg: transfer_order,storeName,orderID, newdrone ID",
                    "display_orders", "eg: display_orders,storeName",
                    "display_birds", "eg: display_birds",
                    "rehash_birds","eg: rehash_birds",
                    "display_summary", "eg: display_summary",
                    "logout", "eg: logout"};

    private int timeElapsed = 0;

    private String currType = "";
    private String currAccount = "";
    private SuperUser adminCurr = null;
    private Customer customerCurr = null;
    private StoreManager storeManagerCurr = null;

    DeliveryService(String loginType, String loginAccount) {
        this.currType = loginType;
        this.currAccount = loginAccount;
        this.adminCurr = null;
        this.customerCurr = null;
        this.storeManagerCurr = null;
        try {
            setCurrUser(loginType, loginAccount);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("System Error");
        }
    }

    private void setCurrUser(String loginType, String loginAccount) throws SQLException {
        if (loginType.equals(UserType.admin.toString())) {
            this.adminCurr = new SuperUser(loginAccount);
        }
        if (loginType.equals(UserType.storemanager.toString())) {
            ResultSet queryManager = sqlHandler.query("SELECT * FROM StoreManager WHERE account = ?", loginAccount);
            if (!queryManager.next()) {
                System.out.println("ERROR:manager_id_not_exists");
                return;
            } else {
                String storeName = queryManager.getString("storename");
                this.storeManagerCurr = new StoreManager(loginAccount, storeName);
            }
        }

        if(loginType.equals(UserType.customer.toString())){
            ResultSet queryCustomer = sqlHandler.query("SELECT * FROM Customer WHERE account = ?", loginAccount);
            if (!queryCustomer.next()) {
                System.out.println("ERROR:customer_id_not_exists");
                return;
            } else {
                this.customerCurr = new Customer(
                        queryCustomer.getString("account"),
                        queryCustomer.getInt("rating"),
                        queryCustomer.getInt("credits"),
                        queryCustomer.getInt("available_credits"),
                        queryCustomer.getInt("location_x"),
                        queryCustomer.getInt("location_y"),
                        queryCustomer.getInt("angryBirdID"));
            }
        }
    }

    public void commandLoop() throws SQLException {
        Scanner commandLineInput = new Scanner(System.in);
        String wholeInputLine;
        String[] tokens;
        Random rand1 = new Random();
        int rehashRandom = rand1.nextInt(REHASH_TIME) + REHASH_TIME;
        final String DELIMITER = ",";
        System.out.println("You are logged on to the Grocery Express Delivery Service as " + currType);

        while (true) {
            try {
                // Determine the next command and echo it to the monitor for testing purposes
                wholeInputLine = commandLineInput.nextLine();
                tokens = wholeInputLine.split(DELIMITER);
                System.out.println("> " + wholeInputLine);
                timeElapsed ++;

                if(timeElapsed == rehashRandom){

                    rehashBirds();
                    System.out.println("The angry birds have traveled around. Type display_birds to see outcome.");
                    timeElapsed = 0;
                    rehashRandom = rand1.nextInt(REHASH_TIME) + REHASH_TIME;
                }

                Boolean illegal = false;
                for (String t: tokens) {
                    if(!usernamePasswordValidation(t)){
                        illegal = true;
                        break;
                    }
                }
                if(illegal){
                    System.out.println("This command contains special characters no allowed for execution");
                    continue;
                }

                if (tokens[0].equals("logout")) {
                    System.out.println("Logging out");
                    break;
                    }

                else if(tokens[0].equals("show_commands")){
                    showCommands();

                }if (tokens[0].equals("make_store")) {
                    // make_store,store_name,revenue,managerID,locationx,locationy,password,firstname,lastName,phone
                    makeStore(tokens);


                } else if (tokens[0].equals("display_stores")) {
                    // display_stores
                    displayStores(tokens);
                }

                else if (tokens[0].equals("sell_item")) {
                    // sell_item,kroger,pot_roast,5
                    sellItems(tokens);

                } else if (tokens[0].equals("display_items")) {
                    displayItems(tokens);

                } else if (tokens[0].equals("make_pilot")) {

                    // make_pilot,account, firsname, lastname,phone, taxID,license, experience
                    //System.out.print("account: " + tokens[1] + ", first_name: " + tokens[2] + ", last_name: " + tokens[3]);
                    //System.out.println(", phone: " + tokens[4] + ", tax: " + tokens[5] + ", license: " + tokens[6] + ", experience: " + tokens[7]);
                    makePilot(tokens);

                } else if (tokens[0].equals("display_pilots")) {
                    //System.out.println("no parameters needed")

                    displayPilots(tokens);


                } else if (tokens[0].equals("make_drone")) {
                    //store, drone, capacity,fuel,
                    //System.out.println("store: " + tokens[1] + ", drone: " + tokens[2] + ", capacity: " + tokens[3] + ", fuel: " + tokens[4]);
                    makeDrone(tokens);


                } else if (tokens[0].equals("display_drones")) {
                    displayDrone(tokens);

                } else if (tokens[0].equals("fly_drone")) {

                    //System.out.println("store: " + tokens[1] + ", drone: " + tokens[2] + ", pilot: " + tokens[3]);
                    flyDrone(tokens);

                } else if (tokens[0].equals("make_customer")) {
                  //make_customer,account,password,firstname,lastname,phone,rating,credits,x,y
                    makeCustomer(tokens);

                } else if (tokens[0].equals("display_customers")) {

                   displayCustomers(tokens);

                } else if(tokens[0].equals("display_customer_order")){
                    //display_customer_order,customerAccount

                    displayCustomerOrder(tokens);
                }
                // start_order,storeName,CustomerID
                else if (tokens[0].equals("start_order")) {
                    startOrder(tokens);
                }
                // display_orders,storeName
                else if (tokens[0].equals("display_orders")) {
                    displayOrders(tokens);

                } else if (tokens[0].equals("request_item")) {
                    //System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ", item: " + tokens[3] + ", quantity: " + tokens[4] + ", unit_price: " + tokens[5]);

                    requestItem(tokens);


                } else if (tokens[0].equals("purchase_order")) {
                    // purchase_order,storeName,OrderID
                       purchaseOrder(tokens);

                } else if (tokens[0].equals("cancel_order")) {

                    //cancel_order,storename,orderId
                    cancelOrder(tokens);

                } else if (tokens[0].equals("transfer_order")) {

                    //transfer_order,storename,order,newdrone
                    //System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ", new_drone: " + tokens[3]);
                    transferOrder(tokens);

                } else if (tokens[0].equals("display_efficiency")) {
                    //display_efficiency,
                    displayEfficiency();

                } else if (tokens[0].equals("display_summary")) {
                    displaySummary();
                } else if (tokens[0].equals("add_birds")) {
                    // add_birds,count
                        addBirds(tokens);

                } else if (tokens[0].equals("display_birds")) {
                        // everybody can see the birds for fun~
                        displayBird();


                }else if(tokens[0].equals("rehash_birds")){
                    rehashBirds();

                }

                else {
                        if (tokens[0].length() >= 2) {
                            if (tokens[0].substring(0, 2).equals("//")) {
                                continue;
                            }

                        }
                        System.out.println("command " + tokens[0] + " NOT acknowledged");
                    }
                } catch(Exception e){
//                    e.printStackTrace();
                    System.out.println("System error. Please contact tech support at OMSCS 2023Spring SAD Team 128");
                }
            }

            System.out.println("back to login page");
            System.out.println("Please enter your account and password separated by comma for login.To exit, please enter stop");
//        commandLineInput.close();
        }

    private void showCommands() {
        System.out.println("Commands for "+currType+ " include:");
        if(currType.equals(UserType.admin.toString())){
            for (String s:
                 adminCommands) {
                System.out.println(s);
            }
        } else if (currType.equals(UserType.storemanager.toString())){
            for (String s:
                    managerCommands) {
                System.out.println(s);
            }
        } else if (currType.equals(UserType.customer.toString())){
            for (String s:
                    customerCommands) {
                System.out.println(s);
            }
        } else {
            System.out.println("Probematic login. Please contact tech support.");
        }
        System.out.println("OK:display completed.");
    }

    private void displayCustomerOrder(String[] tokens) throws SQLException {
        if(!currType.equals(UserType.customer.toString())){
            System.out.println("need customer access");
            return;
        }

        if(!customerCurr.getAccount().equals(tokens[1])){
            System.out.println("cannot access orders that are not yours.");
        }

        customerCurr.displayCustomerOrder();
    }

    private void addBirds(String[] tokens) throws SQLException {
        if (!currType.equals(UserType.admin.toString())) {

            System.out.println("need admin access");
            return;
        }
        adminCurr.add_birds(tokens[1]);
        rehashBirds();
        System.out.println("OK:added_birds_completed");

    }
    private void transferOrder(String[] tokens) throws SQLException {

        if (!currType.equals(UserType.storemanager.toString())) {
            System.out.println("need storemanager access");
            return;
        } else if (!storeManagerCurr.getStoreName().equals(tokens[1])) {
            System.out.println("does not have access to change this store");
            return;
        }
        ResultSet resultset = sqlHandler.query("SELECT * FROM Store where name = ?", tokens[1]);
        if (!resultset.next()) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return;
        }

        ResultSet resultset1 = sqlHandler.query("SELECT * FROM Orders where store_name = ? and id = ?", tokens[1], tokens[2]);
        if (!resultset1.next()) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return;
        }

        ResultSet resultset2 = sqlHandler.query("SELECT * FROM Drones where store_name = ? and id = ?", tokens[1], tokens[3]);
        if (!resultset2.next()) {
            System.out.println("ERROR:drone_identifier_does_not_exist");
            return;
        }

        Order currentOrder = Order.getOrderByID(storeManagerCurr.getStoreName(), Integer.parseInt(tokens[2]));
        Drone currentDrone = currentOrder.getDrone();
        Drone newDrone = Drone.getDroneByID(tokens[1], Integer.getInteger(tokens[3]));

        if (currentOrder.getTotalWeight() > newDrone.getRemainingCap()) {
            System.out.println("ERROR:new_drone_does_not_have_enough_capacity");
            return;
        }

        if (currentOrder.getDrone().getId() == (Integer.parseInt(tokens[3]))) {
            System.out.println("OK:new_drone_is_current_drone_no_change");
            return;
        }

        storeManagerCurr.getStore().transferOrder(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));

        System.out.println("OK:change_completed");
        //System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ", new_drone: " + tokens[3]);

    }

    private void cancelOrder(String[] tokens) throws SQLException {
        if (!currType.equals(UserType.customer.toString())) {
            System.out.println("need customer access");
            return;
        }
        // TODO: customer shall not cancel orders that are not theirs
        ResultSet resultset = sqlHandler.query("SELECT * FROM Store where name = ?", tokens[1]);
        if (!resultset.next()) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return;
        }

        ResultSet resultset1 = sqlHandler.query("SELECT * FROM Orders where store_name = ? and id = ?", tokens[1], tokens[2]);

        if (!resultset1.next()) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return;
        }

        Order currentOrder = Order.getOrderByID(tokens[1], Integer.parseInt(tokens[2]));
        if(!customerCurr.getAccount().equals(currentOrder.getCustomerId())){
            System.out.println("ERROR:customer cannot access order that are not theirs");
            return;
        }



        Drone currentDrone = currentOrder.getDrone();
        int capacityAdd = currentOrder.getTotalWeight();
        currentDrone.addRemainingCap(capacityAdd);
        //remove one order from current drone
        currentDrone.addOrderNumber(-1);
        //cancelOrder will update available credits, remove lines, and remove order
        customerCurr.cancelOrder( Integer.parseInt(tokens[2]), tokens[1]);
        System.out.println("OK:change_completed");
        //System.out.println("store: " + tokens[1] + ", order: " + tokens[2]);
    }

    private void purchaseOrder(String[] tokens) throws SQLException {
        if (!currType.equals(UserType.customer.toString())) {
            System.out.println("need customer access");
            return;
        }
        ResultSet resultSet = sqlHandler.query("SELECT * FROM Store WHERE name = ?", tokens[1]);
        if (!resultSet.next()) {
            System.out.println("ERROR:store_identifier_does_not_exists");
            return;
        }
        ResultSet resultDrone = sqlHandler.query("SELECT * FROM Orders WHERE id = ?", tokens[2]);
        if (!resultDrone.next()) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return;
        }
        String customerId = customerCurr.getAccount();
        int orderID = Integer.parseInt(tokens[2]);
        ResultSet resultSetOrder = sqlHandler.query("SELECT * FROM Orders WHERE customer_id = ? AND id = ?", customerId, orderID);
        if (!resultSetOrder.next()) {
            System.out.println("ERROR:customer_does_not_have_this_order");
            return;
        } else {
            int droneID = resultSetOrder.getInt("drone_id");
            ResultSet resultSetDrone = sqlHandler.query("SELECT id, current_pilot_id FROM Drone WHERE id = ?", droneID);
            if (resultSetDrone.next()) {
                if (resultSetDrone.getString("current_pilot_id").equals("noPilot")) {
                    System.out.println("ERROR:drone_needs_pilot");
                    return;
                }
            }
            ResultSet resultSetFule = sqlHandler.query("SELECT * FROM Drone WHERE store_name = ? and id = ?", tokens[1], droneID);

            if (resultSetFule.next()) {
                int deliveryBeforeRefuel = resultSetFule.getInt("delivery_before_refuel");
                if (deliveryBeforeRefuel <= 0) {
                    System.out.println("ERROR:drone_needs_fuel");
                }
            }
            customerCurr.fulfillOrderAngryBirds(tokens[1], orderID);
        }


        System.out.println("OK:change_completed");
    }

    private void requestItem(String[] tokens) throws SQLException {
        //System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ", item: " + tokens[3] + ", quantity: " + tokens[4] + ", unit_price: " + tokens[5]);
        if (!currType.equals(UserType.customer.toString())) {
            System.out.println("need customer access");
            return;
        }

        ResultSet rs = sqlHandler.query("SELECT * FROM Store WHERE name = ?", tokens[1]);
        if(!rs.next()){
            System.out.println("ERROR:store_identifier_does_not_exist");
            return;
        }

//        rs = sqlHandler.query("SELECT * FROM Order WHERE store_name = ? AND id = ?", tokens[1], Integer.parseInt(tokens[2]));
//        if(!rs.next()){
//            System.out.println("ERROR:order_identifier_does_not_exist");
//            return;
//        }

        rs = sqlHandler.query("SELECT * FROM Orders WHERE store_name = ? AND id = ? AND customer_id =?", tokens[1], Integer.parseInt(tokens[2]), customerCurr.getAccount());
        if(!rs.next()){
            System.out.println("ERROR:order_identifier_does_not_exist or order does not below to this customer");
            return;
        }


        int itemWeight;
        rs = sqlHandler.query("SELECT * FROM Item WHERE store_name = ? AND name = ?"
                , tokens[1], tokens[3]);
        if(!rs.next()){
            System.out.println("ERROR:item_identifier_does_not_exist");
            return;
        } else{
            itemWeight = rs.getInt("weight");
        }



        rs = sqlHandler.query("SELECT * FROM Line WHERE store_name = ? AND order_id = ? AND item_name = ?"
                , tokens[1], Integer.parseInt(tokens[2]), tokens[3]);
        if(rs.next()){
            System.out.println("ERROR:item_already_ordered");
            return;
        }




        Line newLine = new Line(tokens[3], Integer.parseInt(tokens[4]), itemWeight, Integer.parseInt(tokens[5]), Integer.parseInt(tokens[2]), tokens[1]);
        Order currentOrder = new Order(Integer.parseInt(tokens[2]), tokens[1]);
        Drone orderDrone = currentOrder.getDrone();


        if (newLine.getTotalCost() > customerCurr.getAvailable_credits()) {
            System.out.println("ERROR:customer_cant_afford_new_item");
            return;
        }

        if (newLine.getTotalWeight() > orderDrone.getRemainingCap()) {
            System.out.println("ERROR:drone_cant_carry_new_item");
            return;
        }

        // drone change remaining Cap
        orderDrone.addRemainingCap(-1*newLine.getTotalWeight());
        // customer change available credit, order add line, order total weight/ total cost are simply derived from lines
        customerCurr.requestItem(currentOrder, tokens[3], newLine);
        System.out.println("OK:change_completed");

    }

    private void displayOrders(String[] tokens) throws SQLException {
        if (!currType.equals(UserType.storemanager.toString())) {

            System.out.println("need storemanager access");
            return;
        } else if (!storeManagerCurr.getStoreName().equals(tokens[1])) {
            System.out.println("does not have access to change this store");
            return;
        }

        ResultSet resultSet = sqlHandler.query("SELECT * FROM Store WHERE name = ?", tokens[1]);
        if (!resultSet.next()) {
            System.out.println("ERROR:store_identifier_does_not_exists");
            return;
        }
        storeManagerCurr.display_orders(tokens[1]);
        System.out.println("OK:display_completed");
    }

    private void startOrder(String[] tokens) throws SQLException {
        if (!currType.equals(UserType.customer.toString())) {
            System.out.println("need customer access");
            return;
        }
        ResultSet resultSet = sqlHandler.query("SELECT * FROM Store WHERE name = ?", tokens[1]);
        if (!resultSet.next()) {
            System.out.println("ERROR:store_identifier_does_not_exists");
            return;
        }
        ResultSet resultDrone = sqlHandler.query("SELECT * FROM Customer WHERE account = ?", tokens[2]);
        if (!resultDrone.next()) {
            System.out.println("ERROR:customer_identifier_does_not_exist");
            return;
        }
        customerCurr.startOrder(tokens[1], tokens[2]);

        System.out.println("OK:change_completed");
    }

    private void displayCustomers(String[] tokens) {
        if (!currType.equals(UserType.admin.toString())) {
            System.out.println("need admin access");
            return;
        }
        adminCurr.display_customers();
        //this.display_customers();
        System.out.println("OK:display_completed");
        //System.out.println("no parameters needed");
    }

    private void flyDrone(String[] tokens) throws SQLException {
        if (!currType.equals(UserType.storemanager.toString())) {
            System.out.println("need storemanager access");
            return;

        } else if (!storeManagerCurr.getStoreName().equals(tokens[1])) {
            System.out.println("does not have access to change this store");
            return;
        }
        ResultSet resultset = sqlHandler.query("SELECT * FROM Store where name = ?", tokens[1]);
        if (!resultset.next()) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return;
        }
        ResultSet resultset1 = sqlHandler.query("SELECT * FROM Drone where store_name = ? and id = ?", tokens[1], tokens[2]);
        if (!resultset1.next()) {
            System.out.println("ERROR:drone_identifier_does_not_exist");
            return;
        }
        ResultSet resultset2 = sqlHandler.query("SELECT * FROM Pilot where account = ?", tokens[3]);
        if (!resultset2.next()) {
            System.out.println("ERROR:pilot_identifier_does_not_exist");
            return;
        }
        String storename = tokens[1];
        Drone new_drone = Drone.getDroneByID(storename, Integer.parseInt(tokens[2]));

        //       1. find oldPilot from droneID,  oldPilot, set currDrone = -1
        String queryOldPilot = "SELECT * FROM Drone WHERE store_name = ? AND id = ?";
        ResultSet rs = sqlHandler.query(queryOldPilot,storename, Integer.parseInt(tokens[2]));
        if (rs.next()){
            String oldPilotID = rs.getString("current_pilot_id");
            if (oldPilotID != "noPilot"){
                String updatePilot = "Update Pilot Set current_drone_id = -1 WHERE account = ?";
                sqlHandler.update(updatePilot,oldPilotID);
            }
            //
        }

        //         2. find oldDrone from pilotID, oldDrone , set pilot = ""
        String queryOldDrone = "SELECT * FROM Pilot WHERE account = ?" ;
        rs = sqlHandler.query(queryOldDrone,tokens[3]);
        if (rs.next()){
            int oldDroneID = rs.getInt("current_drone_id");
            if (oldDroneID != -1){
                String updateDrone = "Update Drone Set current_pilot_id =? WHERE id = ? and store_name =?";
                sqlHandler.update(updateDrone, "noPilot", oldDroneID, storename);
            }
            //
        }


//        3. droneID find drone, set pilot as pilotID
//        4. droneID find drone, set pilot as pilotID
        storeManagerCurr.fly_drone(tokens[1], Integer.parseInt(tokens[2]), tokens[3]);

//                    this.pilots.get(tokens[3]).assignDrone(this.stores.get(tokens[1]).getDrones().get(tokens[2]));
        System.out.println("OK:change_completed");

        //System.out.println("store: " + tokens[1] + ", drone: " + tokens[2] + ", pilot: " + tokens[3]);
    }

    private void displayDrone(String[] tokens) throws SQLException {
        if (!currType.equals(UserType.storemanager.toString())) {
            System.out.println("need storemanager access");
            return;
        } else if (!storeManagerCurr.getStoreName().equals(tokens[1])) {
            System.out.println("does not have access to change this store");
            return;
        }
        ResultSet resultSet = sqlHandler.query("SELECT * FROM Store where name = ?", tokens[1]);
        if (!resultSet.next()) {
            System.out.println("ERROR:store_identifier_does_not_exist");
        } else {
            // manager fix?
//                        StoreManager mgr = this.stores.get(tokens[1]).manager;
            storeManagerCurr.display_drones();
            System.out.println("OK:display_completed");
        }
    }

    private void makeDrone(String[] tokens) throws SQLException {
        if (!currType.equals(UserType.storemanager.toString())) {
            System.out.println("need storemanager access");
            return;
        } else if (!storeManagerCurr.getStoreName().equals(tokens[1])) {
            System.out.println("does not have access to change this store");
            return;
        }

        ResultSet resultSet = sqlHandler.query("SELECT * FROM Store where name = ?", tokens[1]);
        if (!resultSet.next()) {
            System.out.println("ERROR:store_identifier_does_not_exist");
        } else {
            ResultSet resultSet1 = sqlHandler.query("SELECT * FROM Drone where store_name = ? and id = ?", tokens[1], tokens[2]);
            if (!resultSet1.next()) {
                storeManagerCurr.addDrone(tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), 0, "noPilot");
                System.out.println("OK:change_completed");
            } else {
                System.out.println("ERROR:drone_identifier_already_exists");
            }
        }
    }

    private void displayPilots(String[] tokens) {
        User.display_pilots();
        //this.display_pilots();
        System.out.println("OK:display_completed");
    }

    private void makePilot(String[] tokens) throws SQLException {
        if (!currType.equals(UserType.admin.toString())) {
            System.out.println("need adminCurr access");
        } else {

            ResultSet resultSet = sqlHandler.query("SELECT * FROM Pilot WHERE account = ?", tokens[1]);
            if (resultSet.next()) {
                System.out.println("ERROR:pilot_identifier_already_exists");
                return;
            }

            resultSet = sqlHandler.query("SELECT * FROM Pilot WHERE licenseID = ?", tokens[6]);
            if (resultSet.next()) {
                System.out.println("ERROR:pilot_license_already_exists");
                return;
            }

            adminCurr.make_pilot(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], Integer.parseInt(tokens[7]));
            System.out.println("OK:change_completed");

        }

    }

    private void displayItems(String[] tokens) throws SQLException {
        ResultSet resultSet = sqlHandler.query("SELECT * FROM Store WHERE name = ?", tokens[1]);
        if (!resultSet.next()) {
            System.out.println("ERROR:store_identifier_does_not_exists");
        } else {
            resultSet = sqlHandler.query("SELECT * FROM Item WHERE store_name = ? ORDER BY name", tokens[1]);
            while (resultSet.next()) {
                String item_name = resultSet.getString("name");
                int weight = resultSet.getInt("weight");
                System.out.println(item_name + "," +weight);
            }

//            User.displayItems(tokens[1]);
            System.out.println("OK:display_completed");
        }



    }

    private void sellItems(String[] tokens) throws SQLException {

        if (!currType.equals(UserType.storemanager.toString())) {

            System.out.println("need storemanager access");
            return;
        } else if (!storeManagerCurr.getStoreName().equals(tokens[1])) {
            System.out.println("does not have access to change this store");
            return;
        }
        ResultSet resultSet = sqlHandler.query("SELECT * FROM Store WHERE name = ?", tokens[1]);

        if (!resultSet.next()) {
            System.out.println("Error:store_identifier_does_not_exists");

            return;
        } else {
            ResultSet resultItem = sqlHandler.query("SELECT * FROM Item WHERE name = ?", tokens[2]);
            if (!resultItem.next()) {
                storeManagerCurr.addItem(tokens[2], tokens[3], storeManagerCurr.getStoreName());
                System.out.println("OK:change_completed");
            } else {
                System.out.println("ERROR:item_identifier_already_exists");
                return;
            }
        }
    }

    private void displayStores(String[] tokens) {
        if (!currType.equals(UserType.admin.toString())) {
            System.out.println("need adminCurr access");
            return;
        }
        adminCurr.display_stores();
        System.out.println("OK:display_completed");
    }

    private void makeStore(String[] tokens) throws SQLException {

        if (!currType.equals(UserType.admin.toString())) {

            System.out.println("need adminCurr access");
            return;
        }


        ResultSet resultSet = sqlHandler.query("SELECT * FROM Store WHERE name = ?", tokens[1]);
        if (resultSet.next()) {
            System.out.println("ERROR:store_identifier_already_exists");
            return;
        }
        resultSet = sqlHandler.query("SELECT * FROM StoreManager WHERE account = ?", tokens[3]);
        if (resultSet.next()) {
            System.out.println("ERROR:manager_account_already_exists");
            return;
        } else {

            adminCurr.makeStoreManager(
                    tokens[3],
                    tokens[6],
                    tokens[7],
                    tokens[8],
                    tokens[9],
                    tokens[1]);
            adminCurr.make_store(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]);

            System.out.println("OK:change_completed");
        }
    }

    private void makeCustomer(String[] tokens) throws SQLException {
        //make_customer,account,password,firstname,lastname,phone,rating,credits,x,y
        if (!currType.equals(UserType.admin.toString())) {
            System.out.println("need admin access");
            return;
        }

        ResultSet resultSet = sqlHandler.query("SELECT * FROM Customer WHERE account = ?", tokens[1]);
        if (resultSet.next()) {
            System.out.println("ERROR:customer_identifier_already_exists");
            return;
        }
//                    Customer newCustomer = new Customer(tokens[1], tokens[2], tokens[3], tokens[4],
//                            Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6]), x, y);
//                    this.customers.put(tokens[1], newCustomer);

        adminCurr.make_customer(tokens[1], tokens[2], tokens[3], tokens[4],tokens[5],
                Integer.parseInt(tokens[6]), Integer.parseInt(tokens[7]),
                Integer.parseInt(tokens[8]), Integer.parseInt(tokens[9]));
        System.out.println("OK:change_completed");
        return;
//                    }
        //System.out.print("account: " + tokens[1] + ", first_name: " + tokens[2] + ", last_name: " + tokens[3]);
        //System.out.println(", phone: " + tokens[4] + ", rating: " + tokens[5] + ", credit: " + tokens[6]);

    }





    private void rehashBirds() throws SQLException {
        //get all birdIDs, we probably don't need to store -1 in angry birds as I now think about it
        String query = "SELECT MAX(angryBirdID) AS count FROM AngryBird";
        ResultSet resultSet = sqlHandler.query(query);
        int totalAngryBirds = 0;
        while (resultSet.next()) {
            totalAngryBirds = resultSet.getInt("count");

        }
        List<Integer> angryBirds = new ArrayList<>();


        for(int i = 1; i < totalAngryBirds+1; i++){
            angryBirds.add(i);
        }

        Random rand = new Random();

        // Shuffle the list of Angry Birds randomly
        Collections.shuffle(angryBirds);

        // get all Customers
        query = "SELECT account FROM Customer";
        resultSet = sqlHandler.query(query);
        List<String> customers = new ArrayList<>();

        while (resultSet.next()) {
            customers.add(resultSet.getString("account"));

        }

        // get all Stores
        query = "SELECT name FROM Store";
        resultSet = sqlHandler.query(query);
        List<String> stores = new ArrayList<>();

        while (resultSet.next()) {
            stores.add(resultSet.getString("name"));
        }

        // randomly decide birdcount, clamp it by customersize, storesize
        int customerBirdCount = Math.min(customers.size(), rand.nextInt(totalAngryBirds));
        int storeBirdCount =Math.min(stores.size(), totalAngryBirds - customerBirdCount);


        // customerBirdCount will definitely be less than total customer
        for (int i = 0; i < customerBirdCount; i++) {
            String updateQuery = "UPDATE Customer SET angryBirdID = ? WHERE account = ?";
            sqlHandler.update(updateQuery, angryBirds.get(i), customers.get(i));
        }

        // need to set all other customers' birdID to be -1
        for(int i = customerBirdCount; i < customers.size(); i++){
            String updateQuery = "UPDATE Customer SET angryBirdID = ? WHERE account = ?";
            sqlHandler.update(updateQuery, -1, customers.get(i));
        }


        // set angry birds for store
        for (int i = 0; i < storeBirdCount; i++) {
            int j = i + customerBirdCount;
            String updateQuery = "UPDATE Store SET angryBirdID = ? WHERE name = ?";
            sqlHandler.update(updateQuery, angryBirds.get(j), stores.get(i));
        }

        // need to set all other store's birdID to be -1
        for(int i = storeBirdCount; i < stores.size(); i++){
            String updateQuery = "UPDATE Store SET angryBirdID = ? WHERE name = ?";
            sqlHandler.update(updateQuery, -1, stores.get(i));

        }
        System.out.println("OK:change_completed");

    }


    public void displayBird() throws SQLException {

        ResultSet resultSet = sqlHandler.query("SELECT * FROM AngryBird");
        while (resultSet.next()) {
            int birdID = resultSet.getInt("AngryBird.angryBirdID");
            float attackRate = resultSet.getFloat("AngryBird.attackRate");

            System.out.println("bird: " + birdID + ", attackRate: " + attackRate + " spotted");
        }

         resultSet = sqlHandler.query("SELECT * FROM AngryBird INNER JOIN Customer " +
                "ON AngryBird.angryBirdID  = Customer.angryBirdID");
        while (resultSet.next()) {
            int birdID = resultSet.getInt("AngryBird.angryBirdID");
            float attackRate = resultSet.getFloat("AngryBird.attackRate");
            String customerID = resultSet.getString("Customer.account");
            System.out.println("bird: " + birdID + ", attackRate: " + attackRate + ", at Customer: "+ customerID);
        }

        resultSet = sqlHandler.query("SELECT * FROM AngryBird INNER JOIN Store " +
                "ON AngryBird.angryBirdID  = Store.angryBirdID");
        while (resultSet.next()) {
            int birdID = resultSet.getInt("AngryBird.angryBirdID");
            float attackRate = resultSet.getFloat("AngryBird.attackRate");
            String storeID = resultSet.getString("Store.name");
            System.out.println("bird: " + birdID + ", attackRate: " + attackRate + ", at Store: "+ storeID);
        }

        System.out.println("OK:display_completed");
    }

    // repair_drone_cost, refuel_cost, make_drone cost
    public void displayStoreCost() throws SQLException {
        // Total Cost, Repair Cost, Refule Cost, Buy Drone Cost
        Map<String, Integer[]> storeData = new HashMap<>();
        // buy drone cost
        String query = "SELECT store_name, COUNT(*) as drone_count " +
                "FROM Drone " +
                "GROUP BY store_name";
        ResultSet resultSet = sqlHandler.query(query);
        while (resultSet.next()) {
            String storeName = resultSet.getString("store_name");
            int droneCount = resultSet.getInt("drone_count");
            Integer[] data = storeData.getOrDefault(storeName, new Integer[]{0, 0, 0, 0});
            data[0] = droneCount;
            storeData.put(storeName, data);
        }

        // repair_drone_cost
        query = "SELECT angryBird_at_customer_count, angryBird_at_store_count, store_name FROM StoreAttackByBird";
        resultSet = sqlHandler.query(query);
        while (resultSet.next()) {
            int totalAttack = resultSet.getInt("angryBird_at_customer_count") + resultSet.getInt("angryBird_at_store_count");
            String storeName = resultSet.getString("store_name");
            Integer[] data = storeData.getOrDefault(storeName, new Integer[]{0, 0, 0, 0});
            data[1] = totalAttack;
            storeData.put(storeName, data);        }

        // refule_cost: purchase + angryBird_at_customer_count
        query = "SELECT S.name as store_name, (S.purchase + AB.angryBird_at_customer_count) as total " +
                "FROM Store S " +
                "JOIN StoreAttackByBird AB ON S.name = AB.store_name";
        resultSet = sqlHandler.query(query);
        while (resultSet.next()) {
            int totalFule = resultSet.getInt("total");
            String storeName = resultSet.getString("store_name");
            Integer[] data = storeData.getOrDefault(storeName, new Integer[]{0, 0, 0, 0});
            data[2] = totalFule;
            storeData.put(storeName, data);
        }

        resultSet = sqlHandler.query("SELECT distance_store_customer, store_name FROM StoreAttackByBird");
        while (resultSet.next()) {
            int distance_store_customer = resultSet.getInt("distance_store_customer");
            String storeName = resultSet.getString("store_name");
            Integer[] data = storeData.getOrDefault(storeName, new Integer[]{0, 0, 0, 0});
            data[3] = distance_store_customer;
            storeData.put(storeName, data);
        }

        for (String storeName : storeData.keySet()) {
            Integer[] data = storeData.get(storeName);
            int droneCount = data[0];
            int repairCount = data[1];
            int totalFule = data[2];
            int totalCost = droneCount + repairCount + totalFule;
            System.out.println("Store: " + storeName + ", Buy Drones Cost: " + droneCount +
                    ", Repair Drone Count: " + repairCount + ", Refuel Cost: " + totalFule +
                    " = Total Store Cost: " + totalCost + " , Waste fly distance: " + data[3]);
        }
    }
    public static void getAngrybirdAttackLocation(){
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "plot.py");
            pb.redirectErrorStream(true);
            Process p = pb.start();

            // Read the output of the Python program
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = p.waitFor();
            System.out.println("Python program finished with exit code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void displayEfficiency() throws SQLException {
        if (!currType.equals(UserType.admin.toString())) {
            System.out.println("need admin access");
            return;
        }
        String query = "SELECT name as store_name, purchase, overload, transfers FROM Store";
        ResultSet resultSet = sqlHandler.query(query);
        while (resultSet.next()) {
            String storeName = resultSet.getString("store_name");
            int purchase = resultSet.getInt("purchase");
            int overload = resultSet.getInt("overload");
            int transfers = resultSet.getInt("transfers");
            System.out.println("Store: " + storeName + ", Purchase: " + purchase + ", Overload: " + overload + ", Transfers: " + transfers);
        }
        System.out.println("OK:display_completed");
    }
    private void displaySummary() throws SQLException {
        displayStoreCost();
        getAngrybirdAttackLocation();
        System.out.println("OK:display_completed");
    }

    public static boolean usernamePasswordValidation(String s){

        for(int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')|| Character.isDigit(c)|| c =='-'||c=='_'){
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
}


