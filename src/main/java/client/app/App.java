package client.app;

import client.app.model.Student;
import client.app.model.Teacher;
import org.z_orm.*;
import org.z_orm.configuration.Configuration;
import org.z_orm.configuration.ConfigurationContext;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConfigurationContext configurationContext = getConfigurationContext();

        DBConnectionFactory connectionFactory = new Configuration()
                .configurationContext(configurationContext)
                .buildDBConnectionFactory();

        DBConnection dbConnection = connectionFactory.getCurrentDBConnection();

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
                    break;
            }
        }while (!"q:".equals(ans));


    }

    private static void save(DBConnection dbConnection) {
        Student student = new Student();
        student.setFirstName("Zayar");
        student.setLastName("Linn Naung");
        student.setEmail("");
        dbConnection.save(student);

        Teacher teacher = new Teacher();
        teacher.setName("Zayar Linn Naung");
        teacher.setAge(50);
        dbConnection.save(teacher);
    }

    private static void selectAll(DBConnection dbConnection) {
        System.out.println("SELECTING ALL");
        dbConnection.selectAll(Student.class);
        dbConnection.selectAll(Teacher.class);
    }

    private static ConfigurationContext getConfigurationContext() {
        ConfigurationContext configurationContext = ConfigurationContext.getInstance();
//        configurationContext.setShowSql(true);
        configurationContext.setDdlType(DDLType.CREATE);
        configurationContext.setDialectType(DialectType.MySQLDialect);
        configurationContext.setDbInfo(getDBInfo());

        return configurationContext;
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
