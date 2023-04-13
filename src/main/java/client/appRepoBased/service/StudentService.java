package client.appRepoBased.service;

import client.appRepoBased.model.Student;
import client.appRepoBased.repository.StudentRepository;

import java.util.List;

public class StudentService {
    private StudentRepository studentRepository;

    public StudentService(){
        studentRepository = new StudentRepository();
    }

    public void doCustomTransaction(Student student){
        try{
            Student std = studentRepository.save(student);
            if(std != null){
                throw new RuntimeException("Unknown error occur");
            }

            // update email
            std.setEmail("zayarlinnnaung@gmail.com");
            studentRepository.save(std);

            System.out.println(std);

            List<Student> studentList = studentRepository.findAll();
            studentList.forEach(System.out::println);

        }catch (RuntimeException e){
            e.printStackTrace();
        }

    }
}
