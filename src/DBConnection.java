// File: src/DBConnection.java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Leave empty if no MySQL password in XAMPP

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found! Add mysql-connector.jar to your project.");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.out.println("Database connection failed! Make sure XAMPP MySQL is running.");
            e.printStackTrace();
            return null;
        }
    }
}