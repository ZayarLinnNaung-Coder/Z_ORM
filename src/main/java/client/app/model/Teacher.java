package client.app.model;

import lombok.Getter;
import lombok.Setter;
import org.z_orm.annotation.Entity;
import org.z_orm.annotation.Id;

import java.io.Serializable;

@Entity
@Getter
@Setter
public class Teacher implements Serializable {
    @Id
    private Long id;
    private String name;
    private int age;
}
