package org.z_orm.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

public aspect AnnotationAspect {
    @Pointcut("@annotation(org.z_orm.annotation.Transactional)")
    public void loggableMethods() {
    }

    @Before("loggableMethods()")
    public void beforeTransactional() {
        System.out.println("Before Transactional");
    }

    @After("loggableMethods()")
    public void afterTransactional() {
        System.out.println("After Transactional");
    }
}
