package example.repository;

import example.domain.Employee;

public interface IEmployeeRepository extends IRepository<Long, Employee> {

    Employee findByUsernameAndPassword(String username, String password);
}
