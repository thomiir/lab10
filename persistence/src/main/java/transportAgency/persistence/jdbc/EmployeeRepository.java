package example.repository;

import example.domain.Employee;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import example.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmployeeRepository implements IEmployeeRepository {

    private static JDBCUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();

    public EmployeeRepository(Properties properties) {
        logger.info("Init EmployeeRepository, properties: {}", properties);
        dbUtils = new JDBCUtils(properties);
    }

    @Override
    public Employee findByUsernameAndPassword(String username, String password) {
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select * from employees where username=? and password=?")) {
            preStmt.setString(1, username);
            preStmt.setString(2, password);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next())
                {
                    logger.traceExit("Employee {} found", username);
                    return createEmployee(result);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public Employee findOne(Long id) {
        logger.traceEntry("Find employee {} ", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select * from employees where id = ?")) {
            preStmt.setLong(1, id);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Employee employee = createEmployee(result);
                    logger.traceExit("Employee {} found", id);
                    return employee;
                }
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException(e);
            }
        } catch (SQLException ex) {
            logger.error(ex);
        }
        logger.traceExit("Employee {} not found", id);
        return null;
    }

    @Override
    public Iterable<Employee> findAll() {
        logger.traceEntry("Find all employees");
        List<Employee> employeeList = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select * from employees")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next())
                    employeeList.add(createEmployee(result));
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit();
        return employeeList;
    }

    private static Employee createEmployee(ResultSet result) throws SQLException {
        Long id = result.getLong(1);
        String username = result.getString(2);
        String password = result.getString(3);
        return new Employee(id, username, password);
    }

    @Override
    public void save(Employee entity) {
        logger.traceEntry("Save employee {} ", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("insert into employees values (?,?,?)")) {
            preStmt.setLong(1, entity.getId());
            preStmt.setString(2, entity.getUsername());
            preStmt.setString(3, entity.getPassword());
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} employees", result);
        } catch (SQLException ex) {
            logger.error(ex);
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        logger.traceExit();
    }

    @Override
    public void delete(Long id) {
        logger.traceEntry("Delete employee {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("delete from employees where id=?")) {
            preStmt.setLong(1, id);
            int result = preStmt.executeUpdate();
            logger.trace("Deleted {} employees", result);
        } catch (SQLException ex) {
            logger.error(ex);
        }
        logger.traceExit();
    }

    @Override
    public void update(Long id, Employee entity) {
        logger.traceEntry("Update employee {} ", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = con.prepareStatement("update employees set username = ?, password = ? where id = ?")) {
            preparedStatement.setString(1, entity.getUsername());
            preparedStatement.setString(2, entity.getPassword());
            preparedStatement.setLong(3, id);
            int result = preparedStatement.executeUpdate();
            logger.trace("Updated {} employees", result);
        } catch (SQLException ex) {
            logger.error(ex);
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        logger.traceExit();
    }
}
