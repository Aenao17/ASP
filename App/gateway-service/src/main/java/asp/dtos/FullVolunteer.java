package asp.dtos;

public class FullVolunteer {
    private String username;
    private String email;
    private String institutionalEmail;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String birthday;
    private String departament;
    private Double points;

    public FullVolunteer(String username, String email, String institutionalEmail, String phoneNumber, String firstName, String lastName, String birthday, String departament, String points) {
        this.username = username;
        this.email = email;
        this.institutionalEmail = institutionalEmail;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.departament = departament;
        this.points = Double.parseDouble(points);
    }

    public FullVolunteer() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstitutionalEmail() {
        return institutionalEmail;
    }

    public void setInstitutionalEmail(String institutionalEmail) {
        this.institutionalEmail = institutionalEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }
}
