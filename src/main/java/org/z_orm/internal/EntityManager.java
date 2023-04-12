package org.z_orm.internal;

import javassist.ClassPool;
import javassist.CtField;
import javassist.NotFoundException;
import org.reflections.Reflections;
import org.z_orm.annotation.AnnotationProcessor;
import org.z_orm.annotation.Column;
import org.z_orm.annotation.Entity;

import java.util.LinkedHashSet;
import java.util.Set;

public class EntityManager {
    public Set<Class> loadAllEntities(){
        Set<Class> classSet = new LinkedHashSet<>();

        new Reflections("client.appRepoBased.model").getTypesAnnotatedWith(Entity.class).forEach(c -> {
            try {
                for (CtField declaredField : ClassPool.getDefault().getCtClass(c.getName()).getDeclaredFields()) {
                    // Add default @Column to all declared fields
                    if(declaredField.getAnnotation(Column.class) == null){
                        AnnotationProcessor.addDefaultColumnAnnotation(c, declaredField);
                    }
                }
            } catch (NotFoundException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            classSet.add(c);
        });

        return classSet;
    }

}
