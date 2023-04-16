package org.z_orm.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ConstraintInfo {
    private String tableName;
    private String columnName;
    private String referencedTableName;
    private String referencedColumnName;
    private String constraintName;
}

