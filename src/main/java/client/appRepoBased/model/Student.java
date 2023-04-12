package client.appRepoBased.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.z_orm.annotation.Column;
import org.z_orm.annotation.Entity;
import org.z_orm.annotation.Id;

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
}
