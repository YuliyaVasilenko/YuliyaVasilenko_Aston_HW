package dao;

import entity.User;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ Description: обеспечивает однократное создание
 * экземпляра SessionFactory в sessionFactory и сохранение
 * его для последующих использований
 */
@NoArgsConstructor
public class HibernateSetup {
    private static final Logger logger = LoggerFactory.getLogger(HibernateSetup.class);
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(User.class);

                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
                logger.info("Hibernate SessionFactory created successfully");
            } catch (Exception e) {
                logger.error("Failed to create SessionFactory: {}", e.getMessage());
                if (sessionFactory != null) {
                    sessionFactory.close();
                }
            }
        }
        return sessionFactory;
    }
}