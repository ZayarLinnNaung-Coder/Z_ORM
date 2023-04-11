package org.z_orm.query.executer;

import lombok.Getter;
import lombok.Setter;
import org.z_orm.DDLType;
import org.z_orm.internal.EntityManager;

import java.sql.Connection;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public abstract class QueryExecutorService {

    private DDLType ddlType;
    private Connection connection;

    public abstract void init();

    public abstract void save(Object o);

    public Set<Class> loadAllEntities(){
        return new EntityManager().loadAllEntities();
    }

    public abstract <T> List<T> selectAll(Class<T> targetEntity);
}
