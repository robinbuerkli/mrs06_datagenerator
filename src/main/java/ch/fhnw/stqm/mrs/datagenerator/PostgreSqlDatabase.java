package ch.fhnw.stqm.mrs.datagenerator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

/**
 * Responsible to initialize database.
 */
public final class PostgreSqlDatabase implements Database {
	private PGSimpleDataSource ds;

    public PostgreSqlDatabase() throws Exception {
        String username = System.getenv("POSTGRES_USER");
        String pwd = System.getenv("POSTGRES_PASSWORD");
        String db =  System.getenv("POSTGRES_DB");
        String url = String.format("jdbc:postgresql://:5432/%s?socketFactory=org.newsclub.net.unix.socketfactory.PostgresqlAFUNIXSocketFactory&socketFactoryArg=/var/run/postgresql/.s.PGSQL.5432", db);

        ds = new PGSimpleDataSource();
        ds.setURL(url);
        ds.setUser(username);
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
