package xlk.demo.test.aspectjx;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @author Created by xlk on 2020/9/12.
 * @desc
 */
@Aspect
public class PermissionAspect {
    private final String TAG = "PermissionAspect-->";

    @Pointcut("execution(@xlk.demo.test.aspectjx.applyPermissions * *(..))")
    public void annotationPointcut() {
    }

    @Before("annotationPointcut()")
    public void beforeMethod(JoinPoint joinPoint) {
        Log.i(TAG, "beforeMethod  " + joinPoint);
    }

    @After("annotationPointcut()")
    public void afterMethod(JoinPoint joinPoint) {
        Log.i(TAG, "afterMethod  " + joinPoint);
    }

    @Around("annotationPointcut()")
    public void aroundJoinPoint(final ProceedingJoinPoint joinPoint) throws Throwable {
        Log.i(TAG, "aroundJoinPoint start" + joinPoint);
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        boolean annotationPresent = method != null && method.isAnnotationPresent(applyPermissions.class);
        if (annotationPresent) {
            applyPermissions annotation = method.getAnnotation(applyPermissions.class);
            String[] permissions = annotation.values();
            int requestCode = annotation.requestCode();
            ApplyPermissionFragment fragment = new ApplyPermissionFragment();
            fragment.apply(permissions,requestCode);
        } else {
            joinPoint.proceed();
        }
        Log.i(TAG, "aroundJoinPoint end" + joinPoint);
    }
}
