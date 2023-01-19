package cn.edu.sustech.cs307.implementation;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.DepartmentService;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class DepartmentServiceImpl implements DepartmentService {

    static String ADD_DEPT = "insert into department(name) values (?) returning id";
    static IntegrityViolationException iv = new IntegrityViolationException();

    @Override
    public int addDepartment(String name) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ADD_DEPT)) {
            stmt.setString(1, name);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw iv;
        }
    }

    static String RM_DEPT = "delete from department where id = ?";

    static EntityNotFoundException enf = new EntityNotFoundException();

    @Override
    public void removeDepartment(int departmentId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(RM_DEPT)) {
            stmt.setInt(1, departmentId);
            var res = stmt.executeUpdate();
            if (res == 0) {
//                throw enf;
                System.err.println(0);
            }
        } catch (SQLException ignored) {
        }
    }

    static String ALL_DEPT = "select * from department";
    static String RES_ID = "id", RES_NAME = "name";

    @Override
    public List<Department> getAllDepartments() {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ALL_DEPT)) {
            ResultSet resultSet = stmt.executeQuery();
            List<Department> result = new ArrayList<>();
            while (resultSet.next()) {
                Department department = new Department();
                department.id = resultSet.getInt(RES_ID);
                department.name = resultSet.getString(RES_NAME);
                result.add(department);
            }
            return result;
        } catch (SQLException ignored) {
        }
        return List.of();
    }

    static String GA_DEPT = "select name from department where id = ?";

    @Override
    public Department getDepartment(int departmentId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(GA_DEPT)) {
            stmt.setInt(1, departmentId);
            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                throw enf;
            }
            Department department = new Department();
            department.id = departmentId;
            department.name = resultSet.getString(RES_NAME);
            return department;
        } catch (SQLException ignored) {
        }
        return null;
    }
}
