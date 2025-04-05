package transportAgency.persistence;


import transportAgency.model.Employee;

public interface IEmployeeRepository extends IRepository<Long, Employee> {

    Employee findByUsernameAndPassword(String username, String password);
}
