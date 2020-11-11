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
	private Properties props; 

    public PostgreSqlDatabase(String pathToConfig) throws Exception {
        props = readDbConfig(pathToConfig);
        
        String url = props.getProperty("url");
        String username = props.getProperty("user");
        String pwd = props.getProperty("pwd");

        ds = new PGSimpleDataSource();
        ds.setUrl(url);
        ds.setUser(username);
        ds.setPassword(pwd);
    }

    private Properties readDbConfig(String configFile) throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        // note: getResourceAsStream works with classpath
        // see https://stackoverflow.com/questions/18053059/getresourceasstream-is-returning-null-properties-file-is-not-loading
        InputStream input = getClass().getResourceAsStream(configFile);
        prop.load(input);
        return prop;
    }
    
    public Properties getProperties() {
        return this.props;
    }
    
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
