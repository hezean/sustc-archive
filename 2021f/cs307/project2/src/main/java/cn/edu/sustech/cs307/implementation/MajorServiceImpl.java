package cn.edu.sustech.cs307.implementation;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.MajorService;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class MajorServiceImpl implements MajorService {
    static String ADD_MAJ = "insert into major (name, dept) values (?,?) returning id";
    static IntegrityViolationException iv = new IntegrityViolationException();

    @Override
    public int addMajor(String name, int departmentId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ADD_MAJ)) {
            stmt.setString(1, name);
            stmt.setInt(2, departmentId);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw iv;
        }
    }

    static String RM_MAJ = "delete from major where id = ?";
    static EntityNotFoundException enf = new EntityNotFoundException();

    @Override
    public void removeMajor(int majorId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(RM_MAJ)) {
            stmt.setInt(1, majorId);
            var res = stmt.executeUpdate();
            if (res == 0) {
                throw enf;
            }
        } catch (SQLException ignored) {
        }
    }

    static String GET_ALL_MAJ = "select * from major join department d on d.id = major.dept";
    static String DEPT = "dept", DNAME = "d.name", MAJID = "major.id", MAJNM = "major.name";

    @Override
    public List<Major> getAllMajors() {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(GET_ALL_MAJ)) {
            ResultSet resultSet = stmt.executeQuery();
            List<Major> result = new ArrayList<>();
            while (resultSet.next()) {
                Department department = new Department();
                department.id = resultSet.getInt(DEPT);
                department.name = resultSet.getString(DNAME);
                Major major = new Major();
                major.id = resultSet.getInt(MAJID);
                major.name = resultSet.getString(MAJNM);
                major.department = department;
                result.add(major);
            }
            return result;
        } catch (SQLException ignored) {
        }
        return List.of();
    }

    static String GET_MAJ = "select * from major join department d on d.id = major.dept where major.id=?";

    @Override
    public Major getMajor(int majorId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(GET_MAJ)) {
            stmt.setInt(1, majorId);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                throw enf;
            }
            Department department = new Department();
            department.id = resultSet.getInt(DEPT);
            department.name = resultSet.getString(DNAME);
            Major major = new Major();
            major.id = resultSet.getInt(MAJID);
            major.name = resultSet.getString(MAJNM);
            major.department = department;
            return major;
        } catch (SQLException ignored) {
            return null;
        }
    }

    static String ADD_MCC = "insert into MajorCourse(majorid,courseid,compul_elect) values(?,?,true)";

    @Override
    public void addMajorCompulsoryCourse(int majorId, String courseId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ADD_MCC)) {
            stmt.setInt(1, majorId);
            stmt.setString(2, courseId.replace('-', '_'));
            stmt.execute();
        } catch (SQLException e) {
            throw iv;
        }
    }

    static String ADD_MEC = "insert into MajorCourse(majorid,courseid,compul_elect) values(?,?,false)";

    @Override
    public void addMajorElectiveCourse(int majorId, String courseId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ADD_MEC)) {
            stmt.setInt(1, majorId);
            stmt.setString(2, courseId.replace('-', '_'));
            stmt.execute();
        } catch (SQLException e) {
            throw iv;
        }
    }
}
