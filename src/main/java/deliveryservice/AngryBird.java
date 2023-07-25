package deliveryservice;
import sqlhandler.sqlHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;


public class AngryBird {
    private double attackProbability;
    private int id;


    public AngryBird(int id){
        this.id = id;
        this.attackProbability = 0;
    }

    public static boolean willAttack(int angryBirdID) throws SQLException {
        // query attackRate
        ResultSet resultSet = sqlHandler.query("SELECT attackRate FROM AngryBird WHERE angryBirdID = ?", angryBirdID);
        double attachRate = 0;
        while (resultSet.next()) {
            attachRate = resultSet.getDouble("attackRate");
        }
        Random rand = new Random();
        Double attackProbability = attachRate;
        attackProbability += rand.nextDouble();
        if (attackProbability >= 1.0){
            attackProbability = -2.02;
            String updateQuery = "UPDATE AngryBird SET attackRate = ? WHERE angryBirdID = ?";
            sqlHandler.update(updateQuery, attackProbability, angryBirdID);
            // udpate attachRate with attackProbability
            return true;
        } else {
            // udpate attachRate with attackProbability
            String updateQuery = "UPDATE AngryBird SET attackRate = ? WHERE angryBirdID = ?";
            sqlHandler.update(updateQuery, attackProbability, angryBirdID);
            return false;
        }
    }

    public void displayBirdInfo(){
        System.out.println("bird" + id + ": " + "attack probability :" + attackProbability);
    }

}
