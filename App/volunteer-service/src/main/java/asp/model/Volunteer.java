package asp.model;

import java.time.LocalDateTime;

public class Volunteer {
    private String usernameLinked;
    private Double points;
    private LocalDateTime birthday;
    private Departament departament;

    public Volunteer(String usernameLinked, Double points, LocalDateTime birthday, Departament departament) {
        this.usernameLinked = usernameLinked;
        this.points = points;
        this.birthday = birthday;
        this.departament = departament;
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

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public Departament getDepartament() {
        return departament;
    }

    public void setDepartament(Departament departament) {
        this.departament = departament;
    }
}
