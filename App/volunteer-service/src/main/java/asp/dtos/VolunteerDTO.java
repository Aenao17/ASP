package asp.dtos;

import asp.model.Departament;

import java.time.LocalDateTime;

public class VolunteerDTO {
    private String usernameLinked;
    private Double points;
    private String birthday; // Changed to String for easier JSON serialization
    private String departament; // Changed to String for easier JSON serialization

    public VolunteerDTO(String usernameLinked, Double points, LocalDateTime birthday, Departament departament) {
        this.usernameLinked = usernameLinked;
        this.points = points;
        this.birthday = birthday.toString();
        this.departament = departament.name();
    }

    public String getUsernameLinked() {
        return usernameLinked;
    }

    public void setUsernameLinked(String usernameLinked) {
        this.usernameLinked = usernameLinked;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDepartament() {
        return departament;
    }

    public void setDepartament(String departament) {
        this.departament = departament;
    }
}
