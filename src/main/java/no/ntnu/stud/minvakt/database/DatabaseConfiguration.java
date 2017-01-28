package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.jersey.MinVaktApp;
import no.ntnu.stud.minvakt.util.TravisUtil;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by Audun on 27.01.2017.
 */
public class DatabaseConfiguration {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private static final DataSource dataSource;

    static {
        PoolProperties p = new PoolProperties();
        p.setDriverClassName("com.mysql.cj.jdbc.Driver");

        if (TravisUtil.isTravis()) {
            p.setUrl("jdbc:mysql://localhost/test");
            p.setUsername("root");
            p.setPassword("");
        } else {
            p.setUrl(MinVaktApp.getAppProperties().getProperty("mysql.url"));
            p.setUsername(MinVaktApp.getAppProperties().getProperty("mysql.user"));
            p.setPassword(MinVaktApp.getAppProperties().getProperty("mysql.password"));
        }

        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);

        // Pool size settings
        p.setInitialSize(1);
        p.setMinIdle(0);
        p.setMaxIdle(5);
        p.setMaxActive(20);

        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        dataSource = new DataSource();
        dataSource.setPoolProperties(p);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
