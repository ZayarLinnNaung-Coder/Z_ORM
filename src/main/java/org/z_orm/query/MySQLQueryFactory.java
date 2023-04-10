package org.z_orm.query;

import org.z_orm.query.executer.QueryExecutorService;
import org.z_orm.query.executer.internal.MySQLQueryExecutorService;

public class MySQLQueryFactory extends AbstractQueryFactory {
    @Override
    public QueryExecutorService createQueryExecutorService() {
        return new MySQLQueryExecutorService();
    }
}
