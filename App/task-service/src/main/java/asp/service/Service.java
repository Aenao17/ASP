package asp.service;

import asp.database.DAO;
import asp.model.Task;

import java.util.List;

public class Service {
    private final DAO dao = new DAO();

    public List<Task> getAllTasks() {
        return dao.getAllTasks();
    }

    public Integer addTask(Task task) {
        return dao.addTask(task);
    }

    public void assignUserToTask(String requestBody) {
        int taskId =Integer.parseInt(requestBody.split(",")[0].split(":")[1]);
        String volunteer  = requestBody.split(",")[1].split(":")[1].split("\"")[1];
        dao.assignTaskToUser(taskId, volunteer);
    }

    public void completeTask(int taskId) {
        dao.completeTask(taskId);
    }

    public Integer computePointsForUser(String username) {
        List<Task> tasks = dao.getAllTasks();
        int totalPoints = 0;
        for (Task task : tasks) {
            if (task.getVolunteers().contains(username) && task.getStatus().name().equals("COMPLETED")) {
                totalPoints += task.getPoints() != null ? task.getPoints() : 0;
            }
        }
        return totalPoints;
    }
}
