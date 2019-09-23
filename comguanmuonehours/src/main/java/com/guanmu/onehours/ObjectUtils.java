package com.guanmu.onehours;

/**
 * Created by Larry.
 */

public class ObjectUtils {
    @SafeVarargs
    public static <T> T coalesce(T... params)
    {
        for (T param : params)
            if (param != null)
                return param;
        return null;
    }
}
