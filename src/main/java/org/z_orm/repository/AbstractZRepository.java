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

    private DBConnection dbConnection;
    private static ConfigurationContext configurationContext;
    private static DBConnectionFactory dbConnectionFactory;
    private EntityInformation<T, ID> entityInformation;
    private static boolean isInitState = true;

    public AbstractZRepository(){

        // do only one time like Table Creation, ...
        if(isInitState){
            configurationContext = ConfigurationContext.getInstance();

            dbConnectionFactory = new Configuration()
                    .configurationContext(configurationContext)
                    .buildDBConnectionFactory();
            dbConnectionFactory.init();
            dbConnectionFactory.createNewQueryExecutorService().init();

            isInitState = false;
        }

        entityInformation = new EntityInformation<>();
        entityInformation.setEntityType(generateEntityType());

        dbConnectionFactory.createNewQueryExecutorService();
        dbConnection = dbConnectionFactory.getDBConnection();
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

    private void setEntityInformation(EntityInformation<T, ID> entityInformation){
        this.entityInformation = entityInformation;
    }

    private EntityInformation<T, ID> getEntityInformation(){
        return this.entityInformation;
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
