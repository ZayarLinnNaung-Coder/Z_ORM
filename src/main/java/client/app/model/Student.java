package client.app.model;

import lombok.Getter;
import lombok.Setter;
import org.z_orm.annotation.Column;
import org.z_orm.annotation.Entity;
import org.z_orm.annotation.Id;

import java.io.Serializable;

@Entity
@Getter
@Setter
public class Student implements Serializable {
    @Id
    private Long id;

    @Column(name = "first_name")
    private String firstName;
    private String lastName;

    @Column(unique = true, length = 1000)
    private String email;
    private int age;
}
