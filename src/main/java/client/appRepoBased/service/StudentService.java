package client.appRepoBased.service;

import client.appRepoBased.model.Student;
import client.appRepoBased.repository.StudentRepository;
import org.z_orm.annotation.Transactional;

public class StudentService {

    private StudentRepository studentRepository;

    public StudentService(){
        studentRepository = new StudentRepository();
    }

    @Transactional
    public void doCustomTransaction(Student student){
        try{
//            Student std = studentRepository.save(student);
//
//            // update email
//            std.setEmail("zayarlinnnaung@gmail.com");
//            studentRepository.save(std);
//
//            System.out.println(std);
//
//            List<Student> studentList = studentRepository.findAll();
//            studentList.forEach(System.out::println);

            System.out.println("Doing transaction");

        }catch (RuntimeException e){
            e.printStackTrace();
        }

    }
}
