package dev.krud.crudframework.crud.annotation;

import dev.krud.crudframework.crud.hooks.interfaces.CRUDHooks;
import dev.krud.crudframework.crud.hooks.interfaces.CRUDHooks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Entity annotation, used to define generic persistent hooks which will run on the given entity
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(WithHooks.List.class)
public @interface WithHooks {

	/**
	 * The hook classes, each class provided must be an active bean
	 */
	Class<? extends CRUDHooks<?, ?>>[] hooks() default {};

	@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	@interface List {

		WithHooks[] value() default {};
	}
}
