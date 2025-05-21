package transportAgency.persistence.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtils {

    private final Properties jdbcProps;

    private static final Logger logger  = LogManager.getLogger();

    public JDBCUtils(Properties props) {
        jdbcProps = props;
    }

    private Connection instance = null;

    private Connection getNewConnection() {
        logger.traceEntry();
        String url = jdbcProps.getProperty("jakarta.persistence.jdbc.url");
        logger.info("trying to connect to database ... {}",url);
        Connection con = null;
        try {
            con = DriverManager.getConnection(url);
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit();
        return con;
    }

    public Connection getConnection() {
        logger.traceEntry();
        try {
            if (instance == null || instance.isClosed())
                instance = getNewConnection();
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit(instance);
        return instance;
    }
}
