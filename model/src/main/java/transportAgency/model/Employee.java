package transportAgency.model;


import jakarta.persistence.Basic;
import jakarta.persistence.Table;

@jakarta.persistence.Entity
@Table(name="employees")
public class Employee extends Entity<Long> {
    @Basic
    private String username;
    @Basic
    private String password;

    public Employee(Long id, String username, String password) {
        setId(id);
        this.username = username;
        this.password = password;
    }

    public Employee() {

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + getId() + '\'' +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
