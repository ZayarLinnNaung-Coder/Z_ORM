package client.appRepoBased.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.z_orm.annotation.Entity;
import org.z_orm.annotation.Id;

@Entity
@Getter
@Setter
@ToString
public class Bag {
    @Id
    private Long id;

    private String color;
    private String type;
    private String price;
}
