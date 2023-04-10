package org.z_orm.query.generator.internal;

import java.util.HashMap;
import java.util.Map;

import static org.z_orm.query.generator.internal.MySQLDataType.*;

public class MySQLUtils {
    public static Map<String, MySQLDataType> dataTypeMap(){
        Map<String, MySQLDataType> dataTypeMap = new HashMap<>();
        dataTypeMap.put("character", CHAR);
        dataTypeMap.put("string", NVARCHAR);
        dataTypeMap.put("boolean", BOOL);
        dataTypeMap.put("Integer", INT);
        dataTypeMap.put("int", INT);
        dataTypeMap.put("long", BIGINT);
        dataTypeMap.put("float", FLOAT);
        dataTypeMap.put("double", DOUBLE);

        return dataTypeMap;
    }
}
