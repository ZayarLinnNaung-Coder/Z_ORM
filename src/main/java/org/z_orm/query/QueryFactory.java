package org.z_orm.query;

import org.z_orm.query.executer.QueryExecutorService;

public interface QueryFactory {
    QueryExecutorService createQueryExecutorService();
}
