package com.libby.hanna.drivingalltheway.model.backend;
public class DBManagerFactory {
    static DB_manager db = null;
    public static DB_manager GetFactory() {
        if (db == null)
            db = new Firebase_DBManager();
        return db;
    }
}

