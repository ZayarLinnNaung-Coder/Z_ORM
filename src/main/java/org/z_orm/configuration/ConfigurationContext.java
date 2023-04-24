package org.z_orm.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.z_orm.DBInfo;
import org.z_orm.DDLType;
import org.z_orm.DialectType;

@Getter
@Setter
@ToString
public class ConfigurationContext {
    private DBInfo dbInfo;
    private DialectType dialectType;
    private boolean showSql;
    private DDLType ddlType;
    private String entityPath;

    private static ConfigurationContext instance;

    private ConfigurationContext(){}

    public static ConfigurationContext getInstance(){
        if(instance == null){
            instance = new ConfigurationContext();
        }
        return instance;
    }

}
