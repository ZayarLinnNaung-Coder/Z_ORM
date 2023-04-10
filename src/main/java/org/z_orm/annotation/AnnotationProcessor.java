package org.z_orm.annotation;

import javassist.CtField;

public class AnnotationProcessor {

    public static void addDefaultColumnAnnotation(Class c, CtField field){
        AnnotationUtils.addAnnotationToField(c, field.getName(), Column.class);
    }

}
