package org.z_orm.query.executer.internal;

import com.mysql.cj.util.StringUtils;
import org.z_orm.DDLType;
import org.z_orm.annotation.Column;
import org.z_orm.annotation.Id;
import org.z_orm.logging.logger.Logger;
import org.z_orm.logging.logger.LoggerFactory;
import org.z_orm.query.executer.QueryExecutorService;
import org.z_orm.query.generator.QueryGenerator;
import org.z_orm.query.generator.internal.MySQLQueryGenerator;
import org.z_orm.reflection.ReflectionUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySQLQueryExecutorService extends QueryExecutorService {

    private final QueryGenerator queryGenerator;
    private Logger logger = LoggerFactory.SQLLogger(this.getClass());

    public MySQLQueryExecutorService(){
        this.queryGenerator = new MySQLQueryGenerator();
    }

    @Override
    public void init() {
        initEntities();
    }

    @Override
    public Object save(Object o) {
        try {

            Connection connection = getConnection();
            String queryString = queryGenerator.generateSaveQuery(o);
            PreparedStatement stmt = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
            stmt.execute();

            // get the saved object including 'ID'
            ResultSet resultSet = stmt.getGeneratedKeys();

            while (resultSet.next()){
                for (Field field : o.getClass().getDeclaredFields()) {
                    final Id idAnnotation = field.getAnnotation(Id.class);

                    if(idAnnotation != null){
                        Object fieldValue = getFieldValueFromResultSet(field.getType(), resultSet, "GENERATED_KEY");
                        ReflectionUtils.invokeSetter(o, field.getName(), fieldValue);
                    }
                }
            }

            logger.info("Inserted successfully...");
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return o;
    }

    @Override
    public Object update(Object o, String id) {

        Connection connection = getConnection();
        String queryString = queryGenerator.generateUpdateByIdQuery(o, id);

        return o;
    }

    @Override
    public <T> List<T> selectAll(Class<T> targetEntity) {
        List<T> resultList = new ArrayList<>();

        try{
            Connection connection = getConnection();
            String queryString = queryGenerator.generateSelectAllQuery(targetEntity);
            logger.info(queryString);
            PreparedStatement stmt = connection.prepareStatement(queryString);
            ResultSet resultSet = stmt.executeQuery();

            while(resultSet.next()){
                T obj = getObjectFromResultSet(targetEntity, resultSet);
                resultList.add(obj);
            }

        }catch (SQLException throwable){
            throwable.printStackTrace();
        }

        return resultList;
    }

    @Override
    public <T> Optional<T> findById(Class<T> entityClass, Object primaryKey) {

        T result = null;

        try {
            Connection connection = getConnection();
            String queryString = queryGenerator.generateFindByIdQuery(entityClass, primaryKey);
            logger.info(queryString);
            PreparedStatement stmt = null;

            stmt = connection.prepareStatement(queryString);
            ResultSet resultSet = stmt.executeQuery();
            boolean dataExists = resultSet.next();
            if(dataExists){
                result = getObjectFromResultSet(entityClass, resultSet);
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return Optional.ofNullable(result);
    }

    private <T> T getObjectFromResultSet(Class<T> targetEntity, ResultSet resultSet){

        T obj = null;
        try {
            obj = targetEntity.getDeclaredConstructor().newInstance();

            for (Field field : targetEntity.getDeclaredFields()) {
                final Column columnAnnotation = field.getAnnotation(Column.class);
                String columnName = StringUtils.isNullOrEmpty(columnAnnotation.name()) ? field.getName(): columnAnnotation.name();
                Class<?> fieldType = field.getType();

                Object fieldValue = getFieldValueFromResultSet(fieldType, resultSet, columnName);
                ReflectionUtils.invokeSetter(obj, field.getName(), fieldValue);
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return obj;
    }

    private Object getFieldValueFromResultSet(Class<?> fieldType, ResultSet resultSet, String columnName){

        Object result = null;

        try {
            if(String.class.equals(fieldType)){
                result = resultSet.getString(columnName);
            }else if(Character.class.equals(fieldType) || fieldType.equals("char")){
                result = resultSet.getCharacterStream(columnName);
            }else if(Integer.class.equals(fieldType) || "int".equals(fieldType.getSimpleName())){
                result = resultSet.getInt(columnName);
            }else if("long".equalsIgnoreCase(fieldType.getSimpleName())){
                result = resultSet.getLong(columnName);
            }else if("double".equalsIgnoreCase(fieldType.getSimpleName())){
                result = resultSet.getDouble(columnName);
            }else if("float".equalsIgnoreCase(fieldType.getSimpleName())){
                result = resultSet.getFloat(columnName);
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return result;
    }

    private void initEntities() {
        // Init tables
        if(DDLType.CREATE.equals(getDdlType())){
            super.loadAllEntities().forEach(entity -> {
                Connection connection = getConnection();
                dropTable(entity, connection);
                createTable(entity, connection);
            });
        } else if(DDLType.UPDATE.equals(getDdlType())){
            super.loadAllEntities().forEach(entity -> {
                Connection connection = getConnection();
                createTable(entity, connection);
            });
        }
    }

    private void createTable(Class entity, Connection connection) {
        PreparedStatement stmt;
        try {
            String queryString = queryGenerator.generateCreateTableQuery(entity);
            logger.info(queryString);
            stmt = connection.prepareStatement(queryString);
            stmt.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    private void dropTable(Class entity, Connection connection) {
        PreparedStatement stmt;
        try {
            String queryString = queryGenerator.generateDropTableQuery(entity);
            logger.info(queryString);
            stmt = connection.prepareStatement(queryString);
            stmt.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

}
