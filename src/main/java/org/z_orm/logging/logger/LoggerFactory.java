package org.z_orm.logging.logger;

public class LoggerFactory {
    public static Logger SQLLogger(Class c){
        return new SQLLogger(c);
    }
}
