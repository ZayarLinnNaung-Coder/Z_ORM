package client.appRepoBased.repository;

import client.appRepoBased.model.Student;
import org.z_orm.repository.AbstractZRepository;

public class StudentRepository extends AbstractZRepository<Student, Long> {
    public StudentRepository(Class<Student> entityType) {
        super(entityType);
    }
}
