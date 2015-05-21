package com.atet.gamesdk.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by zhouwei on 2015/4/28.
 *
 * 反射的工具类
 */
public class ReflectUtils {

    public final static String TAG = ReflectUtils.class.getSimpleName();

    public static Field findField(Class classz, String name) {

        if (classz == null || name == null) return null;

        try {
            Field field = classz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            Glog.e(TAG, "NoSuchFieldException", e);
        }
        return findField(classz.getSuperclass(), name);
    }

    public static Object getValueQuietly(Field field, Object object) {

        if (field == null) return null;

        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            Glog.e(TAG, "IllegalAccessException", e);
        }
        return null;
    }

    public static void setValueQuietly(Field field, Object object, Object value) {

        if (field == null) return ;

        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            Glog.e(TAG, "IllegalAccessException", e);
        }
    }

    public static Method findMethod(Class classz, String name, Class<?>... parameterTypes) {

        if (classz == null || name == null) return null;

        try {
            Method method = classz.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            Glog.e(TAG, "NoSuchMethodException", e);
        }
        return findMethod(classz.getSuperclass(), name, parameterTypes);
    }

    public static Object invoke(Object receiver, Class classz, String name, Class[] parameterTypes, Object[] args) throws InvocationTargetException, IllegalAccessException {

        if (classz == null || name == null) return null;

        Method method = findMethod(classz, name, parameterTypes);

        return method == null ? null : method.invoke(receiver, args);
    }

    public static Object invokeQuietly(Object receiver, Class classz, String name, Class[] parameterTypes, Object[] args) {

        try {
            return invoke(receiver, classz, name, parameterTypes, args);
        } catch (Exception e) {
            Glog.e(TAG, "Exception", e);
        }
        return null;
    }

    public static Object invokeQuietly(Object receiver, String name, Class[] parameterTypes, Object[] args) {

        if (receiver == null) return null;

        return invokeQuietly(receiver, receiver.getClass(), name, parameterTypes, args);
    }

    public static Object invokeQuietly(Class classz, String name, Class[] parameterTypes, Object[] args) {
        return invokeQuietly(null, classz, name, parameterTypes, args);
    }

    public static Object invokeQuietly(Class classz, String name) {
        return invokeQuietly(null, classz, name, null, null);
    }

    public static Object invokeQuietly(Object receiver, String name) {
        return invokeQuietly(receiver, name, null, null);
    }

    public static Object invokeQuietly(Object receiver, Class classz, String name) {
        return invokeQuietly(receiver, classz, name, null, null);
    }

    public static Object invokeQuietly(Method method, Object receiver, Object[] args) {

        if (method == null) return null;

        try {
            return method.invoke(receiver, args);
        } catch (Exception e) {
            Glog.e(TAG, "Exception", e);
        }
        return null;
    }
}
