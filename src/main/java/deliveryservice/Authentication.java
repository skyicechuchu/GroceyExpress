package deliveryservice;
import org.junit.experimental.max.MaxCore;
import sqlhandler.sqlHandler;
import users.SuperUser;

import java.sql.ResultSet;
import java.util.Scanner;
import java.util.*;

public class Authentication {
    public static int MAX_ATTEMPTS= 5;
    public void login() {


        int remainingAttempts = MAX_ATTEMPTS;
        int waitRound = 0;

        Scanner commandLineInput = new Scanner(System.in);
        String wholeInputLine;
        String[] tokens;
        final String DELIMITER = ",";
        System.out.println("Please enter your account and password separated by comma for login." +
                "To exit, please enter stop");

        while (true) {

            try {
                wholeInputLine = commandLineInput.nextLine();
                tokens = wholeInputLine.split(DELIMITER);
                System.out.println("> " + wholeInputLine);


                if(tokens[0].length()>= 2 && tokens[0].substring(0,2).equals("//")){
                    waitRound ++;
                    continue;
                } else if (tokens[0].equals("stop")) {
                    System.out.println("stop acknowledged.goodbye.");
                    break;
                } else if (tokens.length != 2){
                    System.out.println("Login not recognized. Only letters, digits, underscore, and dash allowed in username or password");
                    remainingAttempts --;
                    System.out.println("remaining attempts: " + remainingAttempts);

                } else {
                    String account = tokens[0];
                    String password = tokens[1];
                    if (!DeliveryService.usernamePasswordValidation(account)){
                        System.out.println("Illegal characters in account. Only letters, digits, underscore, and dash allowed in username or password");
                        remainingAttempts --;
                        System.out.println("remaining attempts: " + remainingAttempts);
                        continue;
                    }
                    if (!DeliveryService.usernamePasswordValidation(password)){
                        System.out.println("Illegal characters in password. Only letters and digits allowed in username or password");
                        remainingAttempts --;
                        System.out.println("remaining attempts: " + remainingAttempts);
                        continue;
                    }


                    ResultSet resultSet = sqlHandler.query("SELECT * FROM User WHERE account = ? AND password = ?", account,password);
                    if(resultSet!= null && resultSet.next() ){
                        remainingAttempts = MAX_ATTEMPTS;
                        System.out.println("Login Successful. Redirecting to delivery service dashboard");
                        String userType = resultSet.getString("role");
                        System.out.println("Your logging type is "+ userType);
                        String userAccount = tokens[0];
                        DeliveryService simulator = new DeliveryService(userType, userAccount);
                        simulator.commandLoop();
                    } else {
                        System.out.println("Invalid username/password combination.");
                        remainingAttempts --;
                        System.out.println("remaining attempts: " + remainingAttempts);
                    }
                }


                if(remainingAttempts <= 0){
                    System.out.println("No more remaining attempts.");
                    break;
                }
                if(waitRound>= MAX_ATTEMPTS*10){
                    System.out.println("No commands received for extended period. Exiting program.");
                    break;
                }

            } catch (Exception e) {

//                e.printStackTrace();
                System.out.println("System error, contact admin at team128.");
            }
        }
        System.out.println("Bye!");
        commandLineInput.close();
    }

}
