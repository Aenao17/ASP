package asp.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class Task implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "title", unique = true)
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "status")
    private Status status;
    @Column(name = "owner_id")
    private String ownerId;
    @ElementCollection
    private List<String> volunteers;
    @ElementCollection
    private List<Integer> subTasks;
    @Column(name = "created_at")
    private String createdAt;
    @Column(name = "deadline")
    private String deadline;
    public Task(String title, String description, Status status, String ownerId, List<String> volunteers, List<Integer> subTasks, String createdAt, String deadline) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.ownerId = ownerId;
        this.volunteers = volunteers;
        this.subTasks = subTasks;
        this.createdAt = createdAt;
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", ownerId='" + ownerId + '\'' +
                ", volunteers=" + volunteers +
                ", subTasks=" + subTasks +
                ", createdAt=" + createdAt +
                ", deadline=" + deadline +
                '}';
    }
}
