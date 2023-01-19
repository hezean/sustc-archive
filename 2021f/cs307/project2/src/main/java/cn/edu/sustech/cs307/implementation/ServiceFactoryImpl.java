package cn.edu.sustech.cs307.implementation;

import cn.edu.sustech.cs307.factory.ServiceFactory;
import cn.edu.sustech.cs307.service.*;

import java.util.List;

public class ServiceFactoryImpl extends ServiceFactory {
    public ServiceFactoryImpl() {
        registerService(CourseService.class, new CourseServiceImpl());
        registerService(DepartmentService.class, new DepartmentServiceImpl());
        registerService(InstructorService.class, new InstructorServiceImpl());
        registerService(MajorService.class, new MajorServiceImpl());
        registerService(SemesterService.class, new SemesterServiceImpl());
        registerService(StudentService.class, new StudentServiceImpl());
        registerService(UserService.class, new UserServiceImpl());
    }

    @Override
    public List<String> getUIDs() {
        return List.of("12011323", "12011517", "12011439");
    }
}
