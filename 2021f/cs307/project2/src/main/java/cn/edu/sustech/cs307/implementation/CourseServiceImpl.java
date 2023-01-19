package cn.edu.sustech.cs307.implementation;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.prerequisite.AndPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.CoursePrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.OrPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.CourseService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ParametersAreNonnullByDefault
public class CourseServiceImpl implements CourseService {

    static String ADD = "select add_course(?,?,?,?,?)";
    static String PREQ = "insert into prerequisites(course, path, seqid, reqlevel) values (?,text2ltree(?),?,?)";
    static String PRE = "Pre.";
    static String _AND = ".and", _OR = ".or";

    static IntegrityViolationException iv = new IntegrityViolationException();

    @Override
    public void addCourse(String courseId, String courseName,
                          int credit, int classHour,
                          Course.CourseGrading grading,
                          @Nullable Prerequisite prerequisite) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ADD)) {
            courseId = courseId.replace('-', '_');
            stmt.setString(1, courseId);
            stmt.setString(2, courseName);
            stmt.setInt(3, credit);
            stmt.setInt(4, classHour);
            stmt.setBoolean(5, grading != Course.CourseGrading.PASS_OR_FAIL);
            stmt.execute();
            if (prerequisite != null) {
                try (var pre = con.prepareStatement(PREQ)) {
                    String top = PRE + courseId;
                    pre.setString(1, courseId);
                    pre.setString(2, top);
                    pre.setInt(3, 2);
                    setPrerequisiteList(pre, prerequisite, top, 3, 1);
                    pre.executeBatch();
                }
            }
        } catch (Exception e) {
            throw iv;
        }
    }

    private void setPrerequisiteList(@Nonnull PreparedStatement stmt,
                                     Prerequisite preq, String lpath,
                                     int reqlevel, int seqid) throws SQLException {
        stmt.setInt(3, seqid);
        stmt.setInt(4, reqlevel);
        if (preq instanceof CoursePrerequisite) {
            stmt.setString(2, lpath + '.' +
                    ((CoursePrerequisite) preq).courseID.replace('-', '_'));
            stmt.addBatch();
        } else {
            lpath += ((preq instanceof AndPrerequisite) ? _AND : _OR) + seqid;
            stmt.setString(2, lpath);
            stmt.addBatch();
            int subid = 1;
            if (preq instanceof AndPrerequisite) {
                for (var i : ((AndPrerequisite) preq).terms)
                    setPrerequisiteList(stmt, i, lpath, reqlevel + 1, subid++);
            } else {
                for (var i : ((OrPrerequisite) preq).terms)
                    setPrerequisiteList(stmt, i, lpath, reqlevel + 1, subid++);
            }
        }
    }

    static String ADD_CS = "select addcoursesection(?,?,?,?)";

    @Override
    public int addCourseSection(String courseId, int semesterId, String sectionName, int totalCapacity) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ADD_CS)) {
            stmt.setString(1, courseId.replace('-', '_'));
            stmt.setInt(2, semesterId);
            stmt.setString(3, sectionName);
            stmt.setInt(4, totalCapacity);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (Exception e) {
            throw iv;
        }
    }

    static String ADD_CSC = "select addcoursesectionclass(?,?,?,?,?,?,?)";
    static String TYPE_INT4 = "smallint";

    @Override
    public int addCourseSectionClass(int sectionId, int instructorId, DayOfWeek dayOfWeek, Set<Short> weekList, short classStart, short classEnd, String location) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ADD_CSC)) {
            stmt.setInt(1, sectionId);
            stmt.setInt(2, instructorId);
            stmt.setInt(3, dayOfWeek.getValue());
            stmt.setArray(4, con.createArrayOf(TYPE_INT4, weekList.toArray()));
            stmt.setShort(5, classStart);
            stmt.setShort(6, classEnd);
            stmt.setString(7, location);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw iv;
        }
    }

    static String REMOVE = "delete from course where id = ?";

    @Override
    public void removeCourse(String courseId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(REMOVE)) {
            stmt.setString(1, courseId);
            var res = stmt.executeUpdate();
            if (res == 0) {
                throw new EntityNotFoundException();
            }
        } catch (SQLException ignored) {
        }
    }

    static String REMOVE_CS = "delete from coursesection where id = ?";

    @Override
    public void removeCourseSection(int sectionId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(REMOVE_CS)) {
            stmt.setInt(1, sectionId);
            var res = stmt.executeUpdate();
            if (res == 0) {
                throw new EntityNotFoundException();
            }
        } catch (SQLException ignored) {
        }
    }

    static String REMOVE_CSC = "delete from coursesectionclass where id = ?";

    @Override
    public void removeCourseSectionClass(int classId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(REMOVE_CSC)) {
            stmt.setInt(1, classId);
            var res = stmt.executeUpdate();
            if (res == 0) {
                throw new EntityNotFoundException();
            }
        } catch (SQLException ignored) {
        }
    }

    static String GET_ALL_C = "select * from course";
    static String RES_P = "P", RES_GRD = "grading", RES_ID = "id",
            RES_NAME = "name", RES_CRD = "credit", RES_HR = "classHour",
            RES_CAP = "capacity", RES_CHO = "chosenstu";

    @Override
    public List<Course> getAllCourses() {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(GET_ALL_C)) {
            ResultSet resultSet = stmt.executeQuery();
            List<Course> result = new ArrayList<>();
            while (resultSet.next()) {
                Course.CourseGrading grading = null;
                if (RES_P.equals(resultSet.getString(RES_GRD))) {
                    grading = Course.CourseGrading.PASS_OR_FAIL;
                } else if (resultSet.getString(RES_GRD) != null) {
                    grading = Course.CourseGrading.HUNDRED_MARK_SCORE;
                }
                Course course = new Course();
                course.id = resultSet.getString(RES_ID);
                course.name = resultSet.getString(RES_NAME);
                course.credit = resultSet.getInt(RES_CRD);
                course.classHour = resultSet.getInt(RES_HR);
                course.grading = grading;
                result.add(course);
            }
            return result;
        } catch (SQLException ignored) {
        }
        return List.of();
    }

    static String CS_SEM = "select getCourseSectionsInSemester(?,?)";

    @Override
    public List<CourseSection> getCourseSectionsInSemester(String courseId, int semesterId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(CS_SEM)) {
            stmt.setString(1, courseId);
            stmt.setInt(2, semesterId);
            ResultSet resultSet = stmt.executeQuery();
            List<CourseSection> result = new ArrayList<>();
            while (resultSet.next()) {
                CourseSection courseSection = new CourseSection();
                courseSection.id = resultSet.getInt(RES_ID);
                courseSection.name = resultSet.getString(RES_NAME);
                courseSection.totalCapacity = resultSet.getInt(RES_CAP);
                courseSection.leftCapacity = courseSection.totalCapacity - resultSet.getInt(RES_CHO);
                result.add(courseSection);
            }
            return result;
        } catch (SQLException ignored) {
        }
        return List.of();
    }

    static String CBS = "select getCourseBySection(?)";

    static EntityNotFoundException enf = new EntityNotFoundException();

    @Override
    public Course getCourseBySection(int sectionId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(CBS)) {
            stmt.setInt(1, sectionId);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                throw enf;
            }
            Course.CourseGrading grading = null;
            if (RES_P.equals(resultSet.getString(RES_GRD))) {
                grading = Course.CourseGrading.PASS_OR_FAIL;
            } else if (resultSet.getString(RES_GRD) != null) {
                grading = Course.CourseGrading.HUNDRED_MARK_SCORE;
            }
            Course course = new Course();
            course.id = resultSet.getString(RES_ID);
            course.name = resultSet.getString(RES_NAME);
            course.credit = resultSet.getInt(RES_CRD);
            course.classHour = resultSet.getInt(RES_HR);
            course.grading = grading;
            return course;
        } catch (SQLException ignored) {
        }
        return null;
    }

    static String CSCs = "select getCourseSectionClasses(?)";
    static String SECTION = "classid", FULL_NAME = "fullname";
    static String CLS_BEG = "classbegin", CLS_END = "classenf", CLS_LOC = "location";
    static String INS_ID = "instructorid", DOW = "dayofweek", WK_LIST = "weeklist";

    @Override
    public List<CourseSectionClass> getCourseSectionClasses(int sectionId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(CSCs)) {
            stmt.setInt(1, sectionId);
            ResultSet resultSet = stmt.executeQuery();
            List<CourseSectionClass> result = new ArrayList<>();
            while (resultSet.next()) {
                CourseSectionClass cour_sec_class = new CourseSectionClass();
                cour_sec_class.id = resultSet.getInt(SECTION);
                Instructor instructor = new Instructor();
                instructor.id = resultSet.getInt(INS_ID);
                instructor.fullName = resultSet.getString(FULL_NAME);
                cour_sec_class.instructor = instructor;
                cour_sec_class.dayOfWeek = DayOfWeek.of(resultSet.getInt(DOW));
                Short[] arr = (Short[]) resultSet.getArray(WK_LIST).getArray();
                cour_sec_class.weekList = new HashSet<>(List.of(arr));
                cour_sec_class.classBegin = resultSet.getShort(CLS_BEG);
                cour_sec_class.classEnd = resultSet.getShort(CLS_END);
                cour_sec_class.location = resultSet.getString(CLS_LOC);
                result.add(cour_sec_class);
            }
            return result;
        } catch (SQLException ignored) {
        }
        return null;
    }

    static String CS_CLS = "select getCourseSectionByClass(?)";

    @Override
    public CourseSection getCourseSectionByClass(int classId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(CS_CLS)) {
            stmt.setInt(1, classId);
            ResultSet resultSet = stmt.executeQuery();
            CourseSection result = new CourseSection();
            if (!resultSet.next()) {
                throw enf;
            }
            result.id = resultSet.getInt(SECTION);
            result.name = resultSet.getString(RES_NAME);
            result.totalCapacity = resultSet.getInt(RES_CAP);
            int chosenstu = resultSet.getInt(RES_CHO);
            result.leftCapacity = result.totalCapacity - chosenstu;
            return result;
        } catch (SQLException ignored) {
        }
        return null;
    }

    static String ESS = "select getEnrolledStudentsInSemester(?,?)";
    static String SID = "sid", ENROLL = "enroll", DEPT_NAME = "deptName",
            MAJ_ID = "majorId", MAJ_NAME = "majorName", MAJ_DEPT = "majorDept";

    @Override
    public List<Student> getEnrolledStudentsInSemester(String courseId, int semesterId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ESS)) {
            stmt.setString(1, courseId);
            stmt.setInt(2, semesterId);
            ResultSet resultSet = stmt.executeQuery();
            List<Student> result = new ArrayList<>();
            while (resultSet.next()) {
                Student stu = new Student();
                stu.id = resultSet.getInt(SID);
                stu.fullName = resultSet.getString(FULL_NAME);
                stu.enrolledDate = resultSet.getDate(ENROLL);
                Major major = new Major();
                major.id = resultSet.getInt(MAJ_ID);
                major.name = resultSet.getString(MAJ_NAME);
                Department department = new Department();
                department.id = resultSet.getInt(MAJ_DEPT);
                department.name = resultSet.getString(DEPT_NAME);
                major.department = department;
                stu.major = major;
                result.add(stu);
            }
            return result;
        } catch (SQLException ignored) {
        }
        return List.of();
    }
}
