DROP SCHEMA IF EXISTS project1 CASCADE;
CREATE SCHEMA project1;

------------ course info ------------
CREATE TABLE department
(
    id   serial PRIMARY KEY,
    name varchar(20) UNIQUE NOT NULL
);

CREATE TABLE teacher
(
    id   serial PRIMARY KEY,
    dept int         NOT NULL REFERENCES department (id),
    name varchar(30) NOT NULL,
    CONSTRAINT teacher_unk UNIQUE (dept, name)
);

-- course -> class -> schedule
CREATE TABLE course
(
    cid    varchar(10) PRIMARY KEY,
    name   varchar(30) NOT NULL,
    period int CHECK (period > 0),
    credit numeric(2, 1) CHECK (credit >= 0),
    dept   int REFERENCES department (id)
);

CREATE TABLE class
(
    id       serial PRIMARY KEY,
    course   varchar(10) REFERENCES course (cid) NOT NULL,
    name     varchar(40)                         NOT NULL,
    capacity int CHECK (capacity > 0),
);

CREATE TABLE class_teacher
(
    record_id serial PRIMARY KEY,
    class     int REFERENCES class (id)   NOT NULL,
    teacher   int REFERENCES teacher (id) NOT NULL,
    CONSTRAINT cls_tec_unk UNIQUE (class, teacher)
);

CREATE TABLE schedule
(
    sch_id    serial PRIMARY KEY,
    class     int REFERENCES class (id) NOT NULL,
    week_list int[],
    location  varchar(30),
    starting  int CHECK (starting >= 1 AND starting <= 12),
    ending    int CHECK (ending >= 1 AND ending <= 12),
    weekday   int CHECK (weekday >= 1 AND weekday <= 7),
    CONSTRAINT time_valid CHECK (ending >= starting)
);

CREATE TABLE prerequisite
(
    record_id serial PRIMARY KEY,
    course    varchar(10) REFERENCES course (cid) NOT NULL,
    pre       varchar(10) REFERENCES course (cid) NOT NULL,
    pre_rk    int                                 NOT NULL,
    CONSTRAINT preq_unk UNIQUE (course, pre)
);


------------ select course ------------
CREATE TABLE college
(
    id       serial PRIMARY KEY,
    name     char(5) UNIQUE     NOT NULL,
    eng_name varchar(20) UNIQUE NOT NULL
);

CREATE TYPE sex AS enum ('M','F');

CREATE TABLE student
(
    id      serial PRIMARY KEY,
    name    varchar(5) NOT NULL,
    sex     sex,
    college int REFERENCES college (id) DEFERRABLE
);

CREATE TABLE learnt
(
    sid    int REFERENCES student (id) DEFERRABLE NOT NULL,
    course varchar(10) REFERENCES course (cid)    NOT NULL,
    CONSTRAINT learn_unk UNIQUE (sid, course)
);

CREATE TABLE enrollment
(
    record_id serial PRIMARY KEY,
    sid       int REFERENCES student (id) DEFERRABLE NOT NULL,
    class     int REFERENCES class (id)              NOT NULL,
    CONSTRAINT enroll_unk UNIQUE (sid, class)
);