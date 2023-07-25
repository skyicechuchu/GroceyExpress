import java.sql.*;

public class dbtest {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/grocery_db";
        String user = "root";
        String password = "rootUser";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to MySQL in Docker container!");
            Statement smt = connection.createStatement();
            ResultSet rs = smt.executeQuery("select * from User");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
