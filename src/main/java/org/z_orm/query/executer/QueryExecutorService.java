package org.z_orm.query.executer;

import lombok.Getter;
import lombok.Setter;
import org.z_orm.DDLType;
import org.z_orm.internal.EntityManager;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
public abstract class QueryExecutorService {

    private DDLType ddlType;
    protected Connection connection;

    public Set<Class> loadAllEntities(){
        return new EntityManager().loadAllEntities();
    }

    public abstract void init();

    public abstract Object save(Object o);

    public abstract Object updateById(Object o, String id);

    public abstract <T> List<T> selectAll(Class<T> targetEntity);

    public abstract <T> Optional<T> findById(Class<T> targetEntity, Object primaryKey);

    public abstract void deleteById(Class targetEntity, String id);
}
