package asp.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Task {
    private int id;
    private String title;
    private String description;
    private String status;
    private Integer ownerId;
    private String createdAt;
    private String deadline;
    private List<String> volunteers;
    private List<Integer> subTasks;

    public Task() {
    }

    public Task(int id, String title, String description, String status, Integer ownerId, String createdAt, String deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.volunteers = new ArrayList<>();
        this.subTasks = new ArrayList<>();
    }

}
