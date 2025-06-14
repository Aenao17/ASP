package asp.database;

import asp.model.Status;
import asp.model.Task;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DAO {

    public void createTable() {
        String sql1 = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL,
                    status TEXT NOT NULL CHECK(status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'EXPIRED')),
                    ownerUsername TEXT NOT NULL,
                    createdAt TEXT NOT NULL,
                    deadline TEXT NOT NULL,
                    points INTEGER NOT NULL
                );
                """;
        String sql2 = """
    
                CREATE TABLE IF NOT EXISTS volunteers_tasks (
        taskId INTEGER NOT NULL,
        volunteerId TEXT NOT NULL,
        FOREIGN KEY (taskId) REFERENCES tasks(id) ON DELETE CASCADE,
        FOREIGN KEY (volunteerId) REFERENCES volunteers(id) ON DELETE CASCADE
    );
    """;

        try(Connection conn = DBUtils.getConnection()) {
            Statement stmt = conn.createStatement();
            // create a new table
            stmt.execute(sql1);
            stmt.execute(sql2);
            System.out.println("Table initialized successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Task> getAllTasks() {
        String sql1 = "SELECT * FROM tasks";
        String sql2 = "SELECT * FROM volunteers_tasks";

        try (Connection conn = DBUtils.getConnection()) {
            Statement stmt1 = conn.createStatement();
            Statement stmt2 = conn.createStatement();
            var rs1 = stmt1.executeQuery(sql1);
            var rs2 = stmt2.executeQuery(sql2);

            List<Task> tasks = new ArrayList<>();

            while (rs1.next()) {
                int id = rs1.getInt("id");
                String title = rs1.getString("title");
                String description = rs1.getString("description");
                String statusul = rs1.getString("status");
                Status status = Status.valueOf(statusul);
                String ownerUsername = rs1.getString("ownerUsername");
                String createdAt = rs1.getString("createdAt");
                String deadline = rs1.getString("deadline");
                Integer points = rs1.getInt("points");

                Task task = new Task(id, title, description, status, ownerUsername, createdAt, deadline, points);
                tasks.add(task);
            }

            while (rs2.next()) {
                int taskId = rs2.getInt("taskId");
                String volunteerId = rs2.getString("volunteerId");
                for(Task task : tasks) {
                    if(task.getId() == taskId) {
                        List<String> volunteers = task.getVolunteers();
                        if (!volunteers.contains(volunteerId)) {
                            volunteers.add(volunteerId);
                        }
                        task.setVolunteers(volunteers);
                        break;
                    }
                }
            }

            return tasks;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }

    public void addTask(Task task) {
        String sql = "INSERT INTO tasks (title, description, status, ownerUsername, createdAt, deadline, points) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtils.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getStatus().name());
            pstmt.setObject(4, task.getownerUsername());
            pstmt.setString(5, task.getCreatedAt());
            pstmt.setString(6, task.getDeadline());
            pstmt.setInt(7, task.getPoints());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void assignTaskToUser(int taskId, String username) {
        String sql = "INSERT INTO volunteers_tasks (taskId, volunteerId) VALUES (?, ?)";
        try (Connection conn = DBUtils.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Update the task status to IN_PROGRESS if it was PENDING
        String updateSql = "UPDATE tasks SET status = 'IN_PROGRESS' WHERE id = ? AND status = 'PENDING'";
        try (Connection conn = DBUtils.getConnection();
             var pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void completeTask(int taskId) {
        String sql = "UPDATE tasks SET status = 'COMPLETED' WHERE id = ?";
        try (Connection conn = DBUtils.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}