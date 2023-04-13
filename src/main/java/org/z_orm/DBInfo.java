package org.z_orm;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DBInfo {
    private String driverClass;
    private String url;
    private String username;
    private String password;
}
