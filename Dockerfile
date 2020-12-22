FROM postgres:alpine

# Initalise the database with the testdata
COPY target/mrs_datagenerator-0.0.1-SNAPSHOT.jar /opt/datagen.jar
COPY create_testdata.sh /docker-entrypoint-initdb.d

# Install java
RUN apk add openjdk11

# Passing default values for postgres and the data generator
ENV POSTGRES_USER="postgres"
ENV POSTGRES_PASSWORD="1234"
ENV POSTGRES_DB="mrs_test"

ENV MRS_USERS=10000
ENV MRS_MOVIES=80000
ENV MRS_RENTALS=5000
