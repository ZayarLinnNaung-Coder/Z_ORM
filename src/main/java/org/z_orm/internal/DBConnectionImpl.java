package org.z_orm.internal;

import lombok.Builder;
import org.z_orm.DBConnection;
import org.z_orm.Transaction;
import org.z_orm.configuration.ConfigurationContext;
import org.z_orm.logging.logger.ConsoleLogger;
import org.z_orm.logging.logger.Logger;
import org.z_orm.query.executer.QueryExecutorService;

import java.io.Serializable;
import java.sql.Connection;

@Builder
public class DBConnectionImpl implements DBConnection {

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
    public Serializable save(Object o) {
        try {
            queryExecutorService.setConnection(connection);
            queryExecutorService.save(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Serializable selectAll(Class targetEntity) {
        return null;
    }

    public Logger logger(){
        return new ConsoleLogger();
    }
}
