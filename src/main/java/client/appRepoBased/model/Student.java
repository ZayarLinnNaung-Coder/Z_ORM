package client.appRepoBased.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.z_orm.annotation.*;
import org.z_orm.persistence.CascadeType;
import org.z_orm.persistence.FetchType;

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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    private Teacher teacher;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bag_id", referencedColumnName = "id")
    private Bag bag;
}
