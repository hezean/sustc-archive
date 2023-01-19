CREATE OR REPLACE FUNCTION remove_user(i int)
    RETURNS void AS
$$
DECLARE
    tmp int;
BEGIN
    DELETE FROM students WHERE id = i RETURNING 1 INTO tmp;
    IF (tmp is null) THEN
        DELETE FROM instructors WHERE id = i RETURNING 2 INTO tmp;
        IF (tmp is null) THEN
            RAISE EXCEPTION '';
        END IF;
    END IF;
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION get_all_students()
    RETURNS table
            (
                uid      int,
                uname    text,
                enrolld   date,
                majid    int,
                majname  varchar(30),
                deptid   int,
                deptname varchar(30)
            )
AS
$$
BEGIN
    RETURN QUERY (
        SELECT s.id                              AS uid,
               CASE
                   WHEN firstname ~ '[A-Za-z]+' THEN firstname || ' ' || surname
                   ELSE firstname || surname END AS uname,
               enroll,
               s.major                           AS majid,
               m.name                            AS majname,
               m.dept                            AS deptid,
               d.name                            AS deptname
        FROM students s
                 JOIN major m ON s.major = m.id
                 JOIN department d ON m.dept = d.id
    );
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION get_all_instructors()
    RETURNS table
            (
                uid   int,
                uname text
            )
AS
$$
BEGIN
    RETURN QUERY (
        SELECT id                                AS uid,
               CASE
                   WHEN firstname ~ '[A-Za-z]+' THEN firstname || ' ' || surname
                   ELSE surname || firstname END AS uname
        FROM instructors
    );
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION get_instructor(i int)
    RETURNS table
            (
                uid      int,
                fullname text
            )
AS
$$
BEGIN
    RETURN QUERY (
        WITH usr AS (SELECT id, firstname, surname
                     FROM instructors
                     WHERE id = i)
        SELECT id,
               CASE
                   WHEN firstname ~ '[A-Za-z]+' THEN firstname || ' ' || surname
                   ELSE firstname || surname END AS fullname
        FROM usr);
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION get_student(i int)
    RETURNS table
            (
                uid       int,
                fullname  text,
                enroll    date,
                maj_id    int,
                maj_name  varchar(30),
                dept_id   int,
                dept_name varchar(30)
            )
AS
$$
BEGIN
    IF ((SELECT COUNT(*) FROM students WHERE id = i) = 0) THEN
        RAISE EXCEPTION '';
    END IF;
    RETURN QUERY (
        WITH usr AS (
            SELECT id, firstname, surname, students.enroll, major AS mid
            FROM students
            WHERE students.id = i
        ),
             dep AS (
                 SELECT major.id AS maj_id, major.name AS maj_name, dept AS dept_id, department.name AS dept_name
                 FROM major
                          JOIN department ON major.dept = department.id
                 WHERE major.id = (SELECT mid FROM usr)
             )
        SELECT usr.id                              AS uid,
               (CASE
                    WHEN firstname ~ '[A-Za-z]+' THEN firstname || ' ' || surname
                    ELSE firstname || surname END) AS fullname,
               usr.enroll                          AS enroll,
               dep.maj_id                          AS maj_id,
               dep.maj_name                        AS maj_name,
               dep.dept_id                         AS dept_id,
               dep.dept_name                       AS dept_name
        FROM usr
                 NATURAL JOIN dep);
END;
$$ LANGUAGE 'plpgsql';
