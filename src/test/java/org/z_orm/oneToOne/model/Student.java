package org.z_orm.oneToOne.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.z_orm.annotation.Entity;
import org.z_orm.annotation.Id;
import org.z_orm.annotation.JoinColumn;
import org.z_orm.annotation.OneToOne;
import org.z_orm.persistence.CascadeType;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    private Long id;
    private String name;
    private int age;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bag_id", referencedColumnName = "id")
    private Bag bag;
}
