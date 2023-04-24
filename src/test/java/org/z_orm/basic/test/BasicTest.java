package org.z_orm.basic.test;

import org.junit.jupiter.api.*;
import org.z_orm.DBInfo;
import org.z_orm.DDLType;
import org.z_orm.DialectType;
import org.z_orm.basic.repository.StudentRepository;
import org.z_orm.configuration.ConfigurationContext;
import org.z_orm.basic.model.Student;

import java.util.Optional;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class BasicTest {

    private static StudentRepository studentRepository;

    @BeforeAll
    static void initContext(){
        ConfigurationContext configurationContext = ConfigurationContext.getInstance();
        configurationContext.setDbInfo(getDBInfo());
        configurationContext.setDdlType(DDLType.CREATE);
        configurationContext.setDialectType(DialectType.MySQLDialect);
        configurationContext.setEntityPath("org.z_orm.basic");

        studentRepository = new StudentRepository();
    }

    @Test
    @Order(value = 1)
    void testSaveMethod(){
        Student student = new Student(null, "Zayar Linn Naung", 23);
        Assertions.assertDoesNotThrow(() -> studentRepository.save(student));
    }

    @Test
    @Order(value = 2)
    void testSaveMethodReturnId(){
        Student student = new Student(null, "Zayar Linn Naung", 23);
        student = studentRepository.save(student);
        Assertions.assertNotNull(student.getId());
    }

    @Test
    @Order(value = 3)
    void testFindALlMethod(){
        Assertions.assertDoesNotThrow(() -> studentRepository.findAll());
    }

    @Test
    @Order(value = 4)
    void testFindByIdMethod(){
        Assertions.assertDoesNotThrow(() -> studentRepository.findById(1L));
    }

    @Test
    @Order(value = 5)
    void testUpdateMethod(){
        Optional<Student> studentOptional = studentRepository.findById(1L);
        if(studentOptional.isPresent()){
            Student student = studentOptional.get();
            student.setName("Updated Username");
            student = studentRepository.save(student);

            Assertions.assertEquals("Updated Username", student.getName());
        }
    }

    @Test
    @Order(value = 6)
    void testDeleteMethod(){

        Optional<Student> studentOptional = studentRepository.findById(1L);
        if(studentOptional.isPresent()){
            Student student = studentOptional.get();
            studentRepository.delete(student);
            Optional<Student> deleteStudentOptional = studentRepository.findById(1L);
            Assertions.assertTrue(deleteStudentOptional.isEmpty());
        }
    }
    private static DBInfo getDBInfo(){
        DBInfo dbInfo = new DBInfo();
        dbInfo.setDriverClass("com.mysql.cj.jdbc.Driver");
        dbInfo.setUrl("jdbc:mysql://localhost:3306/Z_ORM_BASIC_TEST?useSSL=false&allowPublicKeyRetrieval=True");
        dbInfo.setUsername("root");
        dbInfo.setPassword("Zayar2142000");
        return dbInfo;
    }
}
