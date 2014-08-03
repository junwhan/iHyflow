package org.infinispan.iHyflow.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.infinispan.iHyflow.core.tm.NestingModel;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Nested {
	NestingModel value() default NestingModel.CLOSED;
}
