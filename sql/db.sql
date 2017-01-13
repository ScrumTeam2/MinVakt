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
    CONSTRAINT pk_user PRIMARY KEY(user_id)
);

-- Administration user. (rights: needed?)
CREATE TABLE admin(
  user_id INTEGER NOT NULL,
  rights BOOLEAN,
  CONSTRAINT pk_admin PRIMARY KEY(user_id)
);

-- Employee user
-- category: 1 - Assistent, 2 - Helsefagarbeider, 3 - Sykepleier
-- (user_id, category, percentage_work)
CREATE TABLE employee(
  user_id INTEGER NOT NULL,
  category INTEGER,
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
 -- (user_id, first_name, last_name, hash, salt, email, phonenumber)
 INSERT INTO user VALUES(DEFAULT, 'Siri', 'Andresen', 'oQaZgG266KjDzEkGTgXYMQ==','2oUGF8AAgobU1E3rcAtyiw==', 'email1', 'phone1');
 INSERT INTO user VALUES(DEFAULT, 'Geir', 'Geirsen', 'password','password','email2', 'phone2');
 INSERT INTO user VALUES(DEFAULT, 'Stine', 'Pettersen', 'password','password', 'email3', 'phone3');
 INSERT INTO user VALUES(DEFAULT, 'Kari', 'Karlsen', 'password', 'password', 'email4', 'phone4');
 INSERT INTO user VALUES(DEFAULT, 'Narve', 'Berntsen', 'password', 'password', 'email5', 'phone5');
 INSERT INTO user VALUES(DEFAULT, 'Finn', 'Fransen', 'passord', 'password', 'email6', 'phone6');
 INSERT INTO user VALUES(DEFAULT, 'Per', 'Persen', 'password', 'password', 'email7', 'phone7');
 INSERT INTO user VALUES(DEFAULT, 'Mari', 'Nilsen', 'password', 'password', 'email8','phone8');
 INSERT INTO user VALUES(DEFAULT, 'Hanne', 'Holm', 'password', 'password', 'email9', 'phone9');

 INSERT INTO user VALUES(DEFAULT, 'Tonje', 'Tønne', 'password', 'password', 'email10', 'phone10');
 INSERT INTO user VALUES(DEFAULT, 'Stig', 'Smith', 'password', 'password', 'email11', 'phone11');
 INSERT INTO user VALUES(DEFAULT, 'Silje', 'Stigsen', 'password', 'password', 'email12', 'phone12');
 INSERT INTO user VALUES(DEFAULT, 'Greg', 'Hansen', 'password', 'password', 'email13', 'phone13');
 INSERT INTO user VALUES(DEFAULT, 'Helge', 'Helgesen', 'password','password', 'email14', 'phone14');
 INSERT INTO user VALUES(DEFAULT, 'Bjørg', 'Solvang', 'password', 'password', 'email15', 'phone15');
 INSERT INTO user VALUES(DEFAULT, 'Gunnar', 'Persen', 'password', 'password', 'email16', 'phone16');
 INSERT INTO user VALUES(DEFAULT, 'Harry', 'Olsen', 'password', 'password', 'email17','phone17');
 INSERT INTO user VALUES(DEFAULT, 'Tom', 'Jensen', 'password', 'password', 'email18', 'phone18');

INSERT INTO user VALUES(DEFAULT, 'Admin', '', 'password', 'password', 'admin', 'admin');
INSERT INTO admin VALUES(19, true);

 -- category: 1 - Assistent, 2 - Helsefagarbeider, 3 - Sykepleier
 -- (user_id, category, percentage_work)
 INSERT INTO employee VALUES(1, 1, 100);
 INSERT INTO employee VALUES(2, 1, 100);
 INSERT INTO employee VALUES(3, 1, 100);
 INSERT INTO employee VALUES(4, 2, 100);
 INSERT INTO employee VALUES(5, 2, 100);
 INSERT INTO employee VALUES(6, 2, 100);
 INSERT INTO employee VALUES(7, 3, 100);
 INSERT INTO employee VALUES(8, 3, 100);
 INSERT INTO employee VALUES(9, 3, 100);

 INSERT INTO employee VALUES(10, 1, 100);
 INSERT INTO employee VALUES(11, 1, 100);
 INSERT INTO employee VALUES(12, 1, 100);
 INSERT INTO employee VALUES(13, 2, 100);
 INSERT INTO employee VALUES(14, 2, 100);
 INSERT INTO employee VALUES(15, 2, 100);
 INSERT INTO employee VALUES(16, 3, 100);
 INSERT INTO employee VALUES(17, 3, 100);
 INSERT INTO employee VALUES(18, 3, 100);

 -- SHCEDULED SHIFTS
 -- shift(shift_id, responsibility, valid_absence, date, time, user_id, dept_id)

 -- department 1
 
 -- NEW shift(shift_id, staff_number, date, time, dept_id)
 -- employee_shift(user_id, shift_id, responsibility, valid_absence, shift_change)

 -- day 1
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-11', 1, 1); -- Day
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-11', 2, 1); -- Evening
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-11', 3, 1); -- Night

 -- day 2
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-12', 1, 1); -- Day
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-12', 2, 1); -- Evening
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-12', 3, 1); -- Night

 -- day 3
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-13', 1, 1); -- Day
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-13', 2, 1); -- Evening
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-13', 3, 1); -- Night


-- DAY 1
 INSERT INTO employee_shift VALUES(7, 1, true, false, false);
 INSERT INTO employee_shift VALUES(4, 1, false, false, false);
 INSERT INTO employee_shift VALUES(1, 1, false, false,false);

 INSERT INTO employee_shift VALUES(8, 2, true, false, false);
 INSERT INTO employee_shift VALUES(5, 2, false, false, false);
 INSERT INTO employee_shift VALUES(2, 2, false, false, false);

 INSERT INTO employee_shift VALUES(9, 3, true, false, false);
 INSERT INTO employee_shift VALUES(6, 3, false, false, false);
 INSERT INTO employee_shift VALUES(3, 3, false, false, false);

 -- DAY 2
 INSERT INTO employee_shift VALUES(7, 4, true, false, false);
 INSERT INTO employee_shift VALUES(4, 4, false, false, false);
 INSERT INTO employee_shift VALUES(1, 4, false, false, false);

 INSERT INTO employee_shift VALUES(9, 5, true, false, false);
 INSERT INTO employee_shift VALUES(6, 5, false, false, false);
 INSERT INTO employee_shift VALUES(3, 5, false, false, false);

 INSERT INTO employee_shift VALUES(8, 6, true, false, false);
 INSERT INTO employee_shift VALUES(5, 6, false, false, false);
 INSERT INTO employee_shift VALUES(2, 6, false, false, false);

 -- DAY 3
 INSERT INTO employee_shift VALUES(9, 7, true, false, false);
 INSERT INTO employee_shift VALUES(6, 7, false, false, false);
 INSERT INTO employee_shift VALUES(3, 7, false, false, false);

 INSERT INTO employee_shift VALUES(7, 8, true, false, false);
 INSERT INTO employee_shift VALUES(4, 8, false, false, false);
 INSERT INTO employee_shift VALUES(1, 8, false, false, false);

 INSERT INTO employee_shift VALUES(8, 9, true, false, false);
 INSERT INTO employee_shift VALUES(5, 9, false, false, false);
 INSERT INTO employee_shift VALUES(2, 9, false, false, false);


 -- department 2
 -- shift(shift_id, staff_number, date, time, dept_id)
 -- DAG 1
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-11', 1, 2);
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-11', 2, 2);
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-11', 3, 2);

 -- employee_shift(user_id, shift_id, responsibility, valid_absence, shift_change)
 -- dag 1, shift 1
 INSERT INTO employee_shift VALUES(16, 10,  true, false, false);
 INSERT INTO employee_shift VALUES(13, 10, false, false, false);
 INSERT INTO employee_shift VALUES(10, 10, false, false, false);

 -- dag 1, shift 2
 INSERT INTO employee_shift VALUES(17, 11,  true, false, false);
 INSERT INTO employee_shift VALUES(14, 11, false, false, false);
 INSERT INTO employee_shift VALUES(11, 11, false, false, false);

 -- dag 1, shift 3
 INSERT INTO employee_shift VALUES(18, 12,  true, false, false);
 INSERT INTO employee_shift VALUES(15, 12, false, false, false);
 INSERT INTO employee_shift VALUES(12, 12, false, false, false);

 -- DAG 2
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-12', 1, 2);
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-12', 2, 2);
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-12', 3, 2);

  -- dag 2, shift 1
 INSERT INTO employee_shift VALUES(17, 13,  true, false, false);
 INSERT INTO employee_shift VALUES(14, 13, false, false, false);
 INSERT INTO employee_shift VALUES(11, 13, false, false, false);

 -- dag 2, shift 2
 INSERT INTO employee_shift VALUES(18, 14,  true, false, false);
 INSERT INTO employee_shift VALUES(15, 14, false, false, false);
 INSERT INTO employee_shift VALUES(12, 14, false, false, false);

 -- dag 2, shift 3
 INSERT INTO employee_shift VALUES(16, 15,  true, false, false);
 INSERT INTO employee_shift VALUES(13, 15, false, false, false);
 INSERT INTO employee_shift VALUES(10, 15, false, false, false);

 -- DAG 3
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-13', 1, 2);
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-13', 2, 2);
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-13', 3, 2);


 -- dag 3, shift 1
 INSERT INTO employee_shift VALUES(17, 16,  true, false, false);
 INSERT INTO employee_shift VALUES(14, 16, false, false, false);
 INSERT INTO employee_shift VALUES(11, 16, false, false, false);

 -- dag 3, shift 2
 INSERT INTO employee_shift VALUES(18, 17,  true, false, false);
 INSERT INTO employee_shift VALUES(15, 17, false, false, false);
 INSERT INTO employee_shift VALUES(12, 17, false, false, false);

 -- dag 3, shift 3
 INSERT INTO employee_shift VALUES(16, 18,  true, false, false);
 INSERT INTO employee_shift VALUES(13, 18, false, false, false);
 INSERT INTO employee_shift VALUES(10, 18, false, false, false);

 -- AVAILABLE SHIFTS
 -- dag 4, shift 1
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-14', 1, 2);
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-14', 2, 2);
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-14', 1, 1);
 INSERT INTO shift VALUES(DEFAULT, 3, '2017-01-14', 3, 1);

 -- AVAILABILITY
 -- (user_id, shift_id)
 INSERT INTO employee_shift VALUES(13, 19, false, false, true);
 INSERT INTO employee_shift VALUES(18, 21,  true, false, true);
 
 INSERT INTO availability VALUES(10, 19);
 INSERT INTO availability VALUES(14, 20);
 INSERT INTO availability VALUES(17, 19);
 INSERT INTO availability VALUES(7, 21);
 INSERT INTO availability VALUES(3, 21);
 INSERT INTO availability VALUES(2, 22);
 
-- overtime
INSERT INTO overtime VALUES(1, '2017-01-01', 24, 26);
INSERT INTO overtime VALUES(1, '2017-01-02', 86, 88);
INSERT INTO overtime VALUES(1, '2017-01-04', 24, 30);
INSERT INTO overtime VALUES(1, '2017-01-05', 86, 2);
