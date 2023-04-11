package org.z_orm.query.generator;

public interface QueryGenerator {
    String generateSaveQuery(Object o);
    String generateCreateTableQuery(Class entityClass);
    String generateDropTableQuery(Class entityClass);
    String generateSelectAllQuery(Class targetEntity);
}
