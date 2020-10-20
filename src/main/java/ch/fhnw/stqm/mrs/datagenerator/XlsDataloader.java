package ch.fhnw.stqm.mrs.datagenerator;

import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

import javax.sql.DataSource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class XlsDataloader implements Dataloader {
    private static final String GET_CLIENTS = "select * from CLIENTS";
    private static final String GET_MOVIES = "select * from MOVIES";

    private static final String MOVIE_SQL = 
            "INSERT INTO movies (title, isrented, releasedate, pricecategory, agerating)"
            + "  VALUES (?, ?, ?, ?, ?)";
    private static final String CLIENT_SQL = "INSERT INTO clients ( firstname, name, birthdate ) VALUES ( ?, ?, ? )";
    private static final String RENTAL_SQL = "INSERT INTO rentals ( movieid, clientid, rentaldate )"
            + "  VALUES ( ?, ?, ? )";

    private Connection connection;
    
    public XlsDataloader(DataSource ds) throws Exception {
        this.connection = ds.getConnection();
    }

    @Override
    public void load() throws Exception {
    	
        System.out.println("Loading from csv file...");
       // Check if there is client and movie data, otherwise initialize the DB.
        PreparedStatement clientsGet = connection.prepareStatement(GET_CLIENTS);
        ResultSet clients = clientsGet.executeQuery();

        PreparedStatement moviesGet = connection.prepareStatement(GET_MOVIES);
        ResultSet movies = moviesGet.executeQuery();

        if (!clients.next() || !movies.next()) {
            readMovies("/data/movies.csv");
            readUsers("/data/users.csv");
            readRentals("/data/rentals.csv");
        }
    }

    /**
     * Read list of movies from CSV file.
     * @param moviesCsv the file containing the data.
     */
    public void readMovies(String moviesCsv) {
        System.out.print("loading from /data/movies.csv ");
        int count = 0;
        try (Reader in = new InputStreamReader(getClass().getResourceAsStream(moviesCsv))) {
            Iterable<CSVRecord> movies = CSVFormat.EXCEL.withFirstRecordAsHeader().withHeader(MovieHeaders.class)
                    .withDelimiter(';').parse(in);
            for (CSVRecord m : movies) {
                String title = m.get(MovieHeaders.Title);
                LocalDate releaseDate = LocalDate.parse(m.get(MovieHeaders.ReleaseDate));
                String pc = m.get(MovieHeaders.PriceCategory);
                int ageRating = Integer.parseInt(m.get(MovieHeaders.AgeRating));
                boolean isRented = Boolean.parseBoolean(m.get(MovieHeaders.isRented));
                writeMovieToDb(title, releaseDate, pc, ageRating, isRented);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("... done: " + count + " records loaded.");
    }

    /**
     * Insert a single movie record to the movies table.
     * 
     * @param title of movie
     * @param releasedAt release date of movie
     * @param priceCategory of movie
     * @param rating age rating of movie
     * @param rented whether movie is rented
     * @throws Exception on problems
     */
    private void writeMovieToDb(String title, LocalDate releasedAt, String priceCategory, int rating,
                               boolean rented) throws Exception {
        PreparedStatement writeStmt = connection.prepareStatement(MOVIE_SQL, Statement.RETURN_GENERATED_KEYS);
        writeStmt.setString(1, title);
        writeStmt.setBoolean(2, rented);
        writeStmt.setDate(3, Date.valueOf(releasedAt));
        writeStmt.setString(4, priceCategory);
        writeStmt.setInt(5, rating);
        writeStmt.execute();
        writeStmt.close();
    }

    /**
     * Read list of users from CSV file.
     * @param usersCsv the file containing the data.
     */
    private void readUsers(String usersCsv) {
        System.out.print("loading from /data/users.csv ");
        int count = 0;
        try (Reader in = new InputStreamReader(getClass().getResourceAsStream(usersCsv))) {
            Iterable<CSVRecord> users = CSVFormat.EXCEL.withFirstRecordAsHeader().withHeader(UserHeaders.class)
                    .withDelimiter(';').parse(in);
            for (CSVRecord u : users) {
                String surname = u.get(UserHeaders.Surname);
                String firstname = u.get(UserHeaders.FirstName);
                LocalDate birthdate = LocalDate.parse(u.get(UserHeaders.Birthdate));
                writeUserToDb(surname, firstname, birthdate);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("... done: " + count + " records loaded.");
    }

    /**
     * Insert single user record to table clients.
     * 
     * @param name of user
     * @param firstname of user
     * @param birthdate of user
     * @throws Exception on problems
     */
    private void writeUserToDb(String name, String firstname, LocalDate birthdate) throws Exception {
        PreparedStatement writeStmt = connection.prepareStatement(CLIENT_SQL);
        writeStmt.setString(1, firstname);
        writeStmt.setString(2, name);
        writeStmt.setDate(3, Date.valueOf(birthdate));
        writeStmt.execute();
        writeStmt.close();
    }

    /**
     * Read list of rentals from CSV file.
     * @param rentalsCsv the file containing the data.
     */
    private void readRentals(String rentalsCsv) {
        System.out.print("loading from /data/rentals.csv ");
        int count = 0;
        try (Reader in = new InputStreamReader(getClass().getResourceAsStream(rentalsCsv))) {
            Iterable<CSVRecord> rentals = CSVFormat.EXCEL.withFirstRecordAsHeader().withHeader(RentalHeaders.class)
                    .withDelimiter(';').parse(in);
            for (CSVRecord r : rentals) {
                LocalDate rentaldate = LocalDate.parse(r.get(RentalHeaders.RentalDate));
                int userId = Integer.parseInt(r.get(RentalHeaders.UserID));
                int movieId = Integer.parseInt(r.get(RentalHeaders.MovieID));
                writeRentalToDb(userId, movieId, rentaldate);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("... done: " + count + " records loaded.");
    }
    
    /**
     * Insert single rental record into rental table.
     * @param userId user
     * @param movieId movie
     * @param rentalDate date of rental
     * @throws Exception on problems
     */
    private void writeRentalToDb(int userId, int movieId, LocalDate rentalDate) throws Exception {
        PreparedStatement writeStmt = connection.prepareStatement(RENTAL_SQL);
        writeStmt.setInt(1, movieId);
        writeStmt.setInt(2, userId);
        writeStmt.setDate(3, Date.valueOf(rentalDate));
        writeStmt.execute();
        writeStmt.close();
    }

    enum MovieHeaders {
        ID, Title, ReleaseDate, PriceCategory, AgeRating, isRented
    }

    enum UserHeaders {
        ID, Surname, FirstName, Birthdate
    }

    enum RentalHeaders {
        ID, RentalDate, UserID, MovieID
    }

}
