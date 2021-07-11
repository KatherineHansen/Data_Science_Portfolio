CREATE DATABASE IF NOT EXISTS gym;

USE gym;
CREATE TABLE IF NOT EXISTS login (username VARCHAR(30), password VARCHAR(30), role VARCHAR(30));

CREATE TABLE IF NOT EXISTS instructor (ID INT(5), name VARCHAR(30), PRIMARY KEY (ID));

CREATE TABLE IF NOT EXISTS customer (ID INT(5), name VARCHAR(30), PRIMARY KEY (ID));

CREATE TABLE IF NOT EXISTS trains (i_ID INT(5), c_ID INT(5), startTime DECIMAL(4,2), endTime DECIMAL(4,2), weekDate VARCHAR(10), FOREIGN KEY (i_ID) REFERENCES instructor(ID), FOREIGN KEY (c_ID) REFERENCES customer(ID));

INSERT INTO gym.login(username, password, role) VALUES("frontDesk", "frontDesk_pw", "frontDesk");

INSERT INTO gym.customer(ID, name) VALUES(1, "Ben");
INSERT INTO gym.customer(ID, name) VALUES(2, "Garret");
INSERT INTO gym.customer(ID, name) VALUES(3, "Isaac");
INSERT INTO gym.customer(ID, name) VALUES(4, "Kate");
INSERT INTO gym.customer(ID, name) VALUES(5, "Josh");

INSERT INTO gym.instructor(ID, name) VALUES(1, "John");
INSERT INTO gym.instructor(ID, name) VALUES(2, "Dennis");
INSERT INTO gym.instructor(ID, name) VALUES(3, "Mark");
INSERT INTO gym.instructor(ID, name) VALUES(4, "Linus");
INSERT INTO gym.instructor(ID, name) VALUES(5, "Anthony");

INSERT INTO gym.trains(i_ID, c_ID, startTime, endTime, weekDate) VALUES(1, 1, 12, 14, 'Monday');
INSERT INTO gym.trains(i_ID, c_ID, startTime, endTime, weekDate) VALUES(2, 2, 13, 15, 'Tuesday');
INSERT INTO gym.trains(i_ID, c_ID, startTime, endTime, weekDate) VALUES(3, 3, 14, 16, 'Wednesday');
INSERT INTO gym.trains(i_ID, c_ID, startTime, endTime, weekDate) VALUES(4, 4, 15, 17, 'Thursday');
INSERT INTO gym.trains(i_ID, c_ID, startTime, endTime, weekDate) VALUES(5, 5, 16, 18, 'Friday');
