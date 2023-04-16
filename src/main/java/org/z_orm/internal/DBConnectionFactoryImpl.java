package org.z_orm.internal;

import lombok.Builder;
import lombok.Getter;
import org.z_orm.*;
import org.z_orm.configuration.ConfigurationContext;
import org.z_orm.exception.InvalidDialectException;
import org.z_orm.logging.logger.ConsoleLogger;
import org.z_orm.logging.logger.Logger;
import org.z_orm.query.QueryFactory;
import org.z_orm.query.MySQLQueryFactory;
import org.z_orm.query.executer.QueryExecutorService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

@Builder
@Getter
public class DBConnectionFactoryImpl implements DBConnectionFactory {

    private final ConfigurationContext configurationContext;
    private static DBInfo dbInfo;
    private Connection connection;
    private QueryFactory queryFactory;
    private QueryExecutorService queryExecutorService;

    @Override
    public void init() {
            dbInfo = configurationContext.getDbInfo();

            logger().info("Initializing Database Connection");
            logger().info("Url - " + dbInfo.getUrl());
            logger().info("Username - " + dbInfo.getUsername());
            logger().info("Password - " + dbInfo.getPassword());

            queryFactory = createQueryFactory(configurationContext.getDialectType());
    }

    @Override
    public QueryExecutorService createNewQueryExecutorService(){
        try {
            // create query executor service
            queryExecutorService = queryFactory.createQueryExecutorService();
            queryExecutorService.setDdlType(configurationContext.getDdlType());
            queryFactory = createQueryFactory(configurationContext.getDialectType());
            connection = DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUsername(), dbInfo.getPassword());

            queryExecutorService.setConnection(connection);

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return queryExecutorService;
    }

    @Override
    public DBConnection getDBConnection() {

        try {
            // get connection from driverManager
            connection = DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUsername(), dbInfo.getPassword());

            queryExecutorService.setConnection(connection);
            queryExecutorService.setConnectionUUID(UUID.randomUUID().toString());

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return DBConnectionImpl.builder()
                .configurationContext(configurationContext)
                .queryExecutorService(queryExecutorService)
                .connection(connection)
                .build();
    }

    private QueryFactory createQueryFactory(DialectType dialectType){
        QueryFactory queryFactory = null;
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
