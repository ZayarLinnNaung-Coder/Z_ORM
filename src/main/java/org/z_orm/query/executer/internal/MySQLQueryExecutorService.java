package org.z_orm.query.executer.internal;

import org.z_orm.DDLType;
import org.z_orm.logging.logger.Logger;
import org.z_orm.logging.logger.LoggerFactory;
import org.z_orm.query.executer.QueryExecutorService;
import org.z_orm.query.generator.QueryGenerator;
import org.z_orm.query.generator.internal.MySQLQueryGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLQueryExecutorService extends QueryExecutorService {

    private final QueryGenerator queryGenerator;
    private Logger logger = LoggerFactory.SQLLogger(this.getClass());

    public MySQLQueryExecutorService(){
        this.queryGenerator = new MySQLQueryGenerator();
    }

    @Override
    public void init() {
        initEntities();
    }

    @Override
    public void save(Object o) {
        try {
            Connection connection = getConnection();
            String queryString = queryGenerator.generateSaveQuery(o);
            PreparedStatement stmt = connection.prepareStatement(queryString);
            int successCount = stmt.executeUpdate();
            if (successCount > 0){
                logger.info("Inserted successfully...");
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    private void initEntities() {
        // Init tables
        if(DDLType.CREATE.equals(getDdlType())){
            super.loadAllEntities().forEach(entity -> {
                Connection connection = getConnection();
                dropTable(entity, connection);
                createTable(entity, connection);
            });
        } else if(DDLType.UPDATE.equals(getDdlType())){
            super.loadAllEntities().forEach(entity -> {
                Connection connection = getConnection();
                createTable(entity, connection);
            });
        }
    }

    private void createTable(Class entity, Connection connection) {
        PreparedStatement stmt;
        try {
            String queryString = queryGenerator.generateCreateTableQuery(entity);
            logger.info(queryString);
            stmt = connection.prepareStatement(queryString);
            stmt.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    private void dropTable(Class entity, Connection connection) {
        PreparedStatement stmt;
        try {
            String queryString = queryGenerator.generateDropTableQuery(entity);
            logger.info(queryString);
            stmt = connection.prepareStatement(queryString);
            stmt.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

}
