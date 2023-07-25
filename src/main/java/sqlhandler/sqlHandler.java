package sqlhandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class sqlHandler {

    private static final String DB_URL = "jdbc:mysql://database-1.cngbcqgzo5zf.us-east-2.rds.amazonaws.com:3306/grocery_db";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "8439523026_Zqc";

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            } catch (SQLException e) {
                System.out.println("Error: cannot connect to database");
//                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void insert(String sql, Object... params) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error: cannot insert or update database");
//            e.printStackTrace();
        }
    }

    public static ResultSet query(String sql, Object... params) {
        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }

            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("Error: cannot query database");
        }
        return resultSet;
    }

    public static void update(String sql, Object... params) {
        insert(sql, params);
    }

    public static void delete(String sql, Object... params) {
        insert(sql, params);
    }
}
