DROP TABLE IF EXISTS overtime;
DROP TABLE IF EXISTS employee_shift;
DROP TABLE IF EXISTS availability;
DROP TABLE IF EXISTS shift;
DROP TABLE IF EXISTS admin;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS department;

-- General user
-- (user_id, first_name, last_name, hash, salt, email, phonenumber)
CREATE TABLE user(
  user_id INTEGER NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(30),
  last_name VARCHAR(30),
  hash VARCHAR(50),
  salt VARCHAR(50),
  email VARCHAR(40) NOT NULL,
  phonenumber VARCHAR(8),
  category INTEGER,
  CONSTRAINT pk_user PRIMARY KEY(user_id)
);

-- Make login identifiers unique
ALTER TABLE user ADD UNIQUE INDEX (email);
ALTER TABLE user ADD UNIQUE INDEX (phonenumber);

-- Administration user. (rights: needed?)
CREATE TABLE admin(
user_id INTEGER NOT NULL,
rights BOOLEAN,
CONSTRAINT pk_admin PRIMARY KEY(user_id)
);

-- Employee user
-- (user_id, percentage_work)
CREATE TABLE employee(
user_id INTEGER NOT NULL,
percentage_work FLOAT,
CONSTRAINT pk_employee PRIMARY KEY(user_id)
);

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
CONSTRAINT pk_employee_shift PRIMARY KEY(user_id, shift_id)
);

-- If a worker is available to work on a spesific shift
CREATE TABLE availability(
user_id INTEGER,
shift_id INTEGER,
CONSTRAINT pk_availability PRIMARY KEY(user_id, shift_id)
);

-- Used to register hours worked over the scheduled work hours
CREATE TABLE overtime(
user_id INTEGER NOT NULL,
date DATE,
start_time INTEGER,
end_time INTEGER,
CONSTRAINT pk_overtime PRIMARY KEY(user_id, date, start_time)
);

ALTER TABLE admin
ADD CONSTRAINT fk_admin FOREIGN KEY(user_id)
REFERENCES user(user_id);

ALTER TABLE employee
ADD CONSTRAINT fk_employee FOREIGN KEY(user_id)
REFERENCES user(user_id);

ALTER TABLE shift
ADD CONSTRAINT fk_shift FOREIGN KEY(dept_id)
REFERENCES department(dept_id);

ALTER TABLE employee_shift
ADD CONSTRAINT fk1_employee_shift FOREIGN KEY(user_id)
REFERENCES employee(user_id),
ADD CONSTRAINT fk2_employee_shift FOREIGN KEY(shift_id)
REFERENCES shift(shift_id);

ALTER TABLE availability
ADD CONSTRAINT fk1_availability FOREIGN KEY(user_id)
REFERENCES employee(user_id),
ADD CONSTRAINT fk2_availability FOREIGN KEY(shift_id)
REFERENCES shift(shift_id);

ALTER TABLE overtime
ADD CONSTRAINT fk_overtime FOREIGN KEY(user_id)
REFERENCES employee(user_id);

-- DEPARTMENTS
INSERT INTO department VALUES(1, 'Avdeling 1');
INSERT INTO department VALUES(2, 'Avdeling 2');

-- USERS

-- category: 0 - Admin, 1 - Assistent, 2 - Helsefagarbeider, 3 - Sykepleier
-- (user_id, first_name, last_name, hash, salt, email, phonenumber, category)
INSERT INTO user VALUES(DEFAULT, 'Siri', 'Andresen', 'oQaZgG266KjDzEkGTgXYMQ==','2oUGF8AAgobU1E3rcAtyiw==', 'email1', 'phone1',1);
INSERT INTO user VALUES(DEFAULT, 'Geir', 'Geirsen', 'password','password','email2', 'phone2', 1);
INSERT INTO user VALUES(DEFAULT, 'Stine', 'Pettersen', 'password','password', 'email3', 'phone3', 1);
INSERT INTO user VALUES(DEFAULT, 'Maria', 'Christensen', 'password', 'password', 'email4', 'phone4', 1);
INSERT INTO user VALUES(DEFAULT, 'Fridtjof', 'Karlsen', 'password', 'password', 'email5', 'phone5', 1);

INSERT INTO user VALUES(DEFAULT, 'Kari', 'Karlsen', 'password', 'password', 'email6', 'phone6',2);
INSERT INTO user VALUES(DEFAULT, 'Narve', 'Berntsen', 'password', 'password', 'email7', 'phone7',2);
INSERT INTO user VALUES(DEFAULT, 'Finn', 'Fransen', 'passord', 'password', 'email8', 'phone8',2);
INSERT INTO user VALUES(DEFAULT, 'Per', 'Persen', 'password', 'password', 'email9', 'phone9',2);
INSERT INTO user VALUES(DEFAULT, 'Mari', 'Nilsen', 'password', 'password', 'email10','phone10',2);

INSERT INTO user VALUES(DEFAULT, 'Hanne', 'Holm', 'password', 'password', 'email11', 'phone11',2);
INSERT INTO user VALUES(DEFAULT, 'Gunnar', 'Persen', 'password', 'password', 'email12', 'phone12',2);
INSERT INTO user VALUES(DEFAULT, 'Harry', 'Olsen', 'password', 'password', 'email13','phone13',2);
INSERT INTO user VALUES(DEFAULT, 'Tom', 'Jensen', 'password', 'password', 'email14', 'phone14',2);
INSERT INTO user VALUES(DEFAULT, 'Bente', 'Svendsen', 'password', 'password', 'email15', 'phone15', 2);

INSERT INTO user VALUES(DEFAULT, 'Anna', 'Kristiansen', 'password', 'password', 'email16', 'phone16',2);
INSERT INTO user VALUES(DEFAULT, 'Tonje', 'Tønne', 'password', 'password', 'email17', 'phone17',3);
INSERT INTO user VALUES(DEFAULT, 'Stig', 'Smith', 'password', 'password', 'email18', 'phone18',3);
INSERT INTO user VALUES(DEFAULT, 'Silje', 'Stigsen', 'password', 'password', 'email19', 'phone19',3);
INSERT INTO user VALUES(DEFAULT, 'Greg', 'Hansen', 'password', 'password', 'email20', 'phone20',3);

INSERT INTO user VALUES(DEFAULT, 'Helge', 'Helgesen', 'password','password', 'email21', 'phone21',3);
INSERT INTO user VALUES(DEFAULT, 'Bjørg', 'Solvang', 'password', 'password', 'email22', 'phone22',3);
INSERT INTO user VALUES(DEFAULT, 'Vincent', 'Hagen', 'password', 'password', 'email23', 'phone23', 3);
INSERT INTO user VALUES(DEFAULT, 'Erik', 'Jørgensen', 'password', 'password', 'email24', 'phone24', 3);
INSERT INTO user VALUES(DEFAULT, 'Heidi', 'Helmersen', 'password', 'password', 'email25', 'phone25', 3);


INSERT INTO user VALUES(DEFAULT, 'Jens', 'Jensen', 'password', 'password', 'admin', 'admin', 0);

INSERT INTO admin VALUES(26, true);

-- (user_id, percentage_work)
INSERT INTO employee VALUES(1, 100);
INSERT INTO employee VALUES(2, 100);
INSERT INTO employee VALUES(3, 100);
INSERT INTO employee VALUES(4, 100);
INSERT INTO employee VALUES(5, 100);
INSERT INTO employee VALUES(6, 100);
INSERT INTO employee VALUES(7, 100);
INSERT INTO employee VALUES(8, 100);
INSERT INTO employee VALUES(9, 100);

INSERT INTO employee VALUES(10, 100);
INSERT INTO employee VALUES(11, 100);
INSERT INTO employee VALUES(12, 100);
INSERT INTO employee VALUES(13, 100);
INSERT INTO employee VALUES(14, 100);
INSERT INTO employee VALUES(15, 100);
INSERT INTO employee VALUES(16, 100);
INSERT INTO employee VALUES(17, 100);
INSERT INTO employee VALUES(18, 100);
INSERT INTO employee VALUES(19, 100);
INSERT INTO employee VALUES(20, 100);
INSERT INTO employee VALUES(21, 100);
INSERT INTO employee VALUES(22, 100);
INSERT INTO employee VALUES(23, 100);
INSERT INTO employee VALUES(24, 100);
INSERT INTO employee VALUES(25, 100);

-- SHCEDULED SHIFTS
-- shift(shift_id, responsibility, valid_absence, date, time, user_id, dept_id)

-- department 1

-- NEW shift(shift_id, staff_number, date, time, dept_id)
-- employee_shift(user_id, shift_id, responsibility, valid_absence, shift_change)

-- SHIFTS: 
-- WEEK 1
INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-23', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-23', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-23', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-24', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-24', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-24', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-25', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-25', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-25', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-26', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-26', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-26', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-27', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-27', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-27', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-28', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-28', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-28', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-29', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-29', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-29', 3, 1);

-- WEEK 2
INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-23', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-23', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-23', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-24', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-24', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-24', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-25', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-25', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-25', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-26', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-26', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-26', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-27', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-27', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-27', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-28', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-28', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-28', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-29', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-29', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-29', 3, 1);

-- WEEK 3
INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-30', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-30', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-30', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-01-31', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-31', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-31', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-01', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-01', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-01', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-02', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-02', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-02', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-03', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-03', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-03', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-04', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-04', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-04', 3, 1);

INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-05', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-05', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-05', 3, 1);

-- WEEK 4 : DAY 1 (Shift with no employees)
INSERT INTO shift VALUES(DEFAULT, 4, '2017-02-06', 1, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-06', 2, 1);
INSERT INTO shift VALUES(DEFAULT, 3, '2017-02-06', 3, 1);


-- EMPLOYEE_SHIFT : employee_shift(user_id, shift_id, responsibility, valid_absence, shift_change)


-- WEEK 1: DAY 1
INSERT INTO employee_shift VALUES(1, 1, false, false, false);
INSERT INTO employee_shift VALUES(6, 1, false, false, false);
INSERT INTO employee_shift VALUES(12, 1, false, false, false);
INSERT INTO employee_shift VALUES(17, 1, true, false, false);
-- evening
INSERT INTO employee_shift VALUES(2, 2, false, false, false);
INSERT INTO employee_shift VALUES(10, 2, false, false, false);
INSERT INTO employee_shift VALUES(20, 2, true, false, false);
-- night
INSERT INTO employee_shift VALUES(3, 3, false, false, false);
INSERT INTO employee_shift VALUES(13, 3, false, false, false);
INSERT INTO employee_shift VALUES(21, 3, true, false, false);

-- WEEK 1 : DAY 2
INSERT INTO employee_shift VALUES(1, 4, false, false, false);
INSERT INTO employee_shift VALUES(11, 4, false, false, false);
INSERT INTO employee_shift VALUES(12, 4, false, false, false);
INSERT INTO employee_shift VALUES(19, 4, true, false, false);
-- evening
INSERT INTO employee_shift VALUES(2, 5, false, false, false);
INSERT INTO employee_shift VALUES(8, 5, false, false, false);
INSERT INTO employee_shift VALUES(25, 5, true, false, false);
-- night
INSERT INTO employee_shift VALUES(16, 6, false, false, false);
INSERT INTO employee_shift VALUES(22, 6, false, false, false);
INSERT INTO employee_shift VALUES(23, 6, true, false, false);

-- WEEK 1 : DAY 3
INSERT INTO employee_shift VALUES(4, 7, false, false, false);
INSERT INTO employee_shift VALUES(8, 7, false, false, false);
INSERT INTO employee_shift VALUES(17, 7, false, false, false);
INSERT INTO employee_shift VALUES(18, 7, true, false, false);
-- evening
INSERT INTO employee_shift VALUES(1, 8, false, false, false);
INSERT INTO employee_shift VALUES(11, 8, false, false, false);
INSERT INTO employee_shift VALUES(21, 8, true, false, false);
-- night
INSERT INTO employee_shift VALUES(5, 9, false, false, false);
INSERT INTO employee_shift VALUES(9, 9, false, false, false);
INSERT INTO employee_shift VALUES(22, 9, true, false, false);

-- WEEK 1 : DAY 4
INSERT INTO employee_shift VALUES(3, 10, false, false, false);
INSERT INTO employee_shift VALUES(11, 10, false, false, false);
INSERT INTO employee_shift VALUES(21, 10, false, false, false);
INSERT INTO employee_shift VALUES(25, 10, true, false, false);
-- evening
INSERT INTO employee_shift VALUES(2, 11, false, false, false);
INSERT INTO employee_shift VALUES(13, 11, false, false, false);
INSERT INTO employee_shift VALUES(18, 11, true, false, false);
-- night
INSERT INTO employee_shift VALUES(5, 12, false, false, false);
INSERT INTO employee_shift VALUES(9, 12, false, false, false);
INSERT INTO employee_shift VALUES(21, 12, true, false, false);

-- WEEK 1 : DAY 5
INSERT INTO employee_shift VALUES(4, 13, false, false, false);
INSERT INTO employee_shift VALUES(10, 13, false, false, false);
INSERT INTO employee_shift VALUES(17, 13, false, false, false);
INSERT INTO employee_shift VALUES(19, 13, true, false, false);
-- evening
INSERT INTO employee_shift VALUES(1, 14, false, false, false);
INSERT INTO employee_shift VALUES(11, 14, false, false, false);
INSERT INTO employee_shift VALUES(19, 14, true, false, false);
-- night
INSERT INTO employee_shift VALUES(5, 15, false, false, false);
INSERT INTO employee_shift VALUES(12, 15, false, false, false);
INSERT INTO employee_shift VALUES(22, 15, true, false, false);

-- WEEK 1 : DAY 6
INSERT INTO employee_shift VALUES(1, 16, false, false, false);
INSERT INTO employee_shift VALUES(6, 16, false, false, false);
INSERT INTO employee_shift VALUES(11, 16, false, false, false);
INSERT INTO employee_shift VALUES(17, 16, true, false, false);
-- evening
INSERT INTO employee_shift VALUES(2, 17, false, false, false);
INSERT INTO employee_shift VALUES(7, 17, false, false, false);
INSERT INTO employee_shift VALUES(20, 17, true, false, false);
-- night
INSERT INTO employee_shift VALUES(5, 18, false, false, false);
INSERT INTO employee_shift VALUES(13, 18, false, false, false);
INSERT INTO employee_shift VALUES(21, 18, true, false, false);

-- WEEK 1 : DAY 7
INSERT INTO employee_shift VALUES(4, 19, false, false, false);
INSERT INTO employee_shift VALUES(11, 19, false, false, false);
INSERT INTO employee_shift VALUES(12, 19, false, false, false);
INSERT INTO employee_shift VALUES(23, 19, true, false, false);
-- evening
INSERT INTO employee_shift VALUES(5, 20, false, false, false);
INSERT INTO employee_shift VALUES(8, 20, false, false, false);
INSERT INTO employee_shift VALUES(17, 20, true, false, false);
-- night
INSERT INTO employee_shift VALUES(3, 21, false, false, false);
INSERT INTO employee_shift VALUES(14, 21, false, false, false);
INSERT INTO employee_shift VALUES(24, 21, true, false, false);



-- WEEK 2: DAY 1
INSERT INTO employee_shift VALUES(1, 22, false, false, false);
INSERT INTO employee_shift VALUES(6, 22, false, false, false);
INSERT INTO employee_shift VALUES(12, 22, false, false, false);
INSERT INTO employee_shift VALUES(17, 22, true, false, false);
-- evening
INSERT INTO employee_shift VALUES(2, 23, false, false, false);
INSERT INTO employee_shift VALUES(10, 23, false, false, false);
INSERT INTO employee_shift VALUES(20, 23, true, false, false);
-- night
INSERT INTO employee_shift VALUES(3, 24, false, false, false);
INSERT INTO employee_shift VALUES(13, 24, false, false, false);
INSERT INTO employee_shift VALUES(21, 24, true, false, false);

-- WEEK 2 : DAY 2
INSERT INTO employee_shift VALUES(1, 25, false, false, false);
INSERT INTO employee_shift VALUES(11, 25, false, false, false);
INSERT INTO employee_shift VALUES(12, 25, false, false, false);
INSERT INTO employee_shift VALUES(19, 25, true, false, false);
-- evening
INSERT INTO employee_shift VALUES(2, 26, false, false, false);
INSERT INTO employee_shift VALUES(8, 26, false, false, false);
INSERT INTO employee_shift VALUES(25, 26, true, false, false);
-- night
INSERT INTO employee_shift VALUES(16, 27, false, false, false);
INSERT INTO employee_shift VALUES(22, 27, false, false, false);
INSERT INTO employee_shift VALUES(23, 27, true, false, false);

-- WEEK 2 : DAY 3
INSERT INTO employee_shift VALUES(4, 28, false, false, false);
INSERT INTO employee_shift VALUES(8, 28, false, false, false);
INSERT INTO employee_shift VALUES(17, 28, false, false, false);
INSERT INTO employee_shift VALUES(18, 28, true, false, false);
-- evening
INSERT INTO employee_shift VALUES(1, 29, false, false, false);
INSERT INTO employee_shift VALUES(11, 29, false, false, false);
INSERT INTO employee_shift VALUES(21, 29, true, false, false);
-- night
INSERT INTO employee_shift VALUES(5, 30, false, false, false);
INSERT INTO employee_shift VALUES(9, 30, false, false, false);
INSERT INTO employee_shift VALUES(22, 30, true, false, false);

-- WEEK 2 : DAY 4
INSERT INTO employee_shift VALUES(3, 31, false, false, false);
INSERT INTO employee_shift VALUES(11, 31, false, false, false);
INSERT INTO employee_shift VALUES(21, 31, false, false, false);
INSERT INTO employee_shift VALUES(25, 31, true, false, false);
-- evening
INSERT INTO employee_shift VALUES(2, 32, false, false, false);
INSERT INTO employee_shift VALUES(13, 32, false, false, false);
INSERT INTO employee_shift VALUES(18, 32, true, false, false);
-- night
INSERT INTO employee_shift VALUES(5, 33, false, false, false);
INSERT INTO employee_shift VALUES(9, 33, false, false, false);
INSERT INTO employee_shift VALUES(21, 33, true, false, false);

-- WEEK 2 : DAY 5
INSERT INTO employee_shift VALUES(4, 34, false, false, false);
INSERT INTO employee_shift VALUES(10, 34, false, false, false);
INSERT INTO employee_shift VALUES(17, 34, false, false, false);
INSERT INTO employee_shift VALUES(19, 34, true, false, false);
-- evening
INSERT INTO employee_shift VALUES(1, 35, false, false, false);
INSERT INTO employee_shift VALUES(11, 35, false, false, false);
INSERT INTO employee_shift VALUES(19, 35, true, false, false);
-- night
INSERT INTO employee_shift VALUES(5, 36, false, false, false);
INSERT INTO employee_shift VALUES(12, 36, false, false, false);
INSERT INTO employee_shift VALUES(22, 36, true, false, false);

-- WEEK 2 : DAY 6
INSERT INTO employee_shift VALUES(1, 37, false, false, false);
INSERT INTO employee_shift VALUES(6, 37, false, false, false);
INSERT INTO employee_shift VALUES(11, 37, false, false, false);
INSERT INTO employee_shift VALUES(17, 37, true, false, false);
-- evening
INSERT INTO employee_shift VALUES(2, 38, false, false, false);
INSERT INTO employee_shift VALUES(7, 38, false, false, false);
INSERT INTO employee_shift VALUES(20, 38, true, false, false);
-- night
INSERT INTO employee_shift VALUES(5, 39, false, false, false);
INSERT INTO employee_shift VALUES(13, 39, false, false, false);
INSERT INTO employee_shift VALUES(21, 39, true, false, false);

-- WEEK 2 : DAY 7
INSERT INTO employee_shift VALUES(4, 39, false, false, false);
INSERT INTO employee_shift VALUES(11, 39, false, false, false);
INSERT INTO employee_shift VALUES(12, 39, false, false, false);
INSERT INTO employee_shift VALUES(23, 39, true, false, false);
-- evening
INSERT INTO employee_shift VALUES(5, 40, false, false, false);
INSERT INTO employee_shift VALUES(8, 40, false, false, false);
INSERT INTO employee_shift VALUES(17, 40, true, false, false);
-- night
INSERT INTO employee_shift VALUES(3, 40, false, false, false);
INSERT INTO employee_shift VALUES(14, 40, false, false, false);
INSERT INTO employee_shift VALUES(24, 40, true, false, false);

                 
-- AVAILABILITY
-- (user_id, shift_id)

INSERT INTO availability VALUES(10, 41);
INSERT INTO availability VALUES(15, 41);
INSERT INTO availability VALUES(17, 42);
INSERT INTO availability VALUES(7, 42);
INSERT INTO availability VALUES(3, 43);
INSERT INTO availability VALUES(2, 43);


-- overtime
INSERT INTO overtime VALUES(1, '2017-01-01', 24, 26);
INSERT INTO overtime VALUES(1, '2017-01-02', 86, 88);
INSERT INTO overtime VALUES(1, '2017-01-04', 24, 30);

INSERT INTO overtime VALUES(1, '2017-01-05', 86, 2);
