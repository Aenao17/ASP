package asp.service;

import asp.model.Status;
import asp.model.Task;
import asp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    public Task findTaskByTitle(String title) {
        return taskRepository.findByTitle(title)
                .orElseThrow(() -> new RuntimeException("Task not found with title: " + title));
    }

    public void changeTaskStatus(String title, String status) {
        Task task = findTaskByTitle(title);
        task.setStatus(Status.valueOf(status));
        taskRepository.save(task);
    }

    public Task createTask(Task task) {
        if (taskRepository.existsByTitle(task.getTitle())) {
            throw new RuntimeException("Task with title '" + task.getTitle() + "' already exists.");
        }
        return taskRepository.save(task);
    }

}
