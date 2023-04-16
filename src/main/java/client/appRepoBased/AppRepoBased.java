package client.appRepoBased;

import client.appRepoBased.model.Student;
import client.appRepoBased.model.Teacher;
import client.appRepoBased.repository.StudentRepository;
import client.appRepoBased.repository.TeacherRepository;
import client.appRepoBased.service.StudentService;
import org.z_orm.DBInfo;
import org.z_orm.DDLType;
import org.z_orm.DialectType;
import org.z_orm.configuration.ConfigurationContext;

import java.util.List;

public class AppRepoBased {

    public static void main(String[] args) {
        initContext();
        StudentRepository studentRepository = new StudentRepository();
        TeacherRepository teacherRepository = new TeacherRepository();

//        Student student = new Student();
//        student.setId(10L);
//        student.setFirstName("Zayar");
//        student.setLastName("Linn Naung");
//        student.setEmail("");
//        student.setAge(23);
//
//        studentRepository.delete(student);

//        student = studentRepository.save(student);
//        System.out.println("-> " + student);

//        List<Student> x = studentRepository.findAll();
//        x.forEach(student -> System.out.println(student));

//        Optional<Student> y = studentRepository.findById(6L);
//        System.out.println(y);

//        StudentService studentService = new StudentService();
//
//        Student student = new Student();
//        student.setFirstName("Zayar");
//        student.setLastName("Linn Naung");
//        student.setEmail("");
//        student.setAge(23);
//        studentRepository.save(student);
//
//        Teacher teacher = new Teacher();
//        teacher.setName("Zayar Linn Naung");
//        teacher.setAge(53);
//        teacherRepository.save(teacher);
//
//        System.out.println(teacherRepository.findAll());
//        System.out.println(studentRepository.findAll());

    }

    private static void initContext(){
        ConfigurationContext configurationContext = ConfigurationContext.getInstance();
        configurationContext.setDbInfo(getDBInfo());
        configurationContext.setDdlType(DDLType.CREATE);
        configurationContext.setDialectType(DialectType.MySQLDialect);
    }

    private static DBInfo getDBInfo(){
        DBInfo dbInfo = new DBInfo();
        dbInfo.setDriverClass("com.mysql.cj.jdbc.Driver");
        dbInfo.setUrl("jdbc:mysql://localhost:3306/Z_ORM?useSSL=false&allowPublicKeyRetrieval=True");
        dbInfo.setUsername("root");
        dbInfo.setPassword("Zayar2142000");
        return dbInfo;
    }
}
