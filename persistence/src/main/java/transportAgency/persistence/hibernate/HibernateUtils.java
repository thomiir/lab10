package transportAgency.persistence.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import transportAgency.model.Employee;
import transportAgency.model.Reservation;
import transportAgency.model.Trip;

import java.io.IOException;
import java.util.Properties;

public class HibernateUtils {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory(){
        if (sessionFactory == null || sessionFactory.isClosed())
            sessionFactory=createNewSessionFactory();
        return sessionFactory;
    }

    private static SessionFactory createNewSessionFactory(){
        sessionFactory = new Configuration()
                .setProperties(new Properties() {{
                    try {
                        load(ClassLoader.getSystemResourceAsStream("hibernate.properties"));
                    } catch (IOException e) {
                        System.err.println(e.getLocalizedMessage());
                    }
                }})
                .addAnnotatedClass(Employee.class)
                .addAnnotatedClass(Reservation.class)
                .addAnnotatedClass(Trip.class)
                .buildSessionFactory();
        return sessionFactory;
    }

    public static void closeSessionFactory(){
        if (sessionFactory != null)
            sessionFactory.close();
    }
}
