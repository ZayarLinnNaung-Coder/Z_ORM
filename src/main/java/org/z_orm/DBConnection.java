package org.z_orm;

import java.io.Serializable;

public interface DBConnection {
    Transaction getTransaction();
    Serializable save(Object o);
    Serializable selectAll(Class targetEntity);
}
