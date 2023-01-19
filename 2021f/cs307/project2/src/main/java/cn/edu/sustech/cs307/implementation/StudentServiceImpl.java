package cn.edu.sustech.cs307.implementation;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.grade.Grade;
import cn.edu.sustech.cs307.dto.grade.HundredMarkGrade;
import cn.edu.sustech.cs307.dto.grade.PassOrFailGrade;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.StudentService;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.DayOfWeek;
import java.util.*;


@ParametersAreNonnullByDefault
public class StudentServiceImpl implements StudentService {
    static IntegrityViolationException ive = new IntegrityViolationException();
    static String ADD_STU = "insert into students(id,firstname,surname,enroll,major) values(?,?,?,?,?)";

    @Override
    public void addStudent(int userId, int majorId, String firstName, String lastName, Date enrolledDate) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ADD_STU)) {
            stmt.setInt(1, userId);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setDate(4, enrolledDate);
            stmt.setInt(5, majorId);
            stmt.execute();
        } catch (SQLException e) {
            throw ive;
        }
    }

    static String SEARCH_COURSE = "select * from search_course(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    static String VARCHAR = "varchar";
    static String SECID = "secId", CID = "cid", CNAME = "cname", CRED = "cCredit", CLHR = "cClassHour", CGRD = "cGrading";
    static String SECN = "secName", TOTC = "secTotalCap", LFTC = "secLeftCap", CCNS = "conflictCourseNames",
            CSCID = "cscId", CSCII = "cscInsId", CSCIN = "cscInsName", DOW = "cscDayOfWeek", WKL = "cscWeekList",
            CB = "cscClassBegin", CE = "cscClassEnd", LOC = "cscLocation";

    @Override
    public List<CourseSearchEntry> searchCourse(int studentId, int semesterId,
                                                @Nullable String searchCid, @Nullable String searchName,
                                                @Nullable String searchInstructor,
                                                @Nullable DayOfWeek searchDayOfWeek, @Nullable Short searchClassTime,
                                                @Nullable List<String> searchClassLocations, CourseType searchCourseType,
                                                boolean ignoreFull, boolean ignoreConflict, boolean ignorePassed,
                                                boolean ignoreMissingPrerequisites, int pageSize, int pageIndex) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(SEARCH_COURSE)) {
            List<CourseSearchEntry> res = new ArrayList<>();
            stmt.setInt(1, studentId);
            stmt.setInt(2, semesterId);
            if (searchCid == null) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                searchCid = searchCid.replace('-', '_');
                stmt.setString(3, searchCid);
            }
            if (searchName == null) {
                stmt.setNull(4, Types.VARCHAR);
            } else {
                stmt.setString(4, searchName);
            }
            stmt.setString(5, searchInstructor);
            if (searchDayOfWeek == null) {
                stmt.setNull(6, Types.SMALLINT);
            } else {
                stmt.setInt(6, searchDayOfWeek.getValue());
            }
            if (searchClassTime == null) {
                stmt.setNull(7, Types.SMALLINT);
            } else {
                stmt.setShort(7, searchClassTime);
            }
            if (searchClassLocations == null) {
                stmt.setNull(8, Types.ARRAY);
            } else {
                stmt.setArray(8, con.createArrayOf(VARCHAR, searchClassLocations.toArray()));
            }
            stmt.setInt(9, searchCourseType.ordinal());
            stmt.setBoolean(10, ignoreFull);
            stmt.setBoolean(11, ignoreConflict);
            stmt.setBoolean(12, ignorePassed);
            stmt.setBoolean(13, ignoreMissingPrerequisites);
            stmt.setInt(14, pageSize);
            stmt.setInt(15, pageIndex);
            ResultSet rs = stmt.executeQuery();

            int prevCSec = -1;
            String prevCid = null;
            Course prevCourse = null;

            while (rs.next()) {
                int nowSec = rs.getInt(SECID);
                if (prevCSec != nowSec) {
                    prevCSec = nowSec;
                    String nowCid = rs.getString(CID).replace('_', '-');
                    if (prevCid == null || !prevCid.equals(nowCid)) {
                        prevCid = nowCid;
                        prevCourse = new Course();
                        prevCourse.id = prevCid;
                        prevCourse.name = rs.getString(CNAME);
                        prevCourse.credit = rs.getInt(CRED);
                        prevCourse.classHour = rs.getInt(CLHR);
                        prevCourse.grading = rs.getBoolean(CGRD)
                                ? Course.CourseGrading.HUNDRED_MARK_SCORE
                                : Course.CourseGrading.PASS_OR_FAIL;
                    }
                    CourseSection ncs = new CourseSection();
                    ncs.id = nowSec;
                    ncs.name = rs.getString(SECN);
                    ncs.totalCapacity = rs.getInt(TOTC);
                    ncs.leftCapacity = rs.getInt(LFTC);
                    CourseSearchEntry cse = new CourseSearchEntry();
                    cse.course = prevCourse;
                    cse.section = ncs;
                    cse.sectionClasses = new HashSet<>();
                    cse.conflictCourseNames = new ArrayList<>();
                    res.add(cse);
                    cse.conflictCourseNames.addAll(List.of((String[]) rs.getArray(CCNS).getArray()));
                }
                CourseSearchEntry cse = res.get(res.size() - 1);

                CourseSectionClass csc = new CourseSectionClass();
                csc.id = rs.getInt(CSCID);
                csc.instructor = new Instructor();
                csc.instructor.id = rs.getInt(CSCII);
                csc.instructor.fullName = rs.getString(CSCIN);
                csc.dayOfWeek = DayOfWeek.of(rs.getInt(DOW));
                csc.weekList = new HashSet<>(List.of((Short[]) rs.getArray(WKL).getArray()));
                csc.classBegin = rs.getShort(CB);
                csc.classEnd = rs.getShort(CE);
                csc.location = rs.getString(LOC);
                cse.sectionClasses.add(csc);
            }
            return res;
        } catch (Exception ignored) {
            return List.of();
        }
    }

    static String ENROLL = "select enrollcourse(?,?)";

    @Override
    public EnrollResult enrollCourse(int studentId, int sectionId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ENROLL)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            ResultSet feedback = stmt.executeQuery();
            feedback.next();
            return EnrollResult.values()[feedback.getShort(1)];
        } catch (Exception e) {
            return EnrollResult.UNKNOWN_ERROR;
        }
    }

    static String DROP = "select dropcourse(?,?)";
    static IllegalStateException il = new IllegalStateException();

    @Override
    public void dropCourse(int studentId, int sectionId) throws IllegalStateException {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(DROP)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            stmt.execute();
        } catch (SQLException e) {
            throw il;
        }
    }

    static String ENROLL_REC = "select add_enroll_record(?,?,?)";
    static String PASS = "P", FAIL = "F";

    @Override
    public void addEnrolledCourseWithGrade(int studentId, int sectionId, @Nullable Grade grade) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ENROLL_REC)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            if (grade == null) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, grade instanceof HundredMarkGrade ?
                        Short.toString(((HundredMarkGrade) grade).mark) :
                        (grade == PassOrFailGrade.PASS ? PASS : FAIL));
            }
            stmt.execute();
        } catch (SQLException ignored) {
        }
    }

    static String UGR = "select upgrade_grade_record(?,?,?)";

    @Override
    public void setEnrolledCourseGrade(int studentId, int sectionId, Grade grade) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(UGR)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            stmt.setString(3, grade instanceof HundredMarkGrade ? Short.toString(((HundredMarkGrade) grade).mark) :
                    (grade == PassOrFailGrade.PASS ? PASS : FAIL));
            var res = stmt.executeQuery();
            res.next();
            if (!res.getBoolean(1)) {
                throw ive;
            }
        } catch (SQLException e) {
            throw ive;
        }
    }

    static String ECG = "select * from enrolled_courses_grades(?,?)", CREDIT = "credit", CLHRR = "classhour",
            GBOOL = "grading", GRDIF = "grade";

    @Override
    public Map<Course, Grade> getEnrolledCoursesAndGrades(int studentId, @Nullable Integer semesterId) {
        Map<Course, Grade> res = new HashMap<>();
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ECG)) {
            stmt.setInt(1, studentId);
            if (semesterId == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, semesterId);
            }
            ResultSet primRes = stmt.executeQuery();
            while (primRes.next()) {
                Course course = new Course();
                course.id = primRes.getString(CID).replace('_', '-');
                course.name = primRes.getString(CNAME);
                course.credit = primRes.getInt(CREDIT);
                course.classHour = primRes.getInt(CLHRR);
                course.grading = primRes.getBoolean(GBOOL) ?
                        Course.CourseGrading.HUNDRED_MARK_SCORE :
                        Course.CourseGrading.PASS_OR_FAIL;
                String tmpGrade = primRes.getString(GRDIF);
                Grade grade;
                if (tmpGrade == null) {
                    grade = null;
                } else if (course.grading == Course.CourseGrading.PASS_OR_FAIL) {
                    grade = PASS.equals(tmpGrade) ? PassOrFailGrade.PASS : PassOrFailGrade.FAIL;
                } else {
                    grade = new HundredMarkGrade(Short.parseShort(tmpGrade));
                }
                res.put(course, grade);
            }
        } catch (SQLException ignored) {
        }
        return res;
    }

    static String GCT = "select * from search_course_table(?,?)",
            CLSST = "clsst", CLSED = "clsed", CUZFN = "cuzfn", INSID = "insid", INSFN = "insfn",
            LOCC = "loc", DOWW = "dow";

    @Override
    public CourseTable getCourseTable(int studentId, Date date) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(GCT)) {
            stmt.setInt(1, studentId);
            stmt.setDate(2, date);
            var res = stmt.executeQuery();
            CourseTable ct = new CourseTable();
            ct.table = new HashMap<>(7);
            ct.table.put(DayOfWeek.MONDAY, new HashSet<>());
            ct.table.put(DayOfWeek.TUESDAY, new HashSet<>());
            ct.table.put(DayOfWeek.WEDNESDAY, new HashSet<>());
            ct.table.put(DayOfWeek.THURSDAY, new HashSet<>());
            ct.table.put(DayOfWeek.FRIDAY, new HashSet<>());
            ct.table.put(DayOfWeek.SATURDAY, new HashSet<>());
            ct.table.put(DayOfWeek.SUNDAY, new HashSet<>());

            while (res.next()) {
                CourseTable.CourseTableEntry cte = new CourseTable.CourseTableEntry();
                cte.classBegin = res.getShort(CLSST);
                cte.classEnd = res.getShort(CLSED);
                cte.courseFullName = res.getString(CUZFN);
                cte.instructor = new Instructor();
                cte.instructor.id = res.getInt(INSID);
                cte.instructor.fullName = res.getString(INSFN);
                cte.location = res.getString(LOCC);
                ct.table.get(DayOfWeek.of(res.getInt(DOWW))).add(cte);
            }
            return ct;
        } catch (Exception ignored) {
            return null;
        }
    }

    static String PASSPRE = "select check_prerequisites(?,?,null::ltree,1)";

    @Override
    public boolean passedPrerequisitesForCourse(int studentId, String courseId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(PASSPRE)) {
            stmt.setInt(1, studentId);
            stmt.setString(2, courseId);
            var res = stmt.executeQuery();
            res.next();
            return res.getBoolean(1);
        } catch (Exception e) {
            return false;
        }
    }

    static String GSM = "select m.id as major_id, m.name as major_name, d.id as department_id, d.name as department_name from students s join major m join department d on d.id = m.dept on m.id = s.major where s.id=?";
    static String DPT_ID = "department_id", DPT_NM = "department_name", MAJ_ID = "major_id", MAJ_NM = "major_name";
    static EntityNotFoundException enf = new EntityNotFoundException();

    @Override
    public Major getStudentMajor(int studentId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(GSM)) {
            stmt.setInt(1, studentId);
            ResultSet re = stmt.executeQuery();
            re.next();
            Department department = new Department();
            department.id = re.getInt(DPT_ID);
            department.name = re.getString(DPT_NM);
            Major major = new Major();
            major.id = re.getInt(MAJ_ID);
            major.name = re.getString(MAJ_NM);
            major.department = department;
            return major;
        } catch (SQLException e) {
            throw enf;
        }
    }
}
