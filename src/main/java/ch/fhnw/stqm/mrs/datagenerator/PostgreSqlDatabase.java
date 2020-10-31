package ch.fhnw.stqm.mrs.datagenerator;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

/**
 * Responsible to initialize database.
 */
public final class PostgreSqlDatabase implements Database {
	private PGSimpleDataSource ds;

    /** 
     * Create a connection to a database.
     * @throws Exception whenever something goes wrong.
     */
    public PostgreSqlDatabase(String url, String user, String pwd) throws Exception {
    	ds = new PGSimpleDataSource();
    	ds.setUrl(url);
        ds.setUser(user);
        ds.setPassword(pwd);
    }
    
    public void setup() {
        // create database tables only if they do not yet exist.
        DbLifeCycleMgmt db = new DbLifeCycleMgmt(ds);
        db.createTables();
    }
    
    public DataSource getDataSource() {
    	return ds;
    }
    
    public void teardown() {
    	DbLifeCycleMgmt db = new DbLifeCycleMgmt(ds);
    	db.dropTables();
    }

}
