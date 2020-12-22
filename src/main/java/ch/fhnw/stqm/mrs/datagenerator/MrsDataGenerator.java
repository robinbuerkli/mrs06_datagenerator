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
	    // specify claspatgh to the config file
	    PostgreSqlDatabase db = new PostgreSqlDatabase();


        int nOfUsers = Integer.parseInt(System.getenv("MRS_USERS"));
        int nOfMovies = Integer.parseInt(System.getenv("MRS_MOVIES"));
        int nOfRentals = Integer.parseInt(System.getenv("MRS_RENTALS"));
	    
	    if (args.length > 0 && "teardown".equals(args[0])) {
	    	db.teardown();
	    	System.out.println("Database dropped.");
	    	return;
	    } else if (args.length == 3) { // assume the numbers are passed int
	        nOfUsers = Integer.parseInt(args[0]); 
	        nOfMovies = Integer.parseInt(args[1]);
	        nOfRentals = Integer.parseInt(args[2]);
	    }

	    
	    db.setup();
	
		Dataloader loader = new GeneratingDataloader(db.getDataSource(), nOfUsers, nOfMovies, nOfRentals);
		loader.load();
		System.out.println("Done: database generated and filled");

	}
	
	
	/**
	 * Prevent instantiation.
	 */
	private MrsDataGenerator() { }

}
