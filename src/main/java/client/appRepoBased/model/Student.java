package client.appRepoBased.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.z_orm.annotation.*;
import org.z_orm.persistence.CascadeType;

@Entity
@Getter
@Setter
@ToString
public class Student {
    @Id
    private Long id;

    @Column(name = "first_name")
    private String firstName;
    private String lastName;
    private String email;
    private int age;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    private Teacher teacher;
}
