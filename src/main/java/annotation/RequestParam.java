package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用户设置的普通参数(分rest)，
 * 且该参数是唯一的
 * 对于非唯一参数，使用Map<String, String[]>传过去给用户
 */
@Retention(RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {
    String value() default "";
}