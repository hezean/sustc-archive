package cn.edu.sustech.cs307.implementation;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.service.UserService;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class UserServiceImpl implements UserService {

    static String RM_USR = "select remove_user(?)";
    static EntityNotFoundException enf = new EntityNotFoundException();

    @Override
    public void removeUser(int userId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(RM_USR)) {
            stmt.setInt(1, userId);
            stmt.execute();
        } catch (SQLException e) {
            throw enf;
        }
    }

    static String GAU = "select * from get_all_students()",
            GAI = "select * from get_all_instructors()",
            UID = "uid", UNAME = "uname", ENROLL = "enroll",
            DEPI = "deptid", DEPN = "deptname",
            MAJI = "majid", MAJN = "majname";

    @Override
    public List<User> getAllUsers() {
        try (var con = SQLDataSource.getInstance().getSQLConnection()) {
            var stmt = con.prepareStatement(GAU);
            List<User> alluser = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.id = rs.getInt(UID);
                student.fullName = rs.getString(UNAME);
                student.enrolledDate = rs.getDate(ENROLL);
                Department department = new Department();
                department.id = rs.getInt(DEPI);
                department.name = rs.getString(DEPN);
                Major major = new Major();
                major.id = rs.getInt(MAJI);
                major.name = rs.getString(MAJN);
                major.department = department;
                student.major = major;
                alluser.add(student);
            }
            stmt = con.prepareStatement(GAI);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Instructor instructor = new Instructor();
                instructor.id = rs.getInt(UID);
                instructor.fullName = rs.getString(UNAME);
                alluser.add(instructor);
            }
            return alluser;
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return List.of();
        }
    }

    static String GU = "select get_instructor(?)", GS = "select get_student(?)",
            FULL_NAME = "fullname", DPT_N = "dept_name", DPT_I = "dept_id", MAJ_I = "maj_id", MAJ_N = "maj_name";

    @Override
    public User getUser(int userId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection()) {
            var stmt = con.prepareStatement(GU);
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                stmt = con.prepareStatement(GS);
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    throw enf;
                }

                Student student = new Student();
                student.id = rs.getInt(UID);
                student.enrolledDate = rs.getDate(ENROLL);
                student.fullName = rs.getString(FULL_NAME);
                Department department = new Department();
                department.name = rs.getString(DPT_N);
                department.id = rs.getInt(DPT_I);
                Major major = new Major();
                major.id = rs.getInt(MAJ_I);
                major.name = rs.getString(MAJ_N);
                major.department = department;
                student.major = major;
                return student;
            } else {
                Instructor instructor = new Instructor();
                instructor.id = resultSet.getInt(UID);
                instructor.fullName = resultSet.getString(FULL_NAME);
                return instructor;
            }
        } catch (SQLException ignored) {
        }
        return null;
    }
}
