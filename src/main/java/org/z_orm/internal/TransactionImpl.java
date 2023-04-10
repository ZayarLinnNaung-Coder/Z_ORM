package org.z_orm.internal;

import lombok.Builder;
import org.z_orm.Transaction;

import java.sql.Connection;

@Builder
public class TransactionImpl implements Transaction {

    private Connection connection;

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }
}
