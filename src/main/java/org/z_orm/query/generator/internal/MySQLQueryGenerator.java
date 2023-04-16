package org.z_orm.query.generator.internal;

import client.appRepoBased.model.Student;
import client.appRepoBased.model.Teacher;
import com.mysql.cj.util.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.z_orm.annotation.*;
import org.z_orm.exception.NotKnownDataTypeException;
import org.z_orm.logging.logger.Logger;
import org.z_orm.logging.logger.LoggerFactory;
import org.z_orm.query.generator.QueryGenerator;
import org.z_orm.query.generator.SQLConstraintType;
import org.z_orm.reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

import static org.z_orm.query.generator.SQLConstraintType.*;

public class MySQLQueryGenerator implements QueryGenerator {

    private Logger logger = LoggerFactory.SQLLogger(MySQLQueryGenerator.class);

    @Override
    public String generateSaveQuery(Object o) {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append("INSERT INTO ");
            builder.append(o.getClass().getSimpleName());
            builder.append("(");

            concatAllColumns(o.getClass().getDeclaredFields(), builder);

            builder.append(") ");
            builder.append("VALUES (");
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object fieldValue = field.get(o);
                builder.append(String.class.equals(field.getType()) && fieldValue != null ? "'" : "");
                builder.append(fieldValue);
                builder.append(String.class.equals(field.getType()) && fieldValue != null  ? "'" : "");
                builder.append(",");
            }
            builder.replace(builder.length() - 1, builder.length(), "");

            builder.append("); ");
            logger.info(builder.toString());
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
        return builder.toString();
    }

    @Override
    public String generateUpdateByIdQuery(Object o, String id) {

        StringBuilder builder = new StringBuilder();

        builder.append("UPDATE ");
        builder.append(o.getClass().getSimpleName());
        builder.append(" SET ");

        for (Field field : o.getClass().getDeclaredFields()) {
            builder.append(getColumnName(field));
            builder.append(" = ");
            Object value = ReflectionUtils.invokeGetter(o, field.getName());

            // if value is string: 'Zayar Linn Naung'
            String appendQuote = String.class.equals(value.getClass())? "'": "";
            builder.append(appendQuote);
            builder.append(value);
            builder.append(appendQuote);
            builder.append(",");
        }
        builder.replace(builder.length() - 1, builder.length(), "");

        builder.append(" WHERE ");
        builder.append("id = ");
        builder.append(id);

        return builder.toString();
    }

    @Override
    public String generateCreateTableQuery(Class entityClass) {
        String dataTypeName;

        // Use LinkedHashMap for insertion order while HashMap does not give guaranty
        Map<SQLConstraintType, Set<Field>> constraintTypeFieldMap = new LinkedHashMap<>();
        Set<Field> uniqueConstraintFields = new HashSet<>();

        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append(entityClass.getSimpleName());
        builder.append("(");

        for (Field field : entityClass.getDeclaredFields()) {
            Id idAnnotation = field.getAnnotation(Id.class);
            Column columnAnnotation = field.getAnnotation(Column.class);

            if(columnAnnotation != null) {
                // Process @Column(name)
                if (StringUtils.isNullOrEmpty(columnAnnotation.name())) {
                    builder.append(field.getName());
                } else {
                    builder.append(columnAnnotation.name());
                }
            } else{
                // considered as the relationship column
                OneToOne oneToOneAnnotation = field.getAnnotation(OneToOne.class);
                if(oneToOneAnnotation != null){
                    JoinColumn joinColumnAnnotation = field.getAnnotation(JoinColumn.class);
                    if(joinColumnAnnotation != null){
                        builder.append(joinColumnAnnotation.name());
                    }
                }
            }

            // Process column data type
            dataTypeName = field.getType().getSimpleName().toLowerCase();
            if(MySQLUtils.dataTypeMap().containsKey(dataTypeName)) {
                // Concat dataTypes
                final MySQLDataType mySQLDataType = MySQLUtils.dataTypeMap().get(dataTypeName);
                builder.append(" ");
                builder.append(mySQLDataType);

                if(MySQLDataType.CHAR.equals(mySQLDataType) || MySQLDataType.NVARCHAR.equals(mySQLDataType)){
                    builder.append("(");
                    builder.append(columnAnnotation.length());
                    builder.append(")");
                }
            } else{
                // May be relationShip with other entities
                try {

                    OneToOne oneToOneAnnotation = field.getAnnotation(OneToOne.class);
                    JoinColumn joinColumnAnnotation = field.getAnnotation(JoinColumn.class);
                    Class<?> relationshipClass = field.getType();

                    dataTypeName = ReflectionUtils.getType(relationshipClass, "id").getSimpleName().toLowerCase();
                    if(MySQLUtils.dataTypeMap().containsKey(dataTypeName)){
                        final MySQLDataType mySQLDataType = MySQLUtils.dataTypeMap().get(dataTypeName);
                        builder.append(" ");
                        builder.append(mySQLDataType);
                    }else{
                        throw new NotKnownDataTypeException(dataTypeName + " is not a known dataType");
                    }

                } catch (NotKnownDataTypeException e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }

            if(columnAnnotation != null){
                // Process @Column(nullable)
                if (!columnAnnotation.nullable() || idAnnotation != null){
                    builder.append(" NOT NULL");
                }else{
                    builder.append(" NULL");
                }
            }

            // Process @Id
            if(idAnnotation != null){
                builder.append(" AUTO_INCREMENT");
                constraintTypeFieldMap.put(PRIMARY_KEY, Set.of(field));
            }

            if(columnAnnotation != null){
                // Process @Column(unique)
                if(columnAnnotation.unique()){
                    uniqueConstraintFields.add(field);
                    constraintTypeFieldMap.put(UNIQUE, uniqueConstraintFields);
                }
            }

            builder.append(",");
        }


        constraintTypeFieldMap.forEach((sqlConstraintType, fields) -> {
            fields.forEach(f -> {
                switch (sqlConstraintType){
                    case PRIMARY_KEY:
                        appendPrimaryKeyConstraint(builder, f);
                        break;
                    case UNIQUE:
                        appendUniqueKeyConstraint(builder, f);
                        break;
                }

                builder.append(",");
            });
        });



        builder.replace(builder.length() - 1, builder.length(), "");
        builder.append(");");

        return builder.toString();
    }

    @Override
    public String generateDropTableQuery(Class entityClass) {
        StringBuilder builder = new StringBuilder();
        builder.append("DROP TABLE IF EXISTS ");
        builder.append(entityClass.getSimpleName());
        return builder.toString();
    }

    @Override
    public String generateSelectAllQuery(Class targetEntity) {
        return selectAllQueryBuilder(targetEntity).toString();
    }

    @Override
    public String generateFindByIdQuery(Class<?> entityClass, Object primaryKey) {
        StringBuilder builder = selectAllQueryBuilder(entityClass);
        builder.append(" WHERE ");
        for (Field field : entityClass.getDeclaredFields()) {
            Id idAnnotation = field.getAnnotation(Id.class);
            if(idAnnotation != null){
                builder.append(getColumnName(field));
                builder.append(" = ");
                builder.append(primaryKey);
            }
        }
        return builder.toString();
    }

    @Override
    public String generateDeleteByIdQuery(Class targetEntity, String id) {
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM ");
        builder.append(targetEntity.getSimpleName());
        builder.append(" WHERE ");
        for (Field field : targetEntity.getDeclaredFields()) {
            Id idAnnotation = field.getAnnotation(Id.class);
            if(idAnnotation != null){
                builder.append(getColumnName(field));
                builder.append(" = ");
                builder.append(id);
            }
        }
        return builder.toString();
    }

    @Override
    public String generateAddFKConstraintQuery(Class entity, Field field) {

        JoinColumn joinColumnAnnotation = field.getAnnotation(JoinColumn.class);

        StringBuilder sb = new StringBuilder();
        sb.append("alter table ");
        sb.append(entity.getSimpleName());
        sb.append(" add constraint ");
        sb.append(generateRandomFKConstraint(entity, field));
        sb.append(" foreign key ");
        sb.append("(");
        if(joinColumnAnnotation != null){
            sb.append(joinColumnAnnotation.name());
        }
        sb.append(") ");
        sb.append("references ");
        sb.append(field.getType().getSimpleName());
        sb.append("(");
        if(joinColumnAnnotation != null){
            sb.append(joinColumnAnnotation.referencedColumnName());
        }
        sb.append(") ");
        return sb.toString();
    }

    @Override
    public String generateGetAllConstraintsFromTableQuery(String databaseName, String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME, CONSTRAINT_NAME ");
        sb.append("FROM ");
        sb.append("INFORMATION_SCHEMA.KEY_COLUMN_USAGE ");
        sb.append("WHERE ");
        sb.append("REFERENCED_TABLE_SCHEMA = ");
        sb.append("'");
        sb.append(databaseName);
        sb.append("'");
        sb.append(" AND REFERENCED_TABLE_NAME = ");
        sb.append("'");
        sb.append(tableName);
        sb.append("'");
        return sb.toString();
    }

    @Override
    public String generateDropConstraintQuery(String tableName, String constraintName) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" DROP FOREIGN KEY ");
        sb.append(constraintName);
        return sb.toString();
    }

    private String generateRandomFKConstraint(Class entity, Field field) {
        StringBuilder sb = new StringBuilder();
        sb.append("FK");
        sb.append(entity.getSimpleName());
        sb.append(field.getType().getSimpleName());
        sb.append(RandomStringUtils.randomAlphanumeric(10));
        return sb.toString();
    }

    private StringBuilder selectAllQueryBuilder(Class targetEntity){
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        concatAllColumns(targetEntity.getDeclaredFields(), builder);
        builder.append(" FROM ");
        builder.append(targetEntity.getSimpleName());

        return builder;
    }

    private void appendPrimaryKeyConstraint(StringBuilder builder, Field f) {
        builder.append("PRIMARY KEY (`");
        builder.append(f.getName());
        builder.append("`)");
    }

    private void appendUniqueKeyConstraint(StringBuilder builder, Field f) {
        // Output example : UNIQUE INDEX `gmail_UNIQUE` (`gmail` ASC) VISIBLE)
        builder.append("UNIQUE INDEX ");

        builder.append("`");
        builder.append(f.getName());
        builder.append("_UNIQUE");
        builder.append("`");

        builder.append("(`");
        builder.append(f.getName());
        builder.append("` ");

        builder.append("ASC) VISIBLE");
    }

    private void concatAllColumns(Field[] fields, StringBuilder builder) {
        for (Field field : fields) {
            builder.append(getColumnName(field));
            builder.append(",");
        }
        builder.replace(builder.length() - 1, builder.length(), "");
    }

    private String getColumnName(Field field){
        Column columnAnnotation = field.getAnnotation(Column.class);
        if(StringUtils.isNullOrEmpty(columnAnnotation.name())){
            return field.getName();
        } else{
            return columnAnnotation.name();
        }
    }
}
