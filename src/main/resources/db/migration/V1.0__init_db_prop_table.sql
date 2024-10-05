CREATE TABLE properties (
     id VARCHAR(255) NOT NULL PRIMARY KEY,
     val VARCHAR(2000) NULL
);

INSERT INTO properties (id, val) VALUES ('sample.someString', 'value from db');
INSERT INTO properties (id, val) VALUES ('sample.someInt', '777');
INSERT INTO properties (id, val) VALUES ('sample.arrayOfInt', '1, 2, 3, 5, 7, 9');
INSERT INTO properties (id, val) VALUES ('sample.connection.url', 'http://localhost/value/from/db');