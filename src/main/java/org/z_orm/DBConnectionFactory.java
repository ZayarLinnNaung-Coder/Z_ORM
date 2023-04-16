package org.z_orm;

import org.z_orm.query.executer.QueryExecutorService;

public interface DBConnectionFactory {
    void init();
    DBConnection getDBConnection();
    QueryExecutorService createNewQueryExecutorService();
}
