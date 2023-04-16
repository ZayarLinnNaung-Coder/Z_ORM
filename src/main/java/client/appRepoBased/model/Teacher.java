package client.appRepoBased.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.z_orm.annotation.Entity;
import org.z_orm.annotation.Id;
import org.z_orm.annotation.JoinColumn;
import org.z_orm.annotation.OneToOne;
import org.z_orm.persistence.CascadeType;

import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
public class Teacher implements Serializable {
    @Id
    private Long id;
    private String name;
    private int age;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private Student student;
}
