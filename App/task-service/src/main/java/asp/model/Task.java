package asp.model;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private int id;
    private String title;
    private String description;
    private Status status;
    private String ownerUsername;
    private String createdAt;
    private String deadline;
    private Integer points;
    private List<String> volunteers;

    public Task() {
    }

    public Task(int id, String title, String description, Status status, String ownerUsername, String createdAt, String deadline, Integer points) {
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

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getownerUsername() {
        return ownerUsername;
    }

    public void setownerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public List<String> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(List<String> volunteers) {
        this.volunteers = volunteers;
    }
}
