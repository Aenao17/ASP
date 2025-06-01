package asp.database;


import asp.model.Departament;
import asp.model.Volunteer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DAO {

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS volunteers (\n"
                + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    usernameLinked TEXT NOT NULL,\n"
                + "    points REAL DEFAULT 0.0,\n"
                + "    birthday TEXT NOT NULL,\n"
                + "    departament TEXT NOT NULL CHECK(departament IN ('HR', 'EVENTS', 'EXTERNE', 'IMPR'))\n"
                + ");";

        try(Connection conn = DBUtils.getConnection();) {
            Statement stmt = conn.createStatement();
            // create a new table
            stmt.execute(sql);
            System.out.println("Table initialized successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Volunteer> getAllVolunteers() {
        List<Volunteer> volunteers = new ArrayList<>();
        String sql = "SELECT * FROM volunteers";
        try (Connection conn = DBUtils.getConnection();
             Statement stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String usernameLinked = rs.getString("usernameLinked");
                Double points = rs.getDouble("points");
                String birthday = rs.getString("birthday");
                String departamentStr = rs.getString("departament");
                Departament departament = Departament.valueOf(departamentStr);
                Volunteer volunteer = new Volunteer(usernameLinked, points, birthday, departament);
                volunteers.add(volunteer);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return volunteers;
    }

    public void addVolunteer(String usernameLinked, Double points, String birthday, Departament departament) {
        String sql = "INSERT INTO volunteers (usernameLinked, points, birthday, departament) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtils.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usernameLinked);
            pstmt.setDouble(2, points);
            pstmt.setString(3, birthday);
            pstmt.setString(4, departament.name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
