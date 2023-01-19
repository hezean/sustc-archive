/**
 * First checked @Chris Dec. 21
 */

package cn.edu.sustech.cs307.implementation;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Semester;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.SemesterService;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class SemesterServiceImpl implements SemesterService {
    static String ADD_SEM = "insert into semester(name, begindate, enddate) values(?,?,?) returning id";
    static IntegrityViolationException iv = new IntegrityViolationException();

    @Override
    public int addSemester(String name, Date begin, Date end) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ADD_SEM)) {
            stmt.setString(1, name);
            stmt.setDate(2, begin);
            stmt.setDate(3, end);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw iv;
        }
    }

    static String RM_SEM = "delete from semester where id = ?";
    static EntityNotFoundException enf = new EntityNotFoundException();

    @Override
    public void removeSemester(int semesterId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(RM_SEM)) {
            stmt.setInt(1, semesterId);
            var res = stmt.executeUpdate();
            if (res == 0) {
                throw enf;
            }
        } catch (SQLException ignored) {
        }
    }

    static String GET_ALL_SEM = "select * from semester", ID = "id", NAME = "name", BDT = "begindate", EDT = "enddate";

    @Override
    public List<Semester> getAllSemesters() {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(GET_ALL_SEM)) {
            ResultSet resultSet = stmt.executeQuery();
            List<Semester> allSemester = new ArrayList<>();
            while (resultSet.next()) {
                Semester s = new Semester();
                s.id = resultSet.getInt(ID);
                s.name = resultSet.getString(NAME);
                s.begin = resultSet.getDate(BDT);
                s.end = resultSet.getDate(EDT);
                allSemester.add(s);
            }
            return allSemester;
        } catch (SQLException ignored) {
        }
        return List.of();
    }

    static String GET_SEM="select * from semester where id = ?";
    @Override
    public Semester getSemester(int semesterId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(GET_SEM)) {
            stmt.setInt(1, semesterId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                Semester s = new Semester();
                s.id = resultSet.getInt(ID);
                s.name = resultSet.getString(NAME);
                s.begin = resultSet.getDate(BDT);
                s.end = resultSet.getDate(EDT);
                return s;
            }
            throw enf;
        } catch (SQLException ignored) {
        }
        return null;
    }
}
