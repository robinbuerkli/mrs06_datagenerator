package ch.fhnw.stqm.mrs.datagenerator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Load data into MRS database.
 */
public final class MrsDataGenerator {

    /**
     * Start the loader.
     * @param args arguments
     * @throws Exception whenever something goes wrong.
     */
	public static void main(String[] args) throws Exception {
    	Properties props = readDbConfig("./db_config.properties");
    	
    	String server = props.getProperty("server");
        int port = Integer.parseInt(props.getProperty("port"));
        String username = props.getProperty("user");
        String pwd = props.getProperty("pwd");
	    int nOfUsers = Integer.parseInt(props.getProperty("users")); 
	    int nOfMovies = Integer.parseInt(props.getProperty("movies"));
	    int nOfRentals = Integer.parseInt(props.getProperty("rentals"));


	    PostgreSqlDatabase db = new PostgreSqlDatabase(server, port, username, pwd);

	    Dataloader loader = new GeneratingDataloader(db.getDataSource(), nOfUsers, nOfMovies, nOfRentals);
	    loader.load();
	    System.out.println("Datagenerator: done");
	}
	
	private static Properties readDbConfig(String configFile) throws FileNotFoundException, IOException {
		Properties prop = new Properties();
        InputStream input = new FileInputStream(configFile);
    	prop.load(input);
		return prop;
	}
	
	/**
	 * Prevent instantiation.
	 */
	private MrsDataGenerator() { }

}
