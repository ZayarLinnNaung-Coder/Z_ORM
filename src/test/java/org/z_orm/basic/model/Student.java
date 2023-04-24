package org.z_orm.basic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.z_orm.annotation.Entity;
import org.z_orm.annotation.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    private Long id;
    private String name;
    private int age;
}
