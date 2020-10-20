Movie Rental System - Data Generator
====================================
 
a tool that populates the MRS database with random data.

The application 
- creates the tables in an empty database and fills the tables with data.
- does NOT delete existing data.
- is currently configured to run with PostgreSQL database.
  (An HSQLDB database provided is also implemented.)

## Building the application
Import the project as a Maven project into your preferred IDE and build; or build from command line with maven.
 
## How to use
The main class is `ch.fhnw.stqm.mrs.datagenerator.MrsDataGenerator`

1. Run the generator without arguments: It fills the database tables clients, movies, rentals with the amount of data specified in the class

2. Run the tool with arguments: MrsDataGenerator noOfUsers noOfMovies noOfRentals 

Generates "legal" data:
- users: random names using the [JavaFaker](https://www.baeldung.com/java-faker) library. Users age: [0, 90] years.
- movies: random string (no meaning, length [4-40] chars), release date in the last 100 years and an age rating [0, 19] and a legal PriceCategory.
- rentals: legal rental, i.e. a rental refers to an existing user and an existing movie in the database. Rentals are [0, 7] days old.

All random generated data are uniformly distributed over the indicated range.
The price categories have a ration of: new release 1%, children 10%, regular 89%

Alter code to change any of the generated data or distribution properties.


## Settings / Configuration
1. db_config.properties
 Contains the settings to access the database (url, username, password)

2. DBSetup.script
 Contains the sql script to create the database tables



 