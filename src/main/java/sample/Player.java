package sample;

public class Player {
    private String number;
    private String name;
    private String role;

    public Player(String number, String name, String role){
        this.number = number;
        this.name = name;
        this.role = role;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
