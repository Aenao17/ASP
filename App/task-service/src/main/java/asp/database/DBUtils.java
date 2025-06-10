package asp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {
    private static final String DB_URL = "jdbc:sqlite:C:/Licenta/ASP/App/task-service/src/main/resources/tasks.db";

    public  static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

}
