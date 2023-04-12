package org.z_orm.repository;

import org.z_orm.DBConnection;
import org.z_orm.DBConnectionFactory;
import org.z_orm.configuration.Configuration;
import org.z_orm.configuration.ConfigurationContext;
import org.z_orm.repository.utility.EntityInformation;

import java.util.List;
import java.util.Optional;

public class AbstractZRepository<T,ID> implements ZRepository<T,ID> {

    private final DBConnection dbConnection;
    private final ConfigurationContext configurationContext;
    private final EntityInformation<T, ID> entityInformation;

    public AbstractZRepository(Class<T> entityType){
        this.configurationContext = ConfigurationContext.getInstance();
        this.entityInformation = new EntityInformation<>(entityType);

        DBConnectionFactory connectionFactory = new Configuration()
                .configurationContext(configurationContext)
                .buildDBConnectionFactory();

        dbConnection = connectionFactory.getCurrentDBConnection();
    }

    @Override
    public <S extends T> S save(S entity) {
        if(entityInformation.isNew(entity)){
            dbConnection.save(entity);
        }else{
            System.out.println("-> Updating");
        }
        return entity;
    }

    @Override
    public <S extends T> List<S> findAll() {
        return dbConnection.selectAll(entityInformation.getEntityType());
    }

    @Override
    public Optional<T> findById(ID id) {
        return dbConnection.findById(entityInformation.getEntityType(), id);
    }

}
