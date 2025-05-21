package transportAgency.persistence.hibernate;

import org.hibernate.Session;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import transportAgency.model.Employee;
import transportAgency.persistence.interfaces.IEmployeeRepository;

@Component
public class EmployeeRepository implements IEmployeeRepository {
    private static final Logger logger = LogManager.getLogger(EmployeeRepository.class);

    @Override
    public Employee findByUsernameAndPassword(String username, String password) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Query<Employee> query = session.createQuery("from Employee where username=:usernameM and password=:passwordM", Employee.class);
            query.setParameter("usernameM", username);
            query.setParameter("passwordM", password);
            return query.uniqueResultOptional().orElse(null);
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    @Override
    public Employee findByUsername(String username) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Query<Employee> query = session.createQuery("from Employee where username=:usernameM", Employee.class);
            query.setParameter("usernameM", username);
            return query.uniqueResultOptional().orElse(null);
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    @Override
    public Employee findOne(Long aLong) {
        return null;
    }

    @Override
    public Iterable<Employee> findAll() {
        return null;
    }

    @Override
    public Employee save(Employee entity) {
        return new Employee();
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public Employee update(Long aLong, Employee entity) {
        return new Employee();
    }
}
