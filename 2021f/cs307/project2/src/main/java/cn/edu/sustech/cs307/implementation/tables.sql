CREATE EXTENSION IF NOT EXISTS ltree;
CREATE EXTENSION IF NOT EXISTS intarray;

CREATE TABLE IF NOT EXISTS course
(
    id        varchar(15) PRIMARY KEY,
    name      varchar(50),
    credit    int,
    classHour int,
    grading   bool
);

CREATE INDEX IF NOT EXISTS cid_name_idx ON course (id, name);

CREATE TABLE IF NOT EXISTS prerequisites
(
    course   varchar(15)
        CONSTRAINT preq_cid_fk
            REFERENCES course (id)
            ON DELETE CASCADE,
    path     ltree,
    seqId    int,
    reqLevel int,
    CONSTRAINT preq_unk UNIQUE (course, path)
);

CREATE TABLE IF NOT EXISTS semester
(
    id        serial PRIMARY KEY,
    name      varchar(30) UNIQUE,
    beginDate date,
    endDate   date CHECK (endDate > semester.beginDate)
);

CREATE INDEX IF NOT EXISTS sem_dat_idx ON semester (beginDate, endDate);


CREATE TABLE IF NOT EXISTS courseSection
(
    id        serial PRIMARY KEY,
    course    varchar(15)
        CONSTRAINT cs_c_fk
            REFERENCES course (id)
            ON DELETE CASCADE,
    name      varchar,
    semester  int REFERENCES semester (id) ON DELETE CASCADE,
    capacity  int CHECK (capacity > 0),
    chosenStu int, -- CHECK (chosenStu >= 0 AND chosenStu <= capacity),
    UNIQUE (course, name, semester)
);
CREATE INDEX IF NOT EXISTS cs_c_sem_idx ON courseSection (id, course, semester);

CREATE TABLE IF NOT EXISTS department
(
    id   serial PRIMARY KEY,
    name varchar(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS major
(
    id   serial PRIMARY KEY,
    name varchar(30) UNIQUE NOT NULL,
    dept int
        CONSTRAINT maj_dept_fk
            REFERENCES department (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS instructors
(
    id        int PRIMARY KEY,
    firstName varchar(30),
    surName   varchar(30)
);

CREATE TABLE IF NOT EXISTS students
(
    id        int PRIMARY KEY,
    firstName varchar(30),
    surName   varchar(30),
    enroll    date,
    major     int REFERENCES major (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS courseSectionClass
(
    id         serial PRIMARY KEY,
    section    int
        CONSTRAINT csc_sec_fk
            REFERENCES courseSection (id)
            ON DELETE CASCADE,
    instructor int
        REFERENCES instructors (id)
            ON DELETE CASCADE,
    dayOfWeek  int,
    weekList   smallint[],
    classBegin smallint,
    classEnf   smallint CHECK (classEnf > classBegin),
    location   text
);

CREATE INDEX IF NOT EXISTS csc_sec_idx ON courseSectionClass (section);

CREATE TABLE IF NOT EXISTS selClass
(
    sid       int
        CONSTRAINT sel_sid_fk
            REFERENCES students (id)
            ON DELETE CASCADE,
    c_section int
        CONSTRAINT sle_cs_fk
            REFERENCES courseSection (id)
            ON DELETE CASCADE,
    grade     varchar(6)
);

CREATE INDEX IF NOT EXISTS selcls_ss ON selClass (sid, c_section);


CREATE TABLE IF NOT EXISTS MajorCourse
(
    majorid      int
        CONSTRAINT maj_mid_fk
            REFERENCES major (id)
            ON DELETE CASCADE,
    courseid     varchar(15)
        CONSTRAINT cour_cid_fk
            REFERENCES course (id)
            ON DELETE CASCADE,
    compul_elect boolean,
    PRIMARY KEY (majorid, courseid)
);
