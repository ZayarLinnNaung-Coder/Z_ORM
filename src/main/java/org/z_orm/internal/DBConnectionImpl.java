package org.z_orm.internal;

import lombok.Builder;
import org.z_orm.DBConnection;
import org.z_orm.Transaction;
import org.z_orm.configuration.ConfigurationContext;
import org.z_orm.logging.logger.ConsoleLogger;
import org.z_orm.logging.logger.Logger;
import org.z_orm.query.executer.QueryExecutorService;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

@Builder
public class DBConnectionImpl<T> implements DBConnection {

    private final ConfigurationContext configurationContext;
    private final Connection connection;
    private final QueryExecutorService queryExecutorService;

    @Override
    public Transaction getTransaction() {
        return TransactionImpl.builder()
                .connection(connection)
                .build();
    }

    @Override
    public Object save(Object o) {
        try {
            queryExecutorService.setConnection(connection);
            o = queryExecutorService.save(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    @Override
    public List<T> selectAll(Class entityClass) {
        queryExecutorService.setConnection(connection);
        return queryExecutorService.selectAll(entityClass);
    }

    @Override
    public Object updateById(Object o, String id) {
        queryExecutorService.setConnection(connection);
        queryExecutorService.update(o, id);
        return null;
    }

    @Override
    public <T> Optional<T> findById(Class entityClass, Object primaryKey) {
        queryExecutorService.setConnection(connection);
        return queryExecutorService.findById(entityClass, primaryKey);
    }

    public Logger logger(){
        return new ConsoleLogger();
    }
}
