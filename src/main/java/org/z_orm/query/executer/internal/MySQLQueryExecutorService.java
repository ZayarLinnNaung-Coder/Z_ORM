package org.z_orm.query.executer.internal;

import com.mysql.cj.util.StringUtils;
import org.z_orm.DBConnection;
import org.z_orm.DDLType;
import org.z_orm.annotation.Column;
import org.z_orm.annotation.Id;
import org.z_orm.annotation.JoinColumn;
import org.z_orm.annotation.OneToOne;
import org.z_orm.logging.logger.Logger;
import org.z_orm.logging.logger.LoggerFactory;
import org.z_orm.persistence.ConstraintInfo;
import org.z_orm.query.executer.QueryExecutorService;
import org.z_orm.query.generator.QueryGenerator;
import org.z_orm.query.generator.internal.MySQLQueryGenerator;
import org.z_orm.query.generator.internal.MySQLUtils;
import org.z_orm.reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
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
            resolveRelationshipData(o);
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
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return o;
    }

    private void resolveRelationshipData(Object o) {

        Object idValue = null;

        for (Field field : o.getClass().getDeclaredFields()) {

            if(field.getAnnotation(OneToOne.class) != null){
                Object relationshipObject = ReflectionUtils.invokeGetter(o, field.getName());
                for (Field declaredField : relationshipObject.getClass().getDeclaredFields()) {
                    if(declaredField.getAnnotation(Id.class) != null){
                        idValue = ReflectionUtils.invokeGetter(relationshipObject, declaredField.getName());
                    }
                }

                if(idValue != null){
                    updateById(relationshipObject, idValue.toString());
                }else {
                    save(relationshipObject);
                }
            }

        }
    }

    @Override
    public Object updateById(Object o, String id) {
        String queryString = queryGenerator.generateUpdateByIdQuery(o, id);
        logger.info(queryString);

        try {
            PreparedStatement stmt = connection.prepareStatement(queryString);
            stmt.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return o;
    }

    @Override
    public <T> List<T> selectAll(Class<T> targetEntity) {
        List<T> resultList = new ArrayList<>();

        try{
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
            String queryString = queryGenerator.generateFindByIdQuery(entityClass, primaryKey);
            logger.info(queryString);

            PreparedStatement stmt = connection.prepareStatement(queryString);
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

    @Override
    public void deleteById(Class entityClass, String id) {
        String queryString = queryGenerator.generateDeleteByIdQuery(entityClass, id);
        logger.info(queryString);

        try {
            PreparedStatement stmt = connection.prepareStatement(queryString);
            stmt.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

    }

    private <T> T getObjectFromResultSet(Class<T> targetEntity, ResultSet resultSet){

        T obj = null;
        try {
            obj = targetEntity.getDeclaredConstructor().newInstance();

            for (Field field : targetEntity.getDeclaredFields()) {
                String columnName = "";
                Class<?> fieldType = field.getType();
                final Column columnAnnotation = field.getAnnotation(Column.class);

                if(columnAnnotation != null){
                    columnName  = StringUtils.isNullOrEmpty(columnAnnotation.name()) ? field.getName(): columnAnnotation.name();
                }else{
                    JoinColumn joinColumnAnnotation = field.getAnnotation(JoinColumn.class);
                    columnName = joinColumnAnnotation.name();
                }

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

            if(MySQLUtils.dataTypeMap().containsKey(fieldType.getSimpleName().toLowerCase())){
                result = resultSet.getObject(columnName);
            }else{
                // considered as the relationship field
                Object o = resultSet.getObject(columnName);
                result = findById(fieldType, o).get();
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return result;
    }

    private void initEntities() {
        if(DDLType.CREATE.equals(getDdlType())){
            dropAllEntityTables();
        }

        // Init tables
        createAllEntityTables();
        resolveRelationshipConstraints();
    }

    private void dropAllEntityTables(){
        super.loadAllEntities().forEach(entity -> {
            dropAllConstraints(entity);
            dropTable(entity, connection);
        });
    }

    private void dropAllConstraints(Class entity) {
        List<ConstraintInfo> constraintInfoList = getAllConstraintsFromTable(entity.getSimpleName());
        constraintInfoList.forEach(constraintInfo -> {
            String constraintDropQuery = queryGenerator.generateDropConstraintQuery(constraintInfo.getTableName(), constraintInfo.getConstraintName());
            logger.info(constraintDropQuery);
            try {
                connection.prepareStatement(constraintDropQuery).executeUpdate();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void createAllEntityTables(){
        super.loadAllEntities().forEach(entity -> {
            createTable(entity, connection);
        });
    }

    private void resolveRelationshipConstraints() {
        super.loadAllEntities().forEach(entity -> {
            Arrays.stream(entity.getDeclaredFields()).filter(field -> field.getAnnotation(OneToOne.class) != null).forEach(field -> {
                addFKConstraint(entity, field);
            });
        });
    }

    private void addFKConstraint(Class entity, Field field) {
        PreparedStatement stmt;

        try {
            String queryString = queryGenerator.generateAddFKConstraintQuery(entity, field);
            logger.info(queryString);
            stmt = connection.prepareStatement(queryString);
            stmt.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
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
            System.exit(0);
        }
    }

    public List<ConstraintInfo> getAllConstraintsFromTable(String tableName) {
        List<ConstraintInfo> constraintInfoList = new ArrayList<>();
        PreparedStatement stmt;
        try {
            String databaseName = connection.getCatalog();
            String queryString = queryGenerator.generateGetAllConstraintsFromTableQuery(databaseName, tableName);
            stmt = connection.prepareStatement(queryString);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()){
                String table_name = resultSet.getString("TABLE_NAME");
                String column_name = resultSet.getString("COLUMN_NAME");
                String referenced_table_name = resultSet.getString("REFERENCED_TABLE_NAME");
                String referenced_column_name = resultSet.getString("REFERENCED_COLUMN_NAME");
                String constraint_name = resultSet.getString("CONSTRAINT_NAME");

                ConstraintInfo constraintInfo = new ConstraintInfo();
                constraintInfo.setTableName(table_name);
                constraintInfo.setColumnName(column_name);
                constraintInfo.setReferencedTableName(referenced_table_name);
                constraintInfo.setReferencedColumnName(referenced_column_name);
                constraintInfo.setConstraintName(constraint_name);
                constraintInfoList.add(constraintInfo);
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return constraintInfoList;
    }

}
