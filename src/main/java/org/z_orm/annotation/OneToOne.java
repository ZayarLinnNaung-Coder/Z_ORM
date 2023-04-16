package org.z_orm.annotation;

import org.z_orm.persistence.CascadeType;
import org.z_orm.persistence.FetchType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)

public @interface OneToOne {
    CascadeType[] cascade() default {};
    FetchType fetch() default FetchType.EAGER;
    String mappedBy() default "";
}
