package com.tomoyadeng.trpc.core.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReflectionUtil {

    public static String getInterfaceName(Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        String className = clazz.getName();
        if (interfaces != null && interfaces.length > 0) {
            className = interfaces[0].getName();
        }
        return className;
    }

}
