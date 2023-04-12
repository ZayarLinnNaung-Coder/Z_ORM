package org.z_orm.configuration;

import org.z_orm.DBConnectionFactory;
import org.z_orm.internal.DBConnectionFactoryImpl;

public class Configuration {

    private ConfigurationContext configurationContext;

    public Configuration(){
    }

    public Configuration configurationContext(ConfigurationContext configurationContext){
        this.configurationContext = configurationContext;
        return this;
    }

    public DBConnectionFactory buildDBConnectionFactory(){
        return DBConnectionFactoryImpl
                .builder().configurationContext(configurationContext).build();
    }
}
