package org.z_orm.reflection;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {

    public static Object invokeGetter(Object obj, String variableName) {
        Object result = null;
        try {
            if(obj != null){
                PropertyDescriptor pd = new PropertyDescriptor(variableName, obj.getClass());
                Method getter = pd.getReadMethod();
                result = getter.invoke(obj);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void invokeSetter(Object obj, String propertyName, Object variableValue) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(propertyName, obj.getClass());
            Method setter = pd.getWriteMethod();
            setter.invoke(obj,variableValue);

        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getType(Class<?> targetClass, String variableName){
        Class<?> returnType = null;
        try {
            PropertyDescriptor pd = new PropertyDescriptor(variableName, targetClass);
            returnType = pd.getReadMethod().getReturnType();
        } catch ( IllegalArgumentException | IntrospectionException e) {
            e.printStackTrace();
        }

        return returnType;
    }
}
