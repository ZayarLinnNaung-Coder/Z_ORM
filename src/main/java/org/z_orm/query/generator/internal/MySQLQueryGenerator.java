package org.z_orm.query.generator.internal;

import com.mysql.cj.util.StringUtils;
import org.z_orm.annotation.Column;
import org.z_orm.annotation.Id;
import org.z_orm.logging.logger.Logger;
import org.z_orm.logging.logger.LoggerFactory;
import org.z_orm.query.generator.QueryGenerator;
import org.z_orm.query.generator.SQLConstraintType;

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

            if(columnAnnotation != null){
                // Process @Column(name)
                if(StringUtils.isNullOrEmpty(columnAnnotation.name())){
                    builder.append(field.getName());
                }else{
                    builder.append(columnAnnotation.name());
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
                }

                // Process @Column(nullable)
                if (!columnAnnotation.nullable() || idAnnotation != null){
                    builder.append(" NOT NULL");
                }else{
                    builder.append(" NULL");
                }

                // Process @Id
                if(idAnnotation != null){
                    builder.append(" AUTO_INCREMENT");
                    constraintTypeFieldMap.put(PRIMARY_KEY, Set.of(field));
                }

                // Process @Column(unique)
                if(columnAnnotation.unique()){
                    uniqueConstraintFields.add(field);
                    constraintTypeFieldMap.put(UNIQUE, uniqueConstraintFields);
                }

                builder.append(",");
            }
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
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        concatAllColumns(targetEntity.getDeclaredFields(), builder);
        builder.append(" FROM ");
        builder.append(targetEntity.getSimpleName());
        return builder.toString();
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
            Column columnAnnotation = field.getAnnotation(Column.class);
            if(StringUtils.isNullOrEmpty(columnAnnotation.name())){
                builder.append(field.getName());
            } else{
                builder.append(columnAnnotation.name());
            }
            builder.append(",");
        }
        builder.replace(builder.length() - 1, builder.length(), "");
    }

}
