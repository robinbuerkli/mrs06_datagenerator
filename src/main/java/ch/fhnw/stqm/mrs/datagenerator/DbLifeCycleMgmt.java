package ch.fhnw.stqm.mrs.datagenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;


public class DbLifeCycleMgmt {
    
    private static final String createMoviesTable =
            "CREATE TABLE IF NOT EXISTS movies ( "
            + "MovieId uuid NOT NULL, "
            + "Title text NOT NULL, "
            + "IsRented boolean NOT NULL, "
            + "ReleaseDate date NOT NULL, "
            + "PriceCategory text NOT NULL, "
            + "AgeRating integer NOT NULL, "
            + "CONSTRAINT movies_pkey PRIMARY KEY (MovieId)" 
            + ");";
    private static final String createUsersTable =
            "CREATE TABLE IF NOT EXISTS users ( "
            + "UserId uuid NOT NULL, "
            + "Name text NOT NULL, "
            + "FirstName text NOT NULL, "
            + "Birthdate date NOT NULL, "
            + "CONSTRAINT users_pkey PRIMARY KEY (UserId) "
            + ");";
    private static final String createRentalsTable =
            "CREATE TABLE IF NOT EXISTS rentals ( "
            + "RentalId uuid NOT NULL, "
            + "MovieId uuid NOT NULL, "
            + "UserId uuid NOT NULL, "
            + "RentalDate date NOT NULL, "
            + "CONSTRAINT rentals_pkey PRIMARY KEY (RentalId), "
            + "CONSTRAINT NoDuplicateRentals UNIQUE (MovieId, UserId), "
            + "CONSTRAINT movieFK FOREIGN KEY (MovieId) "
            + "    REFERENCES movies (MovieId) MATCH SIMPLE "
            + "    ON UPDATE NO ACTION "
            + "    ON DELETE NO ACTION, "
            + "CONSTRAINT userFK FOREIGN KEY (UserId) "
            + "    REFERENCES users (UserId) MATCH SIMPLE "
            + "    ON UPDATE NO ACTION "
            + "    ON DELETE NO ACTION "
            + ");";
    private static final String dropMoviesTable  = "DROP TABLE movies";
    private static final String dropUsersTable   = "DROP TABLE users";
    private static final String dropRentalsTable = "DROP TABLE rentals";
    DataSource ds;
	
	/**
	 * @param sql2o the sql to dao param
	 */
	public DbLifeCycleMgmt(DataSource ds) {
	    this.ds = ds;
	}
	
	public void createTables() {
		try (Connection conn = ds.getConnection()) {
		    Statement statement = conn.createStatement();
            statement.execute(createMoviesTable);
            statement.execute(createUsersTable);
            statement.execute(createRentalsTable);
		} catch (SQLException se) {
		    se.printStackTrace();
		}
	}
	
	public void dropTables() {
        try (Connection conn = ds.getConnection()) {
            Statement statement = conn.createStatement();
            statement.execute(dropRentalsTable);
            statement.execute(dropMoviesTable);
            statement.execute(dropUsersTable);
        } catch (SQLException se) {
            se.printStackTrace();
        }
	}

}
