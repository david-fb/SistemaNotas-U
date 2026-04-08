package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static final String DATABASE_NAME = "sistema_notas";
    private static final String URL = "jdbc:postgresql://localhost:5432/" + DATABASE_NAME;
    private static final String USER = "postgres";
    private static final String PASS = "admin";

    private static Connection connection = null;

    private DatabaseConfig() {
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("Conexión a BD exitosa");
            }
        } catch (SQLException e) {
            System.err.println("Error conectando a BD: " + e.getMessage());
        }
        return connection;
    }
}
