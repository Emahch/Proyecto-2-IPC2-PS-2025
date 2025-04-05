package com.joca.salon.api.rest.backend.database;

/**
 *
 * @author emahch
 */
public class DBConnection {

    protected DBSingleton dbSingleton;
    
    public DBConnection() {
        this.dbSingleton = DBSingleton.getInstance();
    }
}
