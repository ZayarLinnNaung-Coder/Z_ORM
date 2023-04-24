package org.z_orm.oneToOne.model;

import lombok.*;
import org.z_orm.annotation.Entity;
import org.z_orm.annotation.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bag {
    @Id
    private Long id;
    private String name;
    private String color;
}
