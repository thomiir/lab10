package transportAgency.persistence.interfaces;


import transportAgency.model.Employee;

public interface IEmployeeRepository extends IRepository<Long, Employee> {

    Employee findByUsernameAndPassword(String username, String password);

    Employee findByUsername(String username);
}
