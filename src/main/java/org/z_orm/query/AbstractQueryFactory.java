package org.z_orm.query;

import org.z_orm.query.executer.QueryExecutorService;

public abstract class AbstractQueryFactory {
    public abstract QueryExecutorService createQueryExecutorService();
}
