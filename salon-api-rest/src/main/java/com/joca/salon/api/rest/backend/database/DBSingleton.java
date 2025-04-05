package com.joca.salon.api.rest.backend.database;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 *
 * @author joca
 */
public class DBSingleton {
    private static final String DIRECCION = "jdbc:mysql://localhost:3306/salon";
    private static final String USUARIO = "rootx";
    private static final String CONTRASEÑA = "password1234";
    
    private static DBSingleton UNICA_INSTANCIA_DE_DATASOURCE;

    private DataSource datasource;

    private DBSingleton() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            PoolProperties p = new PoolProperties();
            p.setUrl(DIRECCION);
            p.setDriverClassName("com.mysql.cj.jdbc.Driver");
            p.setUsername(USUARIO);
            p.setPassword(CONTRASEÑA);
            p.setJmxEnabled(true);
            p.setTestWhileIdle(false);
            p.setTestOnBorrow(true);
            p.setValidationQuery("SELECT 1");
            p.setTestOnReturn(false);
            p.setValidationInterval(30000);
            p.setTimeBetweenEvictionRunsMillis(30000);
            p.setMaxActive(100);
            p.setInitialSize(3);
            p.setMaxWait(10000);
            p.setRemoveAbandonedTimeout(10000);
            p.setMinEvictableIdleTimeMillis(30000);
            p.setMinIdle(3);
            p.setLogAbandoned(true);
            p.setRemoveAbandoned(true);
            p.setJdbcInterceptors(
                    "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                    + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
            datasource = new DataSource(p);
        } catch (ClassNotFoundException e) {
            System.out.println("Error al crear el singleton");
            e.printStackTrace();
        }
    }

    /**
     * Accede a una conexion con la base de datos
     * 
     * @return singleton base de datos
     */
    public static DBSingleton getInstance() {
        if (UNICA_INSTANCIA_DE_DATASOURCE == null) {
            UNICA_INSTANCIA_DE_DATASOURCE = new DBSingleton();
        }

        return UNICA_INSTANCIA_DE_DATASOURCE;
    }

    public DataSource getDatasource() {
        return datasource;
    }
}
