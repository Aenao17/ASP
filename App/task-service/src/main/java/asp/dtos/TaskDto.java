package asp.dtos;

public class TaskDto {
    private String title;
    private String description;
    private String status;
    private String ownerId;
    private String createdAt;
    private String deadline;
    private String[] volunteers;
    private Integer[] subTasks;

    public TaskDto() {
    }

    public TaskDto(String title, String description, String status, String ownerId, String createdAt, String deadline, String[] volunteers, Integer[] subTasks) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.volunteers = volunteers;
        this.subTasks = subTasks;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
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

    public String[] getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(String[] volunteers) {
        this.volunteers = volunteers;
    }

    public Integer[] getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(Integer[] subTasks) {
        this.subTasks = subTasks;
    }
}
