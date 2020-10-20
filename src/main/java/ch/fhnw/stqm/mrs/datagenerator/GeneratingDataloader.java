package ch.fhnw.stqm.mrs.datagenerator;

import java.time.LocalDate;
import java.time.Period;
import java.util.Random;
import java.util.UUID;

import javax.sql.DataSource;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import com.github.javafaker.Faker;

public class GeneratingDataloader implements Dataloader {
    private static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final LocalDate TODAY = LocalDate.now();
    private static final int CURRENT_YEAR = TODAY.getYear();
    
    private static final String MOVIE_SQL = 
            "INSERT INTO movies (MovieId, Title, IsRented, ReleaseDate, PriceCategory, AgeRating)"
            + "  VALUES (:MovieId, :Title, :IsRented, :ReleaseDate, :PriceCategory, :AgeRating)";
    private static final String USER_SQL = "INSERT INTO users (UserId, FirstName, Name, Birthdate) "
    		+ "  VALUES ( :UserId, :FirstName, :Name, :Birthdate )";
    private static final String RENTAL_SQL = "INSERT INTO rentals (RentalId, MovieId, UserId, RentalDate )"
            + "  VALUES ( :RentalId, :MovieId, :UserId, :RentalDate )";
	private static final int NEW_RELEASE_PERCENTAGE = 1;
	private static final int CHILDREN_PERCENTAGE = 10;

    private DataSource ds;
    private Sql2o sql2o;
    private int maxUsers, maxMovies, maxRentals;
    
    private double rentedRatio;
    private int actualRentals;
    private Random rnd = new Random();
    private Faker faker = new Faker();

    /**
     * Generates random data for MRS database.
     * @param ds the data source to use for insertion into db.
     * @param nofusers that shall be generated.
     * @param nofmovies that shall be generated.
     * @param nofrentals that shall be generated.
     */
    public GeneratingDataloader(DataSource ds, int nofusers, int nofmovies, int nofrentals) {
        maxUsers = nofusers;
        maxMovies = nofmovies;
        maxRentals = nofrentals;
        rentedRatio = (double) maxRentals / (double) maxMovies;
        actualRentals = 0;
        this.ds = ds;
        sql2o = new Sql2o(this.ds);
    }
        
    
    @Override
    public void load() throws Exception {
        System.out.println("Generating data to load...");
        
        var users = generateUsers();
        var movies = generateMovies();
        generateRentals(movies, users);  
    }

    private Entry[] generateUsers() throws Exception {
    	System.out.println("Generating users...\n===================\n\n");
        try (Connection con = sql2o.beginTransaction()) {
        	Query q = con.createQuery(USER_SQL);
        	var result = new Entry[maxUsers];
            for (int i = 0; i < maxUsers; i++) {
            	User u = generateSingleUser();
                writeUserToDb(q, u);
                result[i] = new Entry(u.userid, Period.between(u.birthdate, TODAY).getYears());
                if (i % 100 == 0) {
                    System.out.println("" + i + " users");
                }
            }
            System.out.println("" + maxUsers + " users generated. Writing to DB..."); 
	        q.executeBatch();
	        con.commit();
            System.out.println("Generated users stored in DB"); 
            return result;
        }
    }

    private void writeUserToDb(Query q, User u) throws Exception {
    	q.addParameter("UserId", u.userid)
    	.addParameter("FirstName", u.firstname)
    	.addParameter("Name", u.name)
    	.addParameter("Birthdate", u.birthdate)
    	.addToBatch();
    }
    
    private Entry[] generateMovies() throws Exception {
    	System.out.println("Generating movies...\n====================\n\n");
        try (Connection con = sql2o.beginTransaction()) {
        	Query q = con.createQuery(MOVIE_SQL);
        	
        	var movies = new Entry[maxRentals]; 
	        for (int i = 0; i < maxMovies; i++) {
	        	Movie m = generateSingleMovie();
	            if (m.rented && actualRentals < maxRentals) {
	            	movies[actualRentals++] = new Entry(m.movieid, m.rating);
	            } else {
	            	m.rented = false;
	            }
	            writeMovieToDb(q, m);
	            if (i % 100 == 0) {
	                System.out.println("" + actualRentals + " rented movies out of "+ i + " total movies generated ");
	            }
	        }
	        System.out.println("" + maxMovies + " movies generated. Writing to DB...");
	        q.executeBatch();
	        con.commit();
	        System.out.println("Generated movies stored in DB.");
	        return movies;
        }
    }

    private void writeMovieToDb(Query q, Movie m) throws Exception {
    	q.addParameter("MovieId", m.movieid)
    	.addParameter("Title", m.title)
    	.addParameter("IsRented", m.rented)
    	.addParameter("ReleaseDate", m.releasedAt)
    	.addParameter("PriceCategory", m.priceCategory)
    	.addParameter("AgeRating", m.rating)
    	.addToBatch();
	}
    
    private void generateRentals(Entry[] movies, Entry[] users) throws Exception {
    	System.out.println("Generating rentals...\n=====================\n\n");
        try (Connection con = sql2o.beginTransaction()) {
        	Query q = con.createQuery(RENTAL_SQL);
        	int count = 0;
        	for(Entry m: movies) {
        		if (m != null) {
	        		int userIndex = rnd.nextInt(maxUsers);
	        		int age = users[userIndex].age;
	                while (age < m.age) {
	                    userIndex = (userIndex + 1) % maxUsers;
	                    age = users[userIndex].age;
	                }
	            	Rental r = new Rental();
	            	r.id = UUID.randomUUID();
	            	r.movieid = m.id;
	            	r.userid = users[userIndex].id;
	            	r.rentaldate = TODAY.minusDays(rnd.nextInt(8));
	                writeRentalToDb(q, r);
	                count++;
		            if (count % 100 == 0) {
		                System.out.println("" + count + " rentals generated ");
		            }
        		}
        	}
        	System.out.println("" + maxRentals + " rentals generated. Writing to DB...");
        	q.executeBatch();
	        con.commit();
        	System.out.println("Generated rentals stored in DB");
        }
    }

    private void writeRentalToDb(Query q, Rental r) throws Exception {
    	q.addParameter("RentalId", r.id)
    	.addParameter("MovieId", r.movieid)
    	.addParameter("UserId", r.userid)
    	.addParameter("RentalDate", r.rentaldate)
    	.addToBatch();
    }
    
    private String generateString(int length) {
        int len = randBetween(4, length);     
        char[] s = new char[len];
        for (int i = 0; i < len; i++) {
            s[i] = CHARS[rnd.nextInt(CHARS.length)];
        }
        return new String(s);
    }

    private LocalDate generateDate(int maxAge) {
        int year = randBetween(CURRENT_YEAR - maxAge, CURRENT_YEAR);
        int dayOfYear = randBetween(1, 365);

        return LocalDate.ofYearDay(year, dayOfYear);
    }
    
    private static int randBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }
    
    private User generateSingleUser() {
    	User u = new User();
    	u.firstname = faker.name().firstName();
        u.name = faker.name().lastName();
        u.birthdate = generateDate(90);
        u.userid = UUID.randomUUID();
        return u;
    }
    
    private Movie generateSingleMovie() {
    	Movie m = new Movie();   	
        m.title = generateString(40);
        m.releasedAt = generateDate(100);
        m.rating = rnd.nextInt(19);
        int type = rnd.nextInt(100);
        if (type <= NEW_RELEASE_PERCENTAGE) {
        	m.priceCategory = "New Release";
        } else if (type <= NEW_RELEASE_PERCENTAGE + CHILDREN_PERCENTAGE) {
        	m.priceCategory = "Children";
        	m.rating = rnd.nextInt(13);
        } else {
            m.priceCategory = "Regular";
        }
        m.rented = rnd.nextDouble() < rentedRatio;
        m.movieid = UUID.randomUUID();    	
    	return m;
    }
    
    private static class User {
    	UUID userid;
    	String firstname;
    	String name;
    	LocalDate birthdate;
    }
    
    private static class Movie {
    	UUID movieid;
    	String title;
    	boolean rented;
    	LocalDate releasedAt;
    	String priceCategory;
    	int rating;
    }
    
    private static class Rental {
    	UUID id;
    	UUID movieid;
    	UUID userid;
    	LocalDate rentaldate;
    }
    
    private static class Entry {
    	UUID id;
    	int age;
    	Entry(UUID id, int age) {
    		this.id = id; this.age = age;
    	}
    }
}
