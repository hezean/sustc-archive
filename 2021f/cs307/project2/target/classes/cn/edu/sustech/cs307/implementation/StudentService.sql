CREATE OR REPLACE FUNCTION get_instructed_cs(iid int, semid int)
    RETURNS table
            (
                id       int,
                secname  text,
                capacity int,
                left_cap int
            )
AS
$$
BEGIN
    RETURN QUERY (
        WITH csc AS (SELECT DISTINCT section FROM coursesectionclass WHERE instructor = iid)
        SELECT cs.id AS id, name AS secname, cs.capacity AS capacity, (capacity - chosenStu) AS left_cap
        FROM csc
                 JOIN coursesection cs ON csc.section = cs.id
        WHERE cs.semester = semid
    );
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION check_prerequisites(sidIn int, cid varchar(15),
                                               nowPath ltree, lv int)
    RETURNS boolean AS
$$
DECLARE
    root     varchar;
    cnt      int;
    totalCnt int;
    ss       ltree;
BEGIN
    IF (nowPath IS NULL) THEN
        IF (NOT EXISTS(SELECT 1 FROM prerequisites WHERE course = cid)) THEN
            RETURN TRUE;
        END IF;
        ss = (SELECT path
              FROM prerequisites
              WHERE course = cid
                AND reqlevel = 3
              LIMIT 1);
        RETURN check_prerequisites(sidIn, cid, ss, 3);
    END IF;

    root = ltree2text(subltree(nowPath, lv - 1, lv));
    IF (NOT starts_with(root, 'an') AND NOT starts_with(root, 'or')) THEN
        RETURN root IN (SELECT course
                        FROM (SELECT c_section, grade FROM selclass sel WHERE sel.sid = sidIn) x
                                 JOIN coursesection cse ON cse.id = x.c_section
                        WHERE grade != 'F'
                          AND (NOT (grade ~ '[0-9]+') OR CAST(grade AS int) BETWEEN 60 AND 100));
    END IF;

    totalCnt = (SELECT COUNT(*)
                FROM (SELECT path, seqid FROM prerequisites WHERE nowPath @> path AND reqlevel = lv + 1) sb);
    cnt = 1;

    IF (starts_with(root, 'an')) THEN
        WHILE cnt <= totalCnt
            LOOP
                IF (NOT check_prerequisites(sidIn, cid,
                                            (SELECT path
                                             FROM prerequisites
                                             WHERE nowPath @> path
                                               AND reqlevel = lv + 1
                                               AND seqid = cnt),
                                            lv + 1)) THEN
                    RETURN FALSE;
                END IF;
                cnt = cnt + 1;
            END LOOP;
        RETURN TRUE;
    END IF;

    IF (starts_with(root, 'or')) THEN
        WHILE cnt <= totalCnt
            LOOP
                IF (check_prerequisites(sidIn, cid,
                                        (SELECT path
                                         FROM prerequisites
                                         WHERE nowPath @> path
                                           AND reqlevel = lv + 1
                                           AND seqid = cnt),
                                        lv + 1)) THEN
                    RETURN TRUE;
                END IF;
                cnt = cnt + 1;
            END LOOP;
        RETURN FALSE;
    END IF;

    RETURN FALSE;
END;
$$ LANGUAGE 'plpgsql';

CREATE TABLE IF NOT EXISTS __enroll_cg_sem
(
    cid int,
    grd varchar(10)
);

CREATE OR REPLACE FUNCTION enrolled_courses_grades(siid int, semid int)
    RETURNS table
            (
                cid       varchar(15),
                cname     varchar(50),
                credit    smallint,
                classhour int,
                grading   bool,
                grade     varchar(10)
            )
AS
$$
BEGIN
    TRUNCATE __enroll_cg_sem;
    INSERT INTO __enroll_cg_sem
        (SELECT cs.course, grade
         FROM ((SELECT c_section, grade
                FROM selclass
                WHERE sid = siid) cc
                  JOIN coursesection cs ON cc.c_section = cs.id)
         WHERE semid IS NULL
            OR cs.semester = semid
        );
    RETURN QUERY (SELECT id, name, credit, classhour, grading, grade
                  FROM __enroll_cg_sem e
                           JOIN course c ON e.cid = c.id);
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION atTime(seciid int, dayofweekI int,
                                  classbeginI smallint, classendI smallint,
                                  weekListI smallint[])
    RETURNS bool
AS
$$
DECLARE
    cls record;
BEGIN
    FOR cls IN (SELECT dayOfWeek, weekList, classbegin, classenf FROM coursesectionclass WHERE section = seciid)
        LOOP
            IF (cls.dayOfWeek != dayofweekI
                OR cls.classbegin > classendI
                OR cls.classenf < classbeginI) THEN
                CONTINUE;
            END IF;
            IF ((SELECT CARDINALITY(weeklistI & cls.weekList) > 0)) THEN
                RETURN TRUE;
            END IF;
        END LOOP;
    RETURN FALSE;
END
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION atTimeSS(seciid int, isOppoConfId int)
    RETURNS bool
AS
$$
DECLARE
    cls record;
BEGIN
    IF ((SELECT semester FROM coursesection cs WHERE cs.id = seciid) !=
        (SELECT semester FROM coursesection cs WHERE cs.id = isOppoConfId)) THEN
        RETURN FALSE;
    END IF;
    FOR cls IN (SELECT dayOfWeek, weekList, classbegin, classenf FROM coursesectionclass WHERE section = seciid)
        LOOP
            IF (atTime(isOppoConfId, cls.dayofweek, cls.classbegin, cls.classenf, cls.weeklist)) THEN
                RETURN TRUE;
            END IF;
        END LOOP;
    RETURN FALSE;
END
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION enrollcourse(siid int, seciid int)
    RETURNS smallint AS
$$
DECLARE
    cuzid varchar(15);
    res   record;
    slav  record;
    sem   int;
BEGIN
    cuzid := (SELECT course FROM coursesection WHERE id = seciid);
    IF (cuzid IS NULL) THEN
        RETURN 1; -- course not found
    END IF;  -- course not found
    IF (EXISTS(SELECT FROM selclass WHERE sid = siid AND c_section = seciid)) THEN
        RETURN 3; -- already enrolled
    END IF;  -- already enrolled
    IF (EXISTS(
            SELECT 1
            FROM selclass hsc
                     JOIN coursesection c ON hsc.c_section = c.id
            WHERE c.course = cuzid
              AND hsc.sid = siid
              AND hsc.grade IS NOT NULL
              AND (starts_with(hsc.grade, 'P')
                OR (hsc.grade ~ '[0-9]' AND CAST(hsc.grade AS smallint) >= 60)))) THEN
        RETURN 4; -- already passed
    END IF;  -- already passed
    IF (NOT check_prerequisites(siid, cuzid, NULL, 0)) THEN
        RETURN 5; -- prerequisites not fulfilled
    END IF;  -- prerequisites not fulfilled
    sem := (SELECT semester FROM coursesection WHERE id = seciid);
    IF (cuzid IN (SELECT DISTINCT cs.course
                  FROM selclass sl
                           JOIN coursesection cs ON sl.c_section = cs.id
                  WHERE sl.sid = siid
                    AND semester = sem)) THEN
        RETURN 6; -- course already enrolled conflict / tc2
    END IF;  -- course already enrolled conflict / tc2
    FOR res IN (SELECT c_section FROM selclass WHERE sid = siid)
        LOOP
            IF ((SELECT semester FROM coursesection WHERE id = res.c_section) != sem) THEN
                CONTINUE;
            END IF;
            FOR slav IN (SELECT dayofweek, weekList, classbegin, classenf, weekList
                         FROM coursesectionclass csc
                         WHERE csc.section = seciid)
                LOOP
                    IF (atTime(res.c_section, slav.dayofweek, slav.classbegin, slav.classenf, slav.weeklist)) THEN
                        RETURN 6; -- time conflict / tc1
                    END IF;
                END LOOP;
        END LOOP;  -- time conflict / tc1
    IF (SELECT (chosenstu > capacity - 1) FROM coursesection WHERE id = seciid) THEN
        RETURN 2; -- section is full
    END IF;  -- section is full
    INSERT INTO selclass VALUES (siid, seciid, NULL);
    UPDATE coursesection SET chosenstu = chosenstu + 1 WHERE id = seciid;
    RETURN 0; -- success
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION check_pass(grade varchar(20)) RETURNS bool
AS
$$
BEGIN
    IF (grade IS NOT NULL) THEN
        IF (starts_with(grade, 'P')) THEN
            RETURN TRUE;
        ELSEIF (starts_with(grade, 'F')) THEN
            RETURN FALSE;
        ELSE
            RETURN CAST(grade AS smallint) >= 60;
        END IF;
    ELSE
        RETURN FALSE;
    END IF;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION add_enroll_record(stuid int, secid int, ggrade varchar(10))
    RETURNS void
AS
$$
BEGIN
    IF (ggrade IS NULL) THEN
        INSERT INTO selclass VALUES (stuid, secid);
    ELSE
        INSERT INTO selclass VALUES (stuid, secid, ggrade);
    END IF;
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION upgrade_grade_record(stuid int, secid int, ggrade varchar(10))
    RETURNS bool
AS
$$
DECLARE
    cid    varchar(15);
    gdpass bool;
    cgcnt  bool;
BEGIN
    cgcnt := FALSE;
    cid := (SELECT course FROM coursesection WHERE id = secid);
    IF (cid IS NULL) THEN
        RAISE '';
    END IF;
    gdpass := (SELECT grading FROM course c WHERE id = cid);
    IF (gdpass IS NULL) THEN
        RAISE '';
    END IF;
    IF ((gdpass AND ggrade ~ '[0-9]')
        OR (NOT gdpass AND ggrade NOT IN ('P', 'F'))) THEN
        RAISE '';
    END IF;
    UPDATE selclass SET grade = ggrade WHERE sid = stuid AND c_section = secid RETURNING TRUE INTO cgcnt;
    RETURN cgcnt;
END;
$$ LANGUAGE 'plpgsql';

CREATE TABLE IF NOT EXISTS __search_course_semester_course
(
    courseId   varchar(15),
    csectionId int,
    cscId      int,
    dayofweek  int,
    instructor int,
    classbegin int,
    classend   int,
    location   text
);

CREATE TABLE IF NOT EXISTS __search_course_conflict
(
    secid int,
    bysec int
);

CREATE OR REPLACE FUNCTION dropcourse(stuid int, secid int)
    RETURNS void AS
$$
DECLARE
    x int;
BEGIN
    IF (EXISTS(SELECT FROM selclass WHERE sid = stuid AND c_section = secid AND grade IS NOT NULL)) THEN
        RAISE '';
    END IF;
    DELETE FROM selclass WHERE (sid = stuid AND c_section = secid) RETURNING 1 INTO x;
    IF (x IS NOT NULL) THEN
        UPDATE coursesection SET chosenstu = chosenstu - 1 WHERE id = secid;
    END IF;
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION search_course(
    studentID int, semesterID int,
    searchCid text, searchName text,
    searchInstructor text,
    searchDayOfWeek int, searchClassTime int,
    searchClassLocations text[],
    searchCourseType int,
    ignoreFull boolean, ignoreConflict boolean, ignorePassed boolean,
    ignoreMissingPrerequisites boolean,
    pageSize int, pageIndex int
)
    RETURNS table
            (
                cid                 varchar(15),
                cname               varchar(50),
                cCredit             int,
                cClassHour          int,
                cGrading            bool,
                ------------course-----------
                secId               int,
                secName             varchar,
                secTotalCap         int,
                secLeftCap          int,
                --------courSection--------
                cscId               int,
                cscInsId            int,
                cscDayOfWeek        int,
                cscWeekList         smallint[],
                cscClassBegin       smallint,
                cscClassEnd         smallint,
                cscLocation         text,
                cscInsName          text,
                -------courseSectionClass-------
                conflictCourseNames text[]
            )
AS
$$
DECLARE
    majid   int;
    cName   varchar(50);
    secName varchar(50);
    rer     record;
    res     record;
BEGIN
    majid := (SELECT major FROM students WHERE id = studentID);
    TRUNCATE TABLE __search_course_semester_course;
    TRUNCATE TABLE __search_course_conflict;
    IF (searchCid IS NULL) THEN
        IF (searchCourseType IS NULL OR searchCourseType = 0) THEN
            INSERT INTO __search_course_semester_course
                (SELECT cs.course,
                        cs.id,
                        csc.id,
                        csc.dayofweek,
                        csc.instructor,
                        csc.classbegin,
                        csc.classenf,
                        csc.location
                 FROM coursesection cs
                          JOIN coursesectionclass csc ON cs.id = csc.section
                 WHERE cs.semester = semesterID);
        ELSEIF (searchCourseType = 1) THEN
            INSERT INTO __search_course_semester_course
                (SELECT cs.course,
                        cs.id,
                        csc.id,
                        csc.dayofweek,
                        csc.instructor,
                        csc.classbegin,
                        csc.classenf,
                        csc.location
                 FROM majorcourse mc
                          JOIN coursesection cs ON mc.courseid = cs.course
                          JOIN coursesectionclass csc ON cs.id = csc.section
                 WHERE mc.majorid = majid
                   AND cs.semester = semesterID
                   AND mc.compul_elect);
        ELSEIF (searchCourseType = 2) THEN
            INSERT INTO __search_course_semester_course
                (SELECT cs.course,
                        cs.id,
                        csc.id,
                        csc.dayofweek,
                        csc.instructor,
                        csc.classbegin,
                        csc.classenf,
                        csc.location
                 FROM majorcourse mc
                          JOIN coursesection cs ON mc.courseid = cs.course
                          JOIN coursesectionclass csc ON cs.id = csc.section
                 WHERE mc.majorid = majid
                   AND cs.semester = semesterID
                   AND NOT mc.compul_elect);
        ELSEIF (searchCourseType = 3) THEN
            INSERT INTO __search_course_semester_course
                (SELECT cs.course,
                        cs.id,
                        csc.id,
                        csc.dayofweek,
                        csc.instructor,
                        csc.classbegin,
                        csc.classenf,
                        csc.location
                 FROM majorcourse mc
                          JOIN coursesection cs ON mc.courseid = cs.course
                          JOIN coursesectionclass csc ON cs.id = csc.section
                 WHERE mc.majorid <> majid
                   AND cs.semester = semesterID);
        ELSEIF (searchCourseType = 4) THEN
            INSERT INTO __search_course_semester_course
                (SELECT cs.course,
                        cs.id,
                        csc.id,
                        csc.dayofweek,
                        csc.instructor,
                        csc.classbegin,
                        csc.classenf,
                        csc.location
                 FROM coursesection cs
                          JOIN coursesectionclass csc ON cs.id = csc.section
                 WHERE cs.course NOT IN (SELECT courseid FROM majorcourse)
                   AND cs.semester = semesterID);
        END IF;
    ELSE
        IF (searchCourseType IS NULL OR searchCourseType = 0) THEN
            INSERT INTO __search_course_semester_course
                (SELECT cs.course,
                        cs.id,
                        csc.id,
                        csc.dayofweek,
                        csc.instructor,
                        csc.classbegin,
                        csc.classenf,
                        csc.location
                 FROM coursesection cs
                          JOIN coursesectionclass csc ON cs.id = csc.section
                 WHERE cs.course ~ searchCid
                   AND cs.semester = semesterID);
        ELSEIF (searchCourseType = 1) THEN
            INSERT INTO __search_course_semester_course
                (SELECT cs.course,
                        cs.id,
                        csc.id,
                        csc.dayofweek,
                        csc.instructor,
                        csc.classbegin,
                        csc.classenf,
                        csc.location
                 FROM majorcourse mc
                          JOIN coursesection cs ON mc.courseid = cs.course
                          JOIN coursesectionclass csc ON cs.id = csc.section
                 WHERE cs.course ~ searchCid
                   AND mc.majorid = majid
                   AND cs.semester = semesterID
                   AND mc.compul_elect);
        ELSEIF (searchCourseType = 2) THEN
            INSERT INTO __search_course_semester_course
                (SELECT cs.course,
                        cs.id,
                        csc.id,
                        csc.dayofweek,
                        csc.instructor,
                        csc.classbegin,
                        csc.classenf,
                        csc.location
                 FROM majorcourse mc
                          JOIN coursesection cs ON mc.courseid = cs.course
                          JOIN coursesectionclass csc ON cs.id = csc.section
                 WHERE cs.course ~ searchCid
                   AND mc.majorid = majid
                   AND cs.semester = semesterID
                   AND NOT mc.compul_elect);
        ELSEIF (searchCourseType = 3) THEN
            INSERT INTO __search_course_semester_course
                (SELECT cs.course,
                        cs.id,
                        csc.id,
                        csc.dayofweek,
                        csc.instructor,
                        csc.classbegin,
                        csc.classenf,
                        csc.location
                 FROM majorcourse mc
                          JOIN coursesection cs ON mc.courseid = cs.course
                          JOIN coursesectionclass csc ON cs.id = csc.section
                 WHERE cs.course ~ searchCid
                   AND mc.majorid <> majid
                   AND cs.semester = semesterID);
        ELSEIF (searchCourseType = 4) THEN
            INSERT INTO __search_course_semester_course
                (SELECT cs.course,
                        cs.id,
                        csc.id,
                        csc.dayofweek,
                        csc.instructor,
                        csc.classbegin,
                        csc.classenf,
                        csc.location
                 FROM coursesection cs
                          JOIN coursesectionclass csc ON cs.id = csc.section
                 WHERE cs.course ~ searchCid
                   AND cs.course NOT IN (SELECT courseid FROM majorcourse)
                   AND cs.semester = semesterID);
        END IF;
    END IF;
    IF (searchDayOfWeek IS NOT NULL AND searchClassTime IS NOT NULL) THEN
        DELETE
        FROM __search_course_semester_course
        WHERE csectionId NOT IN
              (SELECT DISTINCT csectionId
               FROM __search_course_semester_course x
               WHERE x.dayofweek = searchDayOfWeek
                 AND searchClassTime BETWEEN x.classbegin AND x.classend);

    ELSEIF (searchDayOfWeek IS NOT NULL) THEN
        DELETE
        FROM __search_course_semester_course x
        WHERE csectionId NOT IN
              (SELECT DISTINCT csectionId
               FROM __search_course_semester_course
               WHERE dayofweek = searchDayOfWeek);
    ELSEIF (searchClassTime IS NOT NULL) THEN
        DELETE
        FROM __search_course_semester_course
        WHERE csectionId NOT IN
              (SELECT DISTINCT csectionId
               FROM __search_course_semester_course x
               WHERE searchClassTime BETWEEN x.classbegin AND x.classend);
    END IF;
    IF (searchName IS NOT NULL) THEN
        DELETE
        FROM __search_course_semester_course x
        WHERE POSITION(searchName IN ((SELECT c.name FROM course c WHERE c.id = x.courseId) || '[' ||
                                      (SELECT cs.name FROM coursesection cs WHERE cs.id = x.csectionId) || ']')) = 0;
    END IF;
    IF (searchInstructor IS NOT NULL) THEN
        WITH aval AS (SELECT id
                      FROM instructors i
                      WHERE starts_with(i.firstname, searchInstructor)
                         OR starts_with(i.surname, searchInstructor)
                         OR starts_with(i.firstname || i.surname, searchInstructor)
                         OR starts_with(i.firstname || ' ' || i.surname, searchInstructor)),
             allss AS (
                 SELECT DISTINCT x.csectionId AS sec
                 FROM __search_course_semester_course x
                 WHERE x.instructor IN (SELECT id FROM aval)
             )
        DELETE
        FROM __search_course_semester_course x
        WHERE x.csectionId NOT IN (SELECT sec FROM allss);
    END IF;
    IF (searchClassLocations IS NOT NULL) THEN
        DELETE
        FROM __search_course_semester_course x
        WHERE csectionId NOT IN (SELECT DISTINCT csectionId
                                 FROM __search_course_semester_course x
                                 WHERE x.location ~ ANY (searchClassLocations)
                                   AND (searchDayOfWeek IS NULL OR x.dayofweek = searchDayOfWeek)
                                   AND (searchClassTime IS NULL OR
                                        searchClassTime BETWEEN x.classbegin AND x.classend));
    END IF;

    IF (ignoreFull) THEN
        DELETE
        FROM __search_course_semester_course x
        WHERE (SELECT cs.capacity = cs.chosenstu FROM coursesection cs WHERE cs.id = x.csectionId);
    END IF;

    IF (ignorePassed) THEN
        DELETE
        FROM __search_course_semester_course x
        WHERE (x.courseid IN (SELECT cs.course
                              FROM selclass sel
                                       JOIN coursesection cs ON cs.id = sel.c_section
                              WHERE sel.sid = studentID
                                AND check_pass(sel.grade)));
    END IF;

    IF (ignoreMissingPrerequisites) THEN
        DELETE
        FROM __search_course_semester_course x
        WHERE NOT check_prerequisites(studentID, x.courseId, NULL, 0);
    END IF;

    IF (ignoreConflict) THEN
        DELETE
        FROM __search_course_semester_course scsc
        WHERE scsc.csectionId IN (SELECT DISTINCT cs.id secc
                                  FROM selclass sel
                                           JOIN coursesection cs ON sel.c_section = cs.id
                                  WHERE sel.sid = studentID
                                    AND cs.semester = semesterID);
        FOR res IN (SELECT sc.c_section AS csid
                    FROM selclass sc
                    WHERE sc.sid = studentID)
            LOOP
                IF ((SELECT semester FROM coursesection cs WHERE cs.id = res.csid) != semesterID) THEN
                    CONTINUE;
                END IF;
                FOR rer IN (SELECT csectionId FROM __search_course_semester_course scsc)
                    LOOP
                        IF (atTimeSS(rer.csectionId, res.csid)) THEN
                            DELETE
                            FROM __search_course_semester_course scsc
                            WHERE scsc.csectionId = rer.csectionId;
                        END IF;
                    END LOOP;
            END LOOP;

    ELSE
        WITH seell AS (SELECT DISTINCT cs.course ccr, cs.id secc
                       FROM selclass sel
                                JOIN coursesection cs ON sel.c_section = cs.id
                       WHERE sel.sid = studentID
                         AND cs.semester = semesterID)
        INSERT
        INTO __search_course_conflict
        SELECT tar.csectionId, seell.secc
        FROM __search_course_semester_course tar
                 JOIN seell ON tar.courseId = seell.ccr;

        FOR res IN (SELECT sc.c_section AS csid
                    FROM selclass sc
                    WHERE sc.sid = studentID)
            LOOP
                IF ((SELECT semester FROM coursesection cs WHERE cs.id = res.csid) != semesterID) THEN
                    CONTINUE;
                END IF;
                FOR rer IN (SELECT csectionId FROM __search_course_semester_course scsc)
                    LOOP
                        IF (atTimeSS(rer.csectionId, res.csid)) THEN
                            INSERT INTO __search_course_conflict VALUES (rer.csectionId, res.csid);
                        END IF;
                    END LOOP;
            END LOOP;
    END IF;

    RETURN QUERY (
        WITH garbage AS (
            SELECT tmp.courseId                                                cccid,
                   c.name                                                      cccname,
                   c.credit                                                    ccred,
                   c.classhour                                                 cclsh,
                   c.grading                                                   cg,

                   tmp.csectionId                                              csid,
                   cs.name                                                     csname,
                   cs.capacity                                                 cscap,
                   cs.capacity - cs.chosenstu                                  csleft,

                   tmp.cscid                                                   cscxid,
                   csc.instructor                                              cscinsiid,
                   csc.dayofweek                                               cscday,
                   csc.weeklist                                                cscwks,
                   csc.classbegin                                              cscbeg,
                   csc.classenf                                                cscend,
                   csc.location                                                cscloc,
                   CASE
                       WHEN firstname !~ '[A-Za-z]+' THEN firstname || surname
                       ELSE firstname || ' ' || surname END                    cscinsnname,
                   ARRAY(WITH secconf AS (SELECT scc.bysec secid
                                          FROM __search_course_conflict scc
                                          WHERE scc.secid = tmp.csectionId)
                         SELECT DISTINCT ((SELECT name
                                           FROM course
                                           WHERE course.id =
                                                 (SELECT cc.course
                                                  FROM coursesection cc
                                                  WHERE cc.id = secconf.secid)) || '['
                                              ||
                                          (SELECT name FROM coursesection WHERE coursesection.id = secconf.secid) ||
                                          ']') fn
                         FROM secconf
                         ORDER BY fn)                                          cscconfl,
                   DENSE_RANK() OVER (ORDER BY tmp.courseId, c.name , cs.name) rk
            FROM __search_course_semester_course tmp
                     JOIN course c ON c.id = tmp.courseId
                     JOIN coursesection cs ON tmp.csectionId = cs.id
                     JOIN coursesectionclass csc ON csc.id = tmp.cscid
                     JOIN instructors ins ON csc.instructor = ins.id)
        SELECT cccid,
               cccname,
               ccred,
               cclsh,
               cg,
               csid,
               csname,
               cscap,
               csleft,
               cscxid,
               cscinsiid,
               cscday,
               cscwks,
               cscbeg,
               cscend,
               cscloc,
               cscinsnname,
               cscconfl
        FROM garbage
        WHERE rk BETWEEN pageIndex * pageSize + 1 AND (pageIndex + 1) * pageSize
        ORDER BY rk
    );
END
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION search_course_table(stuid int, dat date)
    RETURNS table
            (
                cuzfn text,
                insid int,
                insfn text,
                clsst smallint,
                clsed smallint,
                dow   int,
                loc   text
            )
AS
$$
DECLARE
    sem int;
    wk  smallint;
BEGIN
    sem := (SELECT id FROM semester WHERE dat BETWEEN begindate AND enddate);
    wk := (SELECT (dat - (SELECT begindate FROM semester WHERE id = sem)) / 7 + 1);
    RETURN QUERY (
        SELECT C.name || '[' || cs.name || ']',
               csc.instructor,
               CASE
                   WHEN i.firstname !~ '[A-Za-z]+' THEN i.firstname || i.surname
                   ELSE i.firstname || ' ' || i.surname END,
               csc.classbegin,
               csc.classenf,
               csc.dayofweek,
               csc.location
        FROM selclass sc
                 JOIN coursesection cs ON cs.id = sc.c_section
                 JOIN course C ON C.id = cs.course
                 JOIN coursesectionclass csc ON csc.section = sc.c_section
                 JOIN instructors i ON i.id = csc.instructor
        WHERE sc.sid = stuid
          AND cs.semester = sem
          AND wk = ANY (csc.weeklist)
    );
END
$$ LANGUAGE 'plpgsql';
