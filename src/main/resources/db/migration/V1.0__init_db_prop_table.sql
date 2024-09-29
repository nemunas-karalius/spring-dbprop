 CREATE TABLE properties (
     id VARCHAR(255) NOT NULL PRIMARY KEY,
     val VARCHAR(2000) NULL
 );

 INSERT INTO properties (id, val) VALUES ('sample.someString', 'value from db');
 INSERT INTO properties (id, val) VALUES ('sample.connection.url', 'http://localhost/value/from/db');