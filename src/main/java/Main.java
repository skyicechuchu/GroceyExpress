//import deliveryservice.DeliveryService;
import deliveryservice.*;
public class Main {

    public static void main(String[] args) {
//        System.out.println("Welcome to the Grocery Express Delivery Service!");
//        DeliveryService simulator = new DeliveryService();
//        simulator.commandLoop();
        System.out.println("Welcome to the Grocery Express Delivery Service!");
        Authentication authentication = new Authentication();
        authentication.login();
    }
}
