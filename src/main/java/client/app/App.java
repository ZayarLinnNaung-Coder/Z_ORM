package client.app;

import client.app.model.Student;
import client.app.model.Teacher;
import org.z_orm.*;
import org.z_orm.configuration.Configuration;
import org.z_orm.configuration.ConfigurationContext;

import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConfigurationContext configurationContext = getConfigurationContext();

        DBConnectionFactory connectionFactory = new Configuration()
                .configurationContext(configurationContext)
                .buildDBConnectionFactory();

        DBConnection dbConnection = connectionFactory.getDBConnection();

        String ans;
        do{
            System.out.println("Select options: ");
            System.out.println("\t s: Save");
            System.out.println("\t a: SelectAll");
            ans = scanner.next();

            switch (ans){
                case "s":
                    save(dbConnection);
                    break;
                case "a":
                    selectAll(dbConnection);
                case "u":
                    updateById(dbConnection);
                    break;
            }
        }while (!"q:".equals(ans));

    }

    private static void save(DBConnection dbConnection) {
        Student student = new Student();
        student.setFirstName("Zayar");
        student.setLastName("Linn Naung");
        student.setEmail("");
        student = (Student) dbConnection.save(student);
        System.out.println(student);

        Teacher teacher = new Teacher();
        teacher.setName("Zayar Linn Naung");
        teacher.setAge(50);
        teacher = (Teacher) dbConnection.save(teacher);
        System.out.println(teacher);
    }

    private static void selectAll(DBConnection dbConnection) {
        List<Student> studentList = dbConnection.selectAll(Student.class);
        studentList.forEach(student -> {
            System.out.println(student);
        });

        List<Teacher> teacherList = dbConnection.selectAll(Teacher.class);
        teacherList.forEach(teacher -> {
            System.out.println(teacher);
        });
    }

    private static void updateById(DBConnection dbConnection) {

        Student student = new Student();
        student.setFirstName("Zayar");
        student.setLastName("Linn Naung");
        student.setEmail("");

        dbConnection.updateById(student, "5");
    }

    private static ConfigurationContext getConfigurationContext() {
        ConfigurationContext configurationContext = ConfigurationContext.getInstance();
//        configurationContext.setShowSql(true);
        configurationContext.setDdlType(DDLType.UPDATE);
        configurationContext.setDialectType(DialectType.MySQLDialect);
        configurationContext.setDbInfo(getDBInfo());

        return configurationContext;
    }

    private static DBInfo getDBInfo(){
        DBInfo dbInfo = new DBInfo();
        dbInfo.setDriverClass("com.mysql.cj.jdbc.Driver");
        dbInfo.setUrl("jdbc:mysql://localhost:3306/Z_ORM?useSSL=false&amp");
        dbInfo.setUsername("root");
        dbInfo.setPassword("Zayar2142000");
        return dbInfo;
    }
}
