package ch.fhnw.stqm.mrs.datagenerator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSourceFactory;

/**
 * Responsible to initialize database.
 */
public final class HsqlDatabase {
    private DataSource ds;

    /** 
     * Create a connection to a Hsqldb database.
     * @throws Exception whenever something goes wrong.
     */
    public HsqlDatabase() throws Exception {
    	Properties props = readDbConfig("./db_config.properties");
    	
    	ds = JDBCDataSourceFactory.createDataSource(props);
    	
        // create database tables only if they do not yet exist.
        createDatabaseModel(ds.getConnection());
    }

	private Properties readDbConfig(String configFile) throws FileNotFoundException, IOException {
		Properties prop = new Properties();
        InputStream input = new FileInputStream(configFile);
    	prop.load(input);
		return prop;
	}
    
    /**
     * Create the database tables.
     */
    private void createDatabaseModel(Connection conn) {
        try {
            InputStream stream = getClass().getResourceAsStream("/data/DBSetup.script");
            List<String> commands = readAllLines(stream);
            
            for (String line: commands) {
                command(line, conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * use for SQL commands CREATE, DROP, INSERT and UPDATE.
     * 
     * @param expression SQL command
     * @throws SQLException when something went wrong
     */
    private synchronized void command(String expression, Connection connection) throws SQLException {
        Statement st = null;
        st = connection.createStatement(); // statements
        int i = st.executeUpdate(expression); // run the query
        if (i == -1) {
            System.out.println("db error : " + expression);
        }
        st.close();
    }

    private List<String> readAllLines(InputStream is) throws IOException {
        List<String> result = new LinkedList<>();
        BufferedReader b = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = b.readLine()) != null) {
            result.add(line);
        }
        return result;
    }

}
