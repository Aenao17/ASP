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
                    ownerId INTEGER,
                    createdAt TEXT NOT NULL,
                    deadline TEXT NOT NULL
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
        String sql3 = """
    CREATE TABLE IF NOT EXISTS task_task (
        taskId1 INTEGER NOT NULL,
        taskId2 INTEGER NOT NULL,
        FOREIGN KEY (taskId1) REFERENCES tasks(id) ON DELETE CASCADE,
        FOREIGN KEY (taskId2) REFERENCES tasks(id) ON DELETE CASCADE
    );""";

        try(Connection conn = DBUtils.getConnection()) {
            Statement stmt = conn.createStatement();
            // create a new table
            stmt.execute(sql1);
            stmt.execute(sql2);
            stmt.execute(sql3);
            System.out.println("Table initialized successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Task> getAllTasks() {
        String sql1 = "SELECT * FROM tasks";
        String sql2 = "SELECT * FROM volunteers_tasks";
        String sql3 = "SELECT * FROM task_task";

        try (Connection conn = DBUtils.getConnection()) {
            Statement stmt1 = conn.createStatement();
            Statement stmt2 = conn.createStatement();
            Statement stmt3 = conn.createStatement();

            var rs1 = stmt1.executeQuery(sql1);
            var rs2 = stmt2.executeQuery(sql2);
            var rs3 = stmt3.executeQuery(sql3);

            List<Task> tasks = new ArrayList<>();

            while (rs1.next()) {
                int id = rs1.getInt("id");
                String title = rs1.getString("title");
                String description = rs1.getString("description");
                String statusul = rs1.getString("status");
                Status status = Status.valueOf(statusul);
                Integer ownerId = rs1.getInt("ownerId");
                String createdAt = rs1.getString("createdAt");
                String deadline = rs1.getString("deadline");

                Task task = new Task(id, title, description, status, ownerId, createdAt, deadline);
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

            while (rs3.next()) {
                int taskId1 = rs3.getInt("taskId1");
                int taskId2 = rs3.getInt("taskId2");

                for(Task task : tasks) {
                    if(task.getId() == taskId1) {
                        List<Integer> subTasks = task.getSubTasks();
                        if (!subTasks.contains(taskId2)) {
                            subTasks.add(taskId2);
                        }
                        task.setSubTasks(subTasks);
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
        String sql = "INSERT INTO tasks (title, description, status, ownerId, createdAt, deadline) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtils.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getStatus().name());
            pstmt.setObject(4, task.getOwnerId());
            pstmt.setString(5, task.getCreatedAt());
            pstmt.setString(6, task.getDeadline());
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
    }
}
