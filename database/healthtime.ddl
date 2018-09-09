CREATE DATABASE healthtime 
    WITH ENCODING 'UTF8';


CREATE DOMAIN DMN_appointment_time
  SMALLINT
  CHECK (VALUE IN (1,  -- 8h00 ~ 8h30
                  2,  -- 8h30 ~ 9h00
                  3,  -- 9h00 ~ 9h30
                  4,  -- 9h30 ~ 10h00
                  5,  -- 10h00 ~ 10h30
                  6,  -- 10h30 ~ 11h00
                  7,  -- 11h00 ~ 11h30
                  8,  -- 13h30 ~ 14h00
                  9,  -- 14h00 ~ 14h30
                  10, -- 14h30 ~ 15h00
                  11, -- 15h00 ~ 15h30
                  12, -- 15h30 ~ 16h00
                  13, -- 16h00 ~ 16h30
                  14  -- 16h30 ~ 17h00
                 ));


CREATE TABLE users (
    id_user SERIAL,
    name VARCHAR(50) NOT NULL;
);

ALTER TABLE users ADD CONSTRAINT PK_USERS
    PRIMARY KEY (id_user);


CREATE TABLE consultant (
    id_user INT,
    sus_number VARCHAR(20) NOT NULL UNIQUE
);

ALTER TABLE consultant ADD CONSTRAINT PK_CONSULTANT
    PRIMARY KEY (id_user);
ALTER TABLE consultant ADD CONSTRAINT FK_USERS
    FOREIGN KEY (id_user) REFERENCES users (id_user);


CREATE TABLE doctor (
    id_user INT,
    crm_number VARCHAR(20) UNIQUE
);

ALTER TABLE doctor ADD CONSTRAINT PK_DOCTOR
    PRIMARY KEY (id_user);
ALTER TABLE doctor ADD CONSTRAINT FK_USERS
    FOREIGN KEY (id_user) REFERENCES users (id_user);


CREATE TABLE login (
    id_user INT,
    password VARCHAR(100) NOT NULL
);

ALTER TABLE login ADD CONSTRAINT PK_LOGIN
    PRIMARY KEY (id_user);
ALTER TABLE login ADD CONSTRAINT FK_LOGIN
    FOREIGN KEY (id_user) REFERENCES users (id_user);


CREATE TABLE specialty (
    id_specialty SERIAL,
    description VARCHAR(50) UNIQUE
);

ALTER TABLE specialty ADD CONSTRAINT PK_SPECIALTY
    PRIMARY KEY (id_specialty);


CREATE TABLE doctor_specialty (
     id_doctor INT,    
     id_specialty INT
);

ALTER TABLE doctor_specialty ADD CONSTRAINT PK_DOCTOR_SPECIALTY
    PRIMARY KEY (id_doctor, id_specialty);
ALTER TABLE doctor_specialty ADD CONSTRAINT FK_DOCTOR
    FOREIGN KEY (id_doctor) REFERENCES doctor (id_user);
ALTER TABLE doctor_specialty ADD CONSTRAINT FK_SPECIALTY
    FOREIGN KEY (id_specialty) REFERENCES specialty (id_specialty);


CREATE TABLE appointment (
     id_appointment SERIAL,
     id_doctor INT,
     id_consultant INT,    
     id_specialty INT,
     id_city INT,
     appointment_date DATE NOT NULL,
     appointment_time DMN_appointment_time NOT NULL
);

ALTER TABLE appointment ADD CONSTRAINT PK_APPOINTMENT
    PRIMARY KEY (id_appointment);
ALTER TABLE appointment ADD CONSTRAINT FK_DOCTOR
    FOREIGN KEY (id_doctor) REFERENCES doctor (id_user);
ALTER TABLE appointment ADD CONSTRAINT FK_CONSULTANT
    FOREIGN KEY (id_consultant) REFERENCES consultant (id_user);
ALTER TABLE appointment ADD CONSTRAINT FK_SPECIALTY
    FOREIGN KEY (id_specialty) REFERENCES specialty (id_specialty);