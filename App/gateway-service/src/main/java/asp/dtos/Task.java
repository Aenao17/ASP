package asp.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Task {
    @Setter
    @Getter
    private int id;
    @Setter
    @Getter
    private String title;
    @Setter
    @Getter
    private String description;
    private String status;
    private Integer ownerId;
    private String createdAt;
    private String deadline;
    private List<Integer> volunteers;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
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

    public List<Integer> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(List<Integer> volunteers) {
        this.volunteers = volunteers;
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Integer> subTasks) {
        this.subTasks = subTasks;
    }
}
