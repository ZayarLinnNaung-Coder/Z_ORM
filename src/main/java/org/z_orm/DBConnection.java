package org.z_orm;

import java.io.Serializable;
import java.util.List;

public interface DBConnection {
    Transaction getTransaction();
    Serializable save(Object o);
    <T> List<T> selectAll(Class<T> entityClass);
}
