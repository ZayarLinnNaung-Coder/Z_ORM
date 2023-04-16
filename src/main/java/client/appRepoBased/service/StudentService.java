package client.appRepoBased.service;

import client.appRepoBased.repository.StudentRepository;

public class StudentService {

    private StudentRepository studentRepository;

    public StudentService(){
        studentRepository = new StudentRepository();
    }
}
