package org.z_orm;

public interface Transaction {
    void commit();

    void rollback();
}
