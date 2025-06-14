package asp.service;

import asp.database.DAO;
import asp.model.Task;

import java.util.List;

public class Service {
    private final DAO dao = new DAO();

    public List<Task> getAllTasks() {
        return dao.getAllTasks();
    }

    public void addTask(Task task) {
        dao.addTask(task);
    }

    public void assignUserToTask(String requestBody) {
        int taskId =Integer.parseInt(requestBody.split(",")[0].split(":")[1]);
        String volunteer  = requestBody.split(",")[1].split(":")[1].split("\"")[1];
        dao.assignTaskToUser(taskId, volunteer);
    }
}
