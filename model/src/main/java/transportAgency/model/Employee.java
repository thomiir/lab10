package transportAgency.model;

public class Employee extends Entity<Long>{
    private final String username;
    private final String password;

    public Employee(Long id, String username, String password) {
        setId(id);
        this.username = username;
        this.password = password;
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
