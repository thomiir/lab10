package transportAgency.dto;

import java.io.Serializable;


public class EmployeeDTO implements Serializable{

    private String username;
    private final String password;


    public EmployeeDTO(String user, String pass) {
        this.username = user;
        this.password = pass;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString(){
        return "UserDTO["+username+"]";
    }

    public String getPassword() {
        return password;
    }
}