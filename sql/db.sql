-- KEEP ON START OF FILE
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS overtime;
DROP TABLE IF EXISTS newsfeed;
DROP TABLE IF EXISTS employee_shift;
DROP TABLE IF EXISTS availability;
DROP TABLE IF EXISTS shift;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS department;

-- General user
CREATE TABLE user(
  user_id INTEGER NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(30),
  last_name VARCHAR(30),
  hash VARCHAR(50),
  salt VARCHAR(50),
  email VARCHAR(40) NOT NULL,
  phonenumber VARCHAR(8),
  category INTEGER,
  percentage_work FLOAT,
  dept_id INTEGER,
  CONSTRAINT pk_user PRIMARY KEY(user_id)
);

-- Make login identifiers unique
ALTER TABLE user ADD UNIQUE INDEX (email);
ALTER TABLE user ADD UNIQUE INDEX (phonenumber);

-- Department where a shift is scheduled
CREATE TABLE department(
  dept_id INTEGER NOT NULL AUTO_INCREMENT,
  dept_name VARCHAR(30),
  CONSTRAINT pk_dept PRIMARY KEY(dept_id)
);

-- Shift, with number of employees working, date, time of day and department
CREATE TABLE shift(
  shift_id INTEGER NOT NULL AUTO_INCREMENT,
  staff_number INTEGER,
  date DATE,
  time TINYINT,
  approved BOOLEAN,
  dept_id INTEGER,
  CONSTRAINT pk_shift PRIMARY KEY(shift_id)
);

-- If valid_absence is true, the employee is absent this shift, but should still be payed.
CREATE TABLE employee_shift(
  user_id INTEGER,
  shift_id INTEGER,
  responsibility BOOLEAN,
  valid_absence BOOLEAN,
  shift_change BOOLEAN,
  removed BOOLEAN DEFAULT false,
  CONSTRAINT pk_employee_shift PRIMARY KEY(user_id, shift_id)
);

-- If a worker is available to work on a specific shift
CREATE TABLE availability(
  user_id INTEGER,
  shift_id INTEGER,
  CONSTRAINT pk_availability PRIMARY KEY(user_id, shift_id)
);

-- Used to register minutes worked over the scheduled work hours
CREATE TABLE overtime(
  user_id INTEGER,
  shift_id INTEGER,
  start_time INTEGER,
  minutes INTEGER,
  approved BOOLEAN,
  CONSTRAINT pk_overtime PRIMARY KEY(user_id, shift_id, start_time)
);

-- newsfeed has two user_ids, first one is the one the newsfeed "belongs" to
-- second one is, together with shift_id, a reference to the employee_shift
CREATE TABLE newsfeed (
  feed_id INTEGER AUTO_INCREMENT,
  date_time DATETIME,
  content VARCHAR(200),
  resolved BOOLEAN,
  category INTEGER,
  user_id INTEGER,
  shift_id INTEGER,
  shift_user_id INTEGER,
  start_time INTEGER,
  CONSTRAINT pk_newsfeed PRIMARY KEY(feed_id)
);

ALTER TABLE shift
  ADD CONSTRAINT fk_shift FOREIGN KEY(dept_id)
  REFERENCES department(dept_id);

ALTER TABLE user
  ADD CONSTRAINT fk_user FOREIGN KEY(dept_id)
  REFERENCES department(dept_id);


ALTER TABLE employee_shift
  ADD CONSTRAINT fk1_employee_shift FOREIGN KEY(user_id)
  REFERENCES user(user_id),
  ADD CONSTRAINT fk2_employee_shift FOREIGN KEY(shift_id)
  REFERENCES shift(shift_id);

ALTER TABLE availability
  ADD CONSTRAINT fk1_availability FOREIGN KEY(user_id)
  REFERENCES user(user_id),
  ADD CONSTRAINT fk2_availability FOREIGN KEY(shift_id)
  REFERENCES shift(shift_id);

ALTER TABLE overtime
  ADD CONSTRAINT fk1_overtime FOREIGN KEY(user_id, shift_id)
  REFERENCES employee_shift(user_id, shift_id);


ALTER TABLE newsfeed
  ADD CONSTRAINT fk1_newsfeed FOREIGN KEY(user_id)
  REFERENCES user(user_id),
  ADD CONSTRAINT fk2_newsfeed FOREIGN KEY(shift_user_id,shift_id)
  REFERENCES employee_shift(user_id, shift_id);

-- DEPARTMENTS
INSERT INTO department VALUES(1, 'Avdeling 1');
INSERT INTO department VALUES(2, 'Avdeling 2');

-- USERS

-- category: 0 - Admin, 1 - Assistent, 2 - Helsefagarbeider, 3 - Sykepleier
INSERT INTO user VALUES(DEFAULT, 'Siri', 'Andresen', 'oQaZgG266KjDzEkGTgXYMQ==','2oUGF8AAgobU1E3rcAtyiw==', 'email1', 'phone1',1, 0.8,1);
INSERT INTO user VALUES(DEFAULT, 'Geir', 'Geirsen', 'password','password','email2', 'phone2', 1, 0.8,1);
INSERT INTO user VALUES(DEFAULT, 'Stine', 'Pettersen', 'password','password', 'email3', 'phone3', 1, 1,2);
INSERT INTO user VALUES(DEFAULT, 'Maria', 'Christensen', 'password', 'password', 'email4', 'phone4', 1, 1,1);
INSERT INTO user VALUES(DEFAULT, 'Fridtjof', 'Karlsen', 'password', 'password', 'email5', 'phone5', 1, 0.8,2);

INSERT INTO user VALUES(DEFAULT, 'Kari', 'Karlsen', 'password', 'password', 'email6', 'phone6',2, 0.2,1);
INSERT INTO user VALUES(DEFAULT, 'Narve', 'Berntsen', 'password', 'password', 'email7', 'phone7',2, 0.5,2);
INSERT INTO user VALUES(DEFAULT, 'Finn', 'Fransen', 'passord', 'password', 'email8', 'phone8',2, 0.9,1);
INSERT INTO user VALUES(DEFAULT, 'Per', 'Persen', 'password', 'password', 'email9', 'phone9',2, 0.75,2);
INSERT INTO user VALUES(DEFAULT, 'Mari', 'Nilsen', 'password', 'password', 'email10','phone10',2, 1,1);

INSERT INTO user VALUES(DEFAULT, 'Hanne', 'Holm', 'password', 'password', 'email11', 'phone11',2, 1,2);
INSERT INTO user VALUES(DEFAULT, 'Gunnar', 'Persen', 'password', 'password', 'email12', 'phone12',2, 1,1);
INSERT INTO user VALUES(DEFAULT, 'Harry', 'Olsen', 'password', 'password', 'email13','phone13',2, 0.6,2);
INSERT INTO user VALUES(DEFAULT, 'Tom', 'Jensen', 'password', 'password', 'email14', 'phone14',2, 0.1,1);
INSERT INTO user VALUES(DEFAULT, 'Bente', 'Svendsen', 'password', 'password', 'email15', 'phone15', 2, 0.5,2);

INSERT INTO user VALUES(DEFAULT, 'Anna', 'Kristiansen', 'password', 'password', 'email16', 'phone16',2, 1,1);
INSERT INTO user VALUES(DEFAULT, 'Tonje', 'Tønne', 'password', 'password', 'email17', 'phone17',3, 1,2);
INSERT INTO user VALUES(DEFAULT, 'Stig', 'Smith', 'password', 'password', 'email18', 'phone18',3, 1,1);
INSERT INTO user VALUES(DEFAULT, 'Silje', 'Stigsen', 'password', 'password', 'email19', 'phone19',3, 0.65,2);
INSERT INTO user VALUES(DEFAULT, 'Greg', 'Hansen', 'password', 'password', 'email20', 'phone20',3, 0.8,1);

INSERT INTO user VALUES(DEFAULT, 'Helge', 'Helgesen', 'password','password', 'email21', 'phone21',3, 0.5,2);
INSERT INTO user VALUES(DEFAULT, 'Bjørg', 'Solvang', 'password', 'password', 'email22', 'phone22',3, 0.80,1);
INSERT INTO user VALUES(DEFAULT, 'Vincent', 'Hagen', 'password', 'password', 'email23', 'phone23', 3, 0.6,2);
INSERT INTO user VALUES(DEFAULT, 'Erik', 'Jørgensen', 'password', 'password', 'email24', 'phone24', 3, 0.15,1);
INSERT INTO user VALUES(DEFAULT, 'Heidi', 'Helmersen', 'password', 'password', 'email25', 'phone25', 3, 1,2);

INSERT INTO user VALUES(DEFAULT, 'Jens', 'Jensen', 'oQaZgG266KjDzEkGTgXYMQ==', '2oUGF8AAgobU1E3rcAtyiw==', 'admin', 'admin', 0, 1,1);

-- SCHEDULED SHIFTS
-- shift(shift_id, staff_number, date, time, approved, dept_id)
-- WEEK 1
INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-23', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-23', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-23', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-24', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-24', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-24', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-25', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-25', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-25', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-26', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-26', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-26', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-27', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-27', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-27', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-28', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-28', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-28', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-29', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-29', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-29', 2, true, 1);

-- WEEK 2
INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-30', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-30', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-30', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-31', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-31', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-31', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-01', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-01', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-01', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-02', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-02', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-02', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-03', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-03', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-03', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-04', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-04', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-04', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-05', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-05', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-05', 2, true, 1);

-- WEEK 3
INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-06', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-06', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-06', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-07', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-07', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-07', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-08', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-08', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-08', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-09', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-09', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-09', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-10', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-10', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-10', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-11', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-11', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-11', 2, true, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-12', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-12', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-12', 2, true, 1);


-- WEEK 4 : Day 1 (Shifts with no employees)
INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-13', 0, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-13', 1, true, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-13', 2, true, 1);


-- EMPLOYEE_SHIFT : employee_shift(user_id, shift_id, responsibility, valid_absence, shift_change, removed)

-- WEEK 1: DAY 1
INSERT INTO employee_shift VALUES(1, 1, false, false, false,false);
INSERT INTO employee_shift VALUES(6, 1, false, false, false,false);
INSERT INTO employee_shift VALUES(12, 1, false, false, false,false);
INSERT INTO employee_shift VALUES(17, 1, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(2, 2, false, false, false,false);
INSERT INTO employee_shift VALUES(10, 2, false, false, false,false);
INSERT INTO employee_shift VALUES(20, 2, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(3, 3, false, false, false,false);
INSERT INTO employee_shift VALUES(13, 3, false, false, false,false);
INSERT INTO employee_shift VALUES(21, 3, true, false, false,false);

-- WEEK 1 : DAY 2
INSERT INTO employee_shift VALUES(1, 4, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 4, false, false, false,false);
INSERT INTO employee_shift VALUES(12, 4, false, false, false,false);
INSERT INTO employee_shift VALUES(19, 4, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(2, 5, false, false, false,false);
INSERT INTO employee_shift VALUES(8, 5, false, false, false,false);
INSERT INTO employee_shift VALUES(25, 5, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(16, 6, false, false, false,false);
INSERT INTO employee_shift VALUES(22, 6, false, false, false,false);
INSERT INTO employee_shift VALUES(23, 6, true, false, false,false);

-- WEEK 1 : DAY 3
INSERT INTO employee_shift VALUES(4, 7, false, false, false,false);
INSERT INTO employee_shift VALUES(8, 7, false, false, false,false);
INSERT INTO employee_shift VALUES(17, 7, false, false, false,false);
INSERT INTO employee_shift VALUES(18, 7, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(1, 8, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 8, false, false, false,false);
INSERT INTO employee_shift VALUES(21, 8, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(5, 9, false, false, false,false);
INSERT INTO employee_shift VALUES(9, 9, false, false, false,false);
INSERT INTO employee_shift VALUES(22, 9, true, false, false,false);

-- WEEK 1 : DAY 4
INSERT INTO employee_shift VALUES(3, 10, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 10, false, false, false,false);
INSERT INTO employee_shift VALUES(21, 10, false, false, false,false);
INSERT INTO employee_shift VALUES(25, 10, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(2, 11, false, false, false,false);
INSERT INTO employee_shift VALUES(13, 11, false, false, false,false);
INSERT INTO employee_shift VALUES(18, 11, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(5, 12, false, false, false,false);
INSERT INTO employee_shift VALUES(9, 12, false, false, false,false);
INSERT INTO employee_shift VALUES(21, 12, true, false, false,false);

-- WEEK 1 : DAY 5
INSERT INTO employee_shift VALUES(4, 13, false, false, false,false);
INSERT INTO employee_shift VALUES(10, 13, false, false, false,false);
INSERT INTO employee_shift VALUES(17, 13, false, false, false,false);
INSERT INTO employee_shift VALUES(19, 13, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(1, 14, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 14, false, false, false,false);
INSERT INTO employee_shift VALUES(19, 14, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(5, 15, false, false, false,false);
INSERT INTO employee_shift VALUES(12, 15, false, false, false,false);
INSERT INTO employee_shift VALUES(22, 15, true, false, false,false);

-- WEEK 1 : DAY 6
INSERT INTO employee_shift VALUES(1, 16, false, false, false,false);
INSERT INTO employee_shift VALUES(6, 16, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 16, false, false, false,false);
INSERT INTO employee_shift VALUES(17, 16, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(2, 17, false, false, false,false);
INSERT INTO employee_shift VALUES(7, 17, false, false, false,false);
INSERT INTO employee_shift VALUES(20, 17, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(5, 18, false, false, false,false);
INSERT INTO employee_shift VALUES(13, 18, false, false, false,false);
INSERT INTO employee_shift VALUES(21, 18, true, false, false,false);

-- WEEK 1 : DAY 7
INSERT INTO employee_shift VALUES(4, 19, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 19, false, false, false,false);
INSERT INTO employee_shift VALUES(12, 19, false, false, false,false);
INSERT INTO employee_shift VALUES(23, 19, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(5, 20, false, false, false,false);
INSERT INTO employee_shift VALUES(8, 20, false, false, false,false);
INSERT INTO employee_shift VALUES(17, 20, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(3, 21, false, false, false,false);
INSERT INTO employee_shift VALUES(14, 21, false, false, false,false);
INSERT INTO employee_shift VALUES(24, 21, true, false, false,false);



-- WEEK 2: DAY 1
INSERT INTO employee_shift VALUES(1, 22, false, false, false,false);
INSERT INTO employee_shift VALUES(6, 22, false, false, false,false);
INSERT INTO employee_shift VALUES(12, 22, false, false, false,false);
INSERT INTO employee_shift VALUES(17, 22, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(2, 23, false, false, false,false);
INSERT INTO employee_shift VALUES(10, 23, false, false, false,false);
INSERT INTO employee_shift VALUES(20, 23, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(3, 24, false, false, false,false);
INSERT INTO employee_shift VALUES(13, 24, false, false, false,false);
INSERT INTO employee_shift VALUES(21, 24, true, false, false,false);

-- WEEK 2 : DAY 2
INSERT INTO employee_shift VALUES(1, 25, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 25, false, false, false,false);
INSERT INTO employee_shift VALUES(12, 25, false, false, false,false);
INSERT INTO employee_shift VALUES(19, 25, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(2, 26, false, false, false,false);
INSERT INTO employee_shift VALUES(8, 26, false, false, false,false);
INSERT INTO employee_shift VALUES(25, 26, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(16, 27, false, false, false,false);
INSERT INTO employee_shift VALUES(22, 27, false, false, false,false);
INSERT INTO employee_shift VALUES(23, 27, true, false, false,false);

-- WEEK 2 : DAY 3
INSERT INTO employee_shift VALUES(4, 28, false, false, false,false);
INSERT INTO employee_shift VALUES(8, 28, false, false, false,false);
INSERT INTO employee_shift VALUES(17, 28, false, false, false,false);
INSERT INTO employee_shift VALUES(18, 28, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(1, 29, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 29, false, false, false,false);
INSERT INTO employee_shift VALUES(21, 29, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(5, 30, false, false, false,false);
INSERT INTO employee_shift VALUES(9, 30, false, false, false,false);
INSERT INTO employee_shift VALUES(22, 30, true, false, false,false);

-- WEEK 2 : DAY 4
INSERT INTO employee_shift VALUES(3, 31, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 31, false, false, false,false);
INSERT INTO employee_shift VALUES(21, 31, false, false, false,false);
INSERT INTO employee_shift VALUES(25, 31, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(2, 32, false, false, false,false);
INSERT INTO employee_shift VALUES(13, 32, false, false, false,false);
INSERT INTO employee_shift VALUES(18, 32, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(5, 33, false, false, false,false);
INSERT INTO employee_shift VALUES(9, 33, false, false, false,false);
INSERT INTO employee_shift VALUES(21, 33, true, false, false,false);

-- WEEK 2 : DAY 5
INSERT INTO employee_shift VALUES(4, 34, false, false, false,false);
INSERT INTO employee_shift VALUES(10, 34, false, false, false,false);
INSERT INTO employee_shift VALUES(17, 34, false, false, false,false);
INSERT INTO employee_shift VALUES(19, 34, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(1, 35, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 35, false, false, false,false);
INSERT INTO employee_shift VALUES(19, 35, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(5, 36, false, false, false,false);
INSERT INTO employee_shift VALUES(12, 36, false, false, false,false);
INSERT INTO employee_shift VALUES(22, 36, true, false, false,false);

-- WEEK 2 : DAY 6
INSERT INTO employee_shift VALUES(1, 37, false, false, false,false);
INSERT INTO employee_shift VALUES(6, 37, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 37, false, false, false,false);
INSERT INTO employee_shift VALUES(17, 37, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(2, 38, false, false, false,false);
INSERT INTO employee_shift VALUES(7, 38, false, false, false,false);
INSERT INTO employee_shift VALUES(20, 38, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(5, 39, false, false, false,false);
INSERT INTO employee_shift VALUES(13, 39, false, false, false,false);
INSERT INTO employee_shift VALUES(21, 39, true, false, false,false);

-- WEEK 2 : DAY 7
INSERT INTO employee_shift VALUES(4, 40, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 40, false, false, false,false);
INSERT INTO employee_shift VALUES(12, 40, false, false, false,false);
INSERT INTO employee_shift VALUES(23, 40, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(5, 41, false, false, TRUE ,false);
INSERT INTO employee_shift VALUES(8, 41, false, false, false,false);
INSERT INTO employee_shift VALUES(17, 41, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(3, 42, false, false, false,false);
INSERT INTO employee_shift VALUES(14, 42, false, false, false,false);
INSERT INTO employee_shift VALUES(24, 42, true, false, false,false);



-- WEEK 3: DAY 1
INSERT INTO employee_shift VALUES(1, 43, false, false, false,false);
INSERT INTO employee_shift VALUES(6, 43, false, false, false,false);
INSERT INTO employee_shift VALUES(12, 43, false, false, false,false);
INSERT INTO employee_shift VALUES(17, 43, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(2, 44, false, false, false,false);
INSERT INTO employee_shift VALUES(10, 44, false, false, false,false);
INSERT INTO employee_shift VALUES(20, 44, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(3, 45, false, false, false,false);
INSERT INTO employee_shift VALUES(13, 45, false, false, false,false);
INSERT INTO employee_shift VALUES(21, 45, true, false, false,false);

-- WEEK 3 : DAY 2
INSERT INTO employee_shift VALUES(1, 46, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 46, false, false, false,false);
INSERT INTO employee_shift VALUES(12, 46, false, false, false,false);
INSERT INTO employee_shift VALUES(19, 46, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(2, 47, false, false, false,false);
INSERT INTO employee_shift VALUES(8, 47, false, false, false,false);
INSERT INTO employee_shift VALUES(25, 47, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(16, 48, false, false, false,false);
INSERT INTO employee_shift VALUES(22, 48, false, false, false,false);
INSERT INTO employee_shift VALUES(23, 48, true, false, false,false);

-- WEEK 3 : DAY 3
INSERT INTO employee_shift VALUES(4, 49, false, false, false,false);
INSERT INTO employee_shift VALUES(8, 49, false, false, false,false);
INSERT INTO employee_shift VALUES(17, 49, false, false, false,false);
INSERT INTO employee_shift VALUES(18, 49, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(1, 50, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 50, false, false, false,false);
INSERT INTO employee_shift VALUES(21, 50, true, false, false,false);
-- nigh
INSERT INTO employee_shift VALUES(5, 51, false, false, false,false);
INSERT INTO employee_shift VALUES(9, 51, false, false, false,false);
INSERT INTO employee_shift VALUES(22, 51, true, false, false,false);

-- WEEK 3 : DAY 4
INSERT INTO employee_shift VALUES(3, 52, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 52, false, false, false,false);
INSERT INTO employee_shift VALUES(21, 52, false, false, false,false);
INSERT INTO employee_shift VALUES(25, 52, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(2, 53, false, false, false,false);
INSERT INTO employee_shift VALUES(13, 53, false, false, false,false);
INSERT INTO employee_shift VALUES(18, 53, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(5, 54, false, false, false,false);
INSERT INTO employee_shift VALUES(9, 54, false, false, false,false);
INSERT INTO employee_shift VALUES(21, 54, true, false, false,false);

-- WEEK 3 : DAY 5
INSERT INTO employee_shift VALUES(4, 55, false, false, false,false);
INSERT INTO employee_shift VALUES(10, 55, false, false, false,false);
INSERT INTO employee_shift VALUES(17, 55, false, false, false,false);
INSERT INTO employee_shift VALUES(19, 55, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(1, 56, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 56, false, false, false,false);
INSERT INTO employee_shift VALUES(19, 56, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(5, 57, false, false, false,false);
INSERT INTO employee_shift VALUES(12, 57, false, false, false,false);
INSERT INTO employee_shift VALUES(22, 57, true, false, false,false);

-- WEEK 3 : DAY 6
INSERT INTO employee_shift VALUES(1, 58, false, false, false,false);
INSERT INTO employee_shift VALUES(6, 58, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 58, false, false, false,false);
INSERT INTO employee_shift VALUES(17, 58, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(2, 59, false, false, false,false);
INSERT INTO employee_shift VALUES(7, 59, false, false, false,false);
INSERT INTO employee_shift VALUES(20, 59, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(5, 60, false, false, false,false);
INSERT INTO employee_shift VALUES(13, 60, false, false, false,false);
INSERT INTO employee_shift VALUES(21, 60, true, false, false,false);

-- WEEK 3 : DAY 7
INSERT INTO employee_shift VALUES(4, 61, false, false, false,false);
INSERT INTO employee_shift VALUES(11, 61, false, false, false,false);
INSERT INTO employee_shift VALUES(12, 61, false, TRUE, false,false);
INSERT INTO employee_shift VALUES(23, 61, true, false, false,false);
-- evening
INSERT INTO employee_shift VALUES(5, 62, false, false, false, false);
INSERT INTO employee_shift VALUES(8, 62, false, TRUE, false,false);
INSERT INTO employee_shift VALUES(17, 62, true, false, false,false);
-- night
INSERT INTO employee_shift VALUES(3, 63, false, false, TRUE, false);
INSERT INTO employee_shift VALUES(14, 63, false, false, false,false);
INSERT INTO employee_shift VALUES(24, 63, true, false, false,false);

-- AVAILABILITY
-- (user_id, shift_id)
INSERT INTO availability VALUES(1, 41);
INSERT INTO availability VALUES(2, 41);
INSERT INTO availability VALUES(10, 41);
INSERT INTO availability VALUES(15, 41);
INSERT INTO availability VALUES(17, 42);
INSERT INTO availability VALUES(7, 42);
INSERT INTO availability VALUES(3, 43);
INSERT INTO availability VALUES(2, 43);
INSERT INTO availability VALUES(4, 43);
INSERT INTO availability VALUES(5, 43);
INSERT INTO availability VALUES(7, 43);
INSERT INTO availability VALUES(11, 43);
INSERT INTO availability VALUES(13, 43);

-- shift: 43, date: 2017-02-06, users: 2, 3, 4, 5, 7, 11, 13

-- OVERTIME
INSERT INTO overtime VALUES(1, 16, 960,  60, TRUE);
INSERT INTO overtime VALUES(1, 37, 960, -80, FALSE);
INSERT INTO overtime VALUES(3, 52, 960,  35, TRUE);
INSERT INTO overtime VALUES(4, 28, 780, -120, TRUE );
INSERT INTO overtime VALUES(4, 61, 840, -60, FALSE);
INSERT INTO overtime VALUES(5, 60, 960,  60, FALSE);

-- NEWSFEED

-- (0) SHIFT_CHANGE_EMPLOYEE
-- newsfeed(feed_id, date_time, content, resolved, category, user_id, shift_id, shift_user_id, start_time)
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 16:02:36','Ledig kveldsvakt', false, 0,    1,    41,    1,   -1);
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 17:02:36', 'Ledig kveldsvakt', false, 0,    1,    41,    2,   -1);
-- "Ledig (dag/natt/kvelds)vakt"

-- (1) SHIFT_CHANGE_ADMIN
-- newsfeed(feed_id, date_time, content, resolved, category, user_id, shift_id, shift_user_id, start_time)
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 16:02:36', 'Siri Andresen ønsker å bytte vakt', false, 1,  26,  44,  1,   -1);
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 17:02:36', 'Mari Nilsen ønsker å bytte vakt', false, 1,  26,  22, 10,   -1);
-- "... ønsker å bytte vakt"


-- (2) VALID_ABSENCE
-- newsfeed(feed_id, date_time, content, resolved, category, user_id, shift_id, shift_user_id, start_time)
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 16:02:36', 'Gunnar Persen har meldt fravær', false, 2,    26,    61,    12,   -1);
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 17:02:36', 'Finn Fransen har meldt fravær', false, 2,    26,    61,    8,   -1);
-- "... har meldt fravær"


-- (3) TIMEBANK
-- newsfeed(feed_id, date_time, content, resolved, category, user_id, shift_id, shift_user_id, start_time)
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 16:02:36', 'Siri Andresen har registrert timeavvik', false,  3,  26,    37,    1,   960);
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 14:02:36', 'Maria Christensen har registrert timeavvik', false,  3,  26,    61,    4,   840);
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 15:02:36', 'Fridtjof Karlsen har registrert timeavvik', false,  3,  26,    60,    5,   960);
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 16:02:36', 'Overtid registrert: (1t 0min)', false,  4,  1,    16,    1,   960);
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 14:02:36', 'Overtid registrert: (35 min)',  false,  4,  3,    52,    3,   960);
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 15:02:36', 'Fravær registrert: (2t 0min)', false,  4,  4,    28,    4,   780);
-- "Overtid/fravær registrert (xxt, xxmin)"
-- "... har registert timeavvik (xxt, xxmin)"

-- (4) NOTIFICATION
-- newsfeed(feed_id, date_time, content, resolved, category, user_id, shift_id, shift_user_id, start_time)
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 16:02:36', 'Du har fått en beskjed', false,  4,  1,    NULL,    NULL,   -1);
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 17:02:36', 'Du har fått en beskjed', false,  4,  1,    NULL,    NULL,   -1);
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 14:02:36', 'Du har fått en beskjed', false,  4,  1,    NULL,    NULL,   -1);
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 15:02:36', 'Du har fått en beskjed', false,  4,  2,    NULL,    NULL,   -1);
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 16:02:36', 'Du har fått en beskjed', false,  4,  2,    NULL,    NULL,   -1);
INSERT INTO newsfeed VALUES (DEFAULT, '2017-01-23 17:02:36', 'Du har fått en beskjed', false,  4,  2,    NULL,    NULL,   -1);


-- KEEP ON END OF FILE
SET FOREIGN_KEY_CHECKS = 1;