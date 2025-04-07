package transportAgency.dto;

import java.io.Serial;
import java.io.Serializable;


public class EmployeeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String username;
    private final String password;


    public EmployeeDTO(String user, String pass) {
        this.username = user;
        this.password = pass;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString(){
        return "UserDTO["+username+"]";
    }

    public String getPassword() {
        return password;
    }
}