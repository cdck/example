package xlk.demo.test.aspectjx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Created by xlk on 2020/9/12.
 * @desc
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface applyPermissions {
    String[] values() default {};

    int requestCode() default 0;
}
