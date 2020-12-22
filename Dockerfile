FROM postgres:alpine

# Initalise the database with the testdata
COPY dump.sql /docker-entrypoint-initdb.d

# Passing default values
ENV POSTGRES_USER="postgres"
ENV POSTGRES_PASSWORD="1234"
ENV POSTGRES_DB="mrs_test"
