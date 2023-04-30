package client.appRepoBased.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.z_orm.annotation.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class Teacher implements Serializable {
    @Id
    private Long id;

    private String name;
    private int age;

    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    private List<Student> studentList;
}
