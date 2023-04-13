package org.z_orm.repository;

import org.z_orm.DBConnection;
import org.z_orm.DBConnectionFactory;
import org.z_orm.configuration.Configuration;
import org.z_orm.configuration.ConfigurationContext;
import org.z_orm.repository.utility.EntityInformation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class AbstractZRepository<T,ID> implements ZRepository<T,ID> {

    private static DBConnection dbConnection;
    private final ConfigurationContext configurationContext;
    private final EntityInformation<T, ID> entityInformation;

    public AbstractZRepository(){
        this.configurationContext = ConfigurationContext.getInstance();
        this.entityInformation = new EntityInformation<>();
        this.entityInformation.setEntityType(generateEntityType());

        if(this.dbConnection == null){
            dbConnection = new Configuration()
                    .configurationContext(configurationContext)
                    .buildDBConnectionFactory().getCurrentDBConnection();
        }
    }

    @Override
    public T save(T entity) {
        if(entityInformation.isNew(entity)){
            dbConnection.save(entity);
        }else{
            String id = String.valueOf(entityInformation.getId(entity));
            dbConnection.updateById(entity, id);
        }
        return entity;
    }

    @Override
    public List<T> findAll() {
        return dbConnection.selectAll(entityInformation.getEntityType());
    }

    @Override
    public Optional<T> findById(ID id) {
        return dbConnection.findById(entityInformation.getEntityType(), id);
    }

    @Override
    public void delete(T entity) {
        String id = String.valueOf(entityInformation.getId(entity));
        dbConnection.deleteById(entity.getClass(), id);
    }

    private Class<T> generateEntityType(){
        Class<T> entityType = null;
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length > 0) {
                entityType = (Class<T>) typeArguments[0];
            }
        }
        return entityType;
    }

}
