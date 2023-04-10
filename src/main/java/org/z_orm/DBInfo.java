package org.z_orm;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class DBInfo {
    private String driverClass;
    private String url;
    private String username;
    private String password;
}
