package asp.controller;

import asp.dtos.TaskDto;
import asp.model.Status;
import asp.model.Task;
import asp.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task Management")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.findAllTasks(); // Placeholder for actual task retrieval logic
    }

    @PostMapping
    public Task createTask(TaskDto task) {
        Task taskC = new Task();
        taskC.setTitle(task.getTitle());
        taskC.setDescription(task.getDescription());
        taskC.setStatus(Status.valueOf(task.getStatus()));
        taskC.setOwnerId(task.getOwnerId());
        taskC.setCreatedAt(task.getCreatedAt());
        taskC.setDeadline(task.getDeadline());
        taskC.setVolunteers(List.of(task.getVolunteers()));
        taskC.setSubTasks(task.getSubTasks() != null ? List.of(task.getSubTasks()) : List.of());

        return taskService.createTask(taskC); // Placeholder for actual task creation logic
    }
}
