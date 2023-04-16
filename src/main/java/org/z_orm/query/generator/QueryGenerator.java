package org.z_orm.query.generator;

import java.lang.reflect.Field;

public interface QueryGenerator {
    String generateSaveQuery(Object o);

    String generateUpdateByIdQuery(Object o, String id);

    String generateCreateTableQuery(Class entityClass);

    String generateDropTableQuery(Class entityClass);

    String generateSelectAllQuery(Class targetEntity);

    String generateFindByIdQuery(Class<?> entityClass, Object primaryKey);

    String generateDeleteByIdQuery(Class targetEntity, String id);

    String generateAddFKConstraintQuery(Class entity, Field field);

    String generateGetAllConstraintsFromTableQuery(String databaseName, String tableName);

    String generateDropConstraintQuery(String tableName, String constraintName);
}
