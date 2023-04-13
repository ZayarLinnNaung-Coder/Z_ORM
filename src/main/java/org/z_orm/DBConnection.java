package org.z_orm;

import java.util.List;
import java.util.Optional;

public interface DBConnection {
    Transaction getTransaction();

    Object save(Object o);

    <T> List<T> selectAll(Class entityClass);

    Object updateById(Object o, String id);

    <T> Optional<T> findById(Class entityClass, Object primaryKey);

    void deleteById(Class entityClass, String id);
}
