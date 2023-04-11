package org.z_orm.query.executer.internal;

import com.mysql.cj.util.StringUtils;
import org.z_orm.DDLType;
import org.z_orm.annotation.Column;
import org.z_orm.logging.logger.Logger;
import org.z_orm.logging.logger.LoggerFactory;
import org.z_orm.query.executer.QueryExecutorService;
import org.z_orm.query.generator.QueryGenerator;
import org.z_orm.query.generator.internal.MySQLQueryGenerator;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    public void save(Object o) {
        try {

            Connection connection = getConnection();
            String queryString = queryGenerator.generateSaveQuery(o);
            PreparedStatement stmt = connection.prepareStatement(queryString);
            int successCount = stmt.executeUpdate();
            if (successCount > 0){
                logger.info("Inserted successfully...");
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public <T> List<T> selectAll(Class<T> targetEntity) {
        List<T> resultList = new ArrayList<>();

        try {
            Connection connection = getConnection();
            String queryString = queryGenerator.generateSelectAllQuery(targetEntity);
            PreparedStatement stmt = connection.prepareStatement(queryString);
            ResultSet resultSet = stmt.executeQuery();

            while(resultSet.next()){

                T obj = targetEntity.getDeclaredConstructor().newInstance();

                targetEntity.getDeclaredMethods();
                for (Field field : targetEntity.getDeclaredFields()) {
                    final Column columnAnnotation = field.getAnnotation(Column.class);
                    String columnName = StringUtils.isNullOrEmpty(columnAnnotation.name()) ? field.getName(): columnAnnotation.name();
                    Class<?> fieldType = field.getType();

                    if(String.class.equals(fieldType)){
                        invokeSetter(obj, field.getName(), resultSet.getString(columnName));
                    }else if(Character.class.equals(fieldType) || fieldType.equals("char")){
                        invokeSetter(obj, field.getName(), resultSet.getCharacterStream(columnName));
                    }else if(Integer.class.equals(fieldType) || "int".equals(fieldType.getSimpleName())){
                        invokeSetter(obj, field.getName(), resultSet.getInt(columnName));
                    }else if("long".equalsIgnoreCase(fieldType.getSimpleName())){
                        invokeSetter(obj, field.getName(), resultSet.getLong(columnName));
                    }else if("double".equalsIgnoreCase(fieldType.getSimpleName())){
                        invokeSetter(obj, field.getName(), resultSet.getDouble(columnName));
                    }else if("float".equalsIgnoreCase(fieldType.getSimpleName())){
                        invokeSetter(obj, field.getName(), resultSet.getFloat(columnName));
                    }
                }

                resultList.add(obj);
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return resultList;
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

    private static void invokeSetter(Object obj, String propertyName, Object variableValue)
    {
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(propertyName, obj.getClass());
            Method setter = pd.getWriteMethod();
            try {
                setter.invoke(obj,variableValue);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

    }

}
