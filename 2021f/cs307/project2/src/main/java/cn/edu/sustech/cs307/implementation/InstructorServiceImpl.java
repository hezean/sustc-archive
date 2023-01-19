package cn.edu.sustech.cs307.implementation;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.CourseSection;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.InstructorService;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class InstructorServiceImpl implements InstructorService {
    static String ADD_INS = "insert into instructors(id,firstname,surname) values(?,?,?)";
    static IntegrityViolationException iv = new IntegrityViolationException();

    @Override
    public void addInstructor(int userId, String firstName, String lastName) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ADD_INS)) {
            stmt.setInt(1, userId);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.execute();
        } catch (Exception e) {
            throw iv;
        }
    }

    static String ICS = "select * from get_instructed_cs(?,?)";
    static String ID = "id", SEC_NAME = "secname", CAP = "capacity", LEFT = "left_cap";

    @Override
    public List<CourseSection> getInstructedCourseSections(int instructorId, int semesterId) {
        try (var con = SQLDataSource.getInstance().getSQLConnection();
             var stmt = con.prepareStatement(ICS)) {
            List<CourseSection> res = new ArrayList<>();
            stmt.setInt(1, instructorId);
            stmt.setInt(2, semesterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CourseSection cs = new CourseSection();
                cs.id = rs.getInt(ID);
                cs.name = rs.getString(SEC_NAME);
                cs.totalCapacity = rs.getInt(CAP);
                cs.leftCapacity = rs.getInt(LEFT);
                res.add(cs);
            }
            return res;
        } catch (Exception ignored) {
        }
        return List.of();
    }
}
