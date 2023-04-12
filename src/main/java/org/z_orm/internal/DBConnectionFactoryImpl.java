package org.z_orm.internal;

import lombok.Builder;
import org.z_orm.*;
import org.z_orm.configuration.ConfigurationContext;
import org.z_orm.exception.InvalidDialectException;
import org.z_orm.logging.logger.ConsoleLogger;
import org.z_orm.logging.logger.Logger;
import org.z_orm.query.AbstractQueryFactory;
import org.z_orm.query.MySQLQueryFactory;
import org.z_orm.query.executer.QueryExecutorService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Builder
public class DBConnectionFactoryImpl implements DBConnectionFactory {

    private final ConfigurationContext configurationContext;
    private final QueryExecutorService queryExecutorService;
    private Connection connection;

    @Override
    public DBConnection getCurrentDBConnection() {
        DBInfo dbInfo = configurationContext.getDbInfo();
        AbstractQueryFactory queryFactory = null;
        QueryExecutorService queryExecutorService = null;

        try {
            logger().info("Initializing Database Connection");
            logger().info("Url - " + dbInfo.getUrl());
            logger().info("Username - " + dbInfo.getUsername());
            logger().info("Password - " + dbInfo.getPassword());

            // get connection from driverManager
            connection = DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUsername(), dbInfo.getPassword());

            queryFactory = createQueryFactory(configurationContext.getDialectType());

            // create query executor service
            queryExecutorService = queryFactory.createQueryExecutorService();
            queryExecutorService.setDdlType(configurationContext.getDdlType());
            queryExecutorService.setConnection(connection);
            queryExecutorService.init();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        } finally {
            logger().info("Connection established...");
        }

        return DBConnectionImpl.builder()
                .configurationContext(configurationContext)
                .queryExecutorService(queryExecutorService)
                .connection(connection)
                .build();
    }

    private AbstractQueryFactory createQueryFactory(DialectType dialectType){
        AbstractQueryFactory queryFactory = null;
        try{
            switch (dialectType){
                case MySQLDialect:
                    queryFactory = new MySQLQueryFactory();
                    break;
                default:
                    throw new InvalidDialectException("No valid dialect is found");
            }
        }catch (InvalidDialectException e){
            e.printStackTrace();
        }
        return queryFactory;
    }

    private static Logger logger(){
        return new ConsoleLogger();
    }
}
