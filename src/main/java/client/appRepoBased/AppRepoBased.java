package client.appRepoBased;

import client.appRepoBased.model.Student;
import client.appRepoBased.repository.StudentRepository;
import org.z_orm.DBInfo;
import org.z_orm.DDLType;
import org.z_orm.DialectType;
import org.z_orm.configuration.ConfigurationContext;

import java.util.List;
import java.util.Optional;

public class AppRepoBased {

    public static void main(String[] args) {
        initContext();
        StudentRepository studentRepository = new StudentRepository(Student.class);

//        Student student = new Student();
//        student.setFirstName("Zayar");
//        student.setLastName("Linn Naung");
//        student.setEmail("");
//        student.setAge(23);
//        student = studentRepository.save(student);
//        System.out.println("-> " + student);

//        List<Student> x = studentRepository.findAll();
//        x.forEach(student -> System.out.println(student));

        Optional<Student> x = studentRepository.findById(90L);
        System.out.println(x);

    }

    private static void initContext(){
        ConfigurationContext configurationContext = ConfigurationContext.getInstance();
        configurationContext.setDbInfo(getDBInfo());
        configurationContext.setDdlType(DDLType.UPDATE);
        configurationContext.setDialectType(DialectType.MySQLDialect);
    }

    private static DBInfo getDBInfo(){
        return DBInfo.builder()
                .driverClass("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/Z_ORM?useSSL=false&amp")
                .username("root")
                .password("Zayar2142000")
                .build();
    }
}
