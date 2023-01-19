CREATE OR REPLACE FUNCTION getCourseSectionsInSemester(courseId varchar(15), semesterId int)
    RETURNS table
            (
                oid       int,
                ocourse   varchar(15),
                oname     varchar,
                osemester int,
                cap       int,
                chosen    int
            )
AS
$$
BEGIN
    RETURN QUERY (SELECT * FROM coursesection WHERE course = courseId AND semester = semesterId);
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION getCourseBySection(sectionId int) RETURNS record AS
$$
BEGIN
    RETURN (SELECT *
            FROM (
                     SELECT course
                     FROM coursesection cs
                     WHERE cs.id = sectionId
                 ) m
                     JOIN course ON m.course = course.id
    );
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION getCourseSectionClasses(sectionId int)
    RETURNS table
            (
                classid      int,
                instructorid int,
                dayofweek    int,
                weeklist     smallint[],
                classbegin   smallint,
                classenf     smallint,
                location     text,
                fullname     text
            )
AS
$$
BEGIN
    RETURN QUERY (SELECT csc.id                            AS classid,
                         instructor                        AS instructorid,
                         dayofweek,
                         weeklist,
                         classbegin,
                         classenf,
                         location,
                         CASE
                             WHEN firstname ~ '[A-Za-z]' THEN firstname || ' ' || surname
                             ELSE firstname || surname END AS fullname
                  FROM coursesectionclass csc
                           JOIN instructors i ON i.id = csc.instructor
                  WHERE section = sectionId
    );
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION getCourseSectionByClass(classId int) RETURNS record AS
$$
BEGIN
    RETURN (SELECT *
            FROM coursesection cs
                     JOIN coursesectionclass c ON cs.id = c.section
            WHERE c.id = classId
    );
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION getEnrolledStudentsInSemester(courseId varchar(15), semesterId int) RETURNS record AS
$$
BEGIN
    RETURN (
        SELECT sid,
               enroll,
               major                             AS majorId,
               CASE
                   WHEN firstname ~ '[A-Za-z]' THEN firstname || ' ' || surname
                   ELSE firstname || surname END AS fullname,
               majorName,
               majorDept,
               deptName
        FROM (
                 SELECT sid,
                        enroll,
                        major,
                        firstname,
                        surname,
                        m2.name AS majorName,
                        m2.dept AS majorDept,
                        d.name  AS deptName
                 FROM students
                          JOIN selclass s ON students.id = s.sid
                          JOIN coursesection c ON c.id = s.c_section
                          JOIN major m2 ON m2.id = students.major
                          JOIN department d ON d.id = m2.dept
                 WHERE course = courseId
                   AND semester = semesterId
             ) m
    );
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION add_course(cid character varying, cname character varying, cre integer, hour integer,
                                      grad bool) RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN
    INSERT INTO course(id, name, credit, classhour, grading)
    VALUES (cid, cname, cre, hour, grad);
END;
$$;

CREATE OR REPLACE FUNCTION addCourseSection(courseId varchar(50), semesterId int, sectionName varchar(50),
                                            totalCapacity int) RETURNS int AS
$$
DECLARE
    result int;
BEGIN
    INSERT INTO coursesection(course, name, semester, capacity, chosenstu)
    VALUES (courseId, sectionName, semesterId, totalCapacity, 0)
    RETURNING id INTO result;
    RETURN result;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION addCourseSectionClass(secid int, instid int, weekid int,
                                                 wl smallint[], st smallint, ed smallint, loc text)
    RETURNS int AS
$$
DECLARE
    result int;
BEGIN
    INSERT INTO coursesectionclass(section, instructor, dayofweek, weeklist, classBegin, classenf, location)
    VALUES (secid, instid, weekid, wl, st, ed, loc)
    RETURNING id INTO result;
    RETURN result;
END;
$$ LANGUAGE plpgsql;
