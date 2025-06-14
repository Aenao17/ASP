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
    private String ownerUsername;
    private String createdAt;
    private String deadline;
    private Integer points;
    private List<String> volunteers;

    public Task() {
    }

    public Task(int id, String title, String description, String status, String ownerUsername, String createdAt, String deadline, Integer points) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.ownerUsername = ownerUsername;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.points = points;
        this.volunteers = new ArrayList<>();
    }

}
