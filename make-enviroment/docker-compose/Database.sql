
CREATE DATABASE input_data;

CREATE TABLE person(
   pid SERIAL  PRIMARY KEY    NOT NULL,
   name           CHAR(20)    NOT NULL,
   age            INT         NOT NULL,
   address        CHAR(50)
);

CREATE DATABASE output_data;

CREATE TABLE kafkaperson(
   pid SERIAL PRIMARY KEY     NOT NULL,
   name           CHAR(20)    NOT NULL,
   age            INT         NOT NULL,
   address        CHAR(50)
);

