/*
Hanna Weissberg 318796398
Libby Olidort 209274612
*/
package com.libby.hanna.drivingalltheway.model.backend;
import com.libby.hanna.drivingalltheway.model.datasource.Firebase_DBManager;

/**
 * Class to define who is the project using as an implementation of DB_manager
 * and to only create one DB_manager
 */
public class DBManagerFactory {
    static DB_manager db = null;
    public static DB_manager GetFactory() {
        if (db == null)
            db = new Firebase_DBManager();
        return db;
    }
}

