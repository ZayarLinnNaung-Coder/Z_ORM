package org.z_orm.repository.utility;

import org.z_orm.annotation.Id;
import org.z_orm.reflection.ReflectionUtils;

import java.lang.reflect.Field;

public class EntityInformation<T, ID> {

    private Class<?> entityType;

    public boolean isNew(T entity){
        return getId(entity) == null;
    }

    public ID getId(T entity){
        for (Field field : entity.getClass().getDeclaredFields()) {
            if(field.getAnnotation(Id.class) != null){
                return (ID) ReflectionUtils.invokeGetter(entity, field.getName());
            }
        }
        return null;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<?> entityType) {
        this.entityType = entityType;
    }
}
