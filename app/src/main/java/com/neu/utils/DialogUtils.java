package com.neu.utils;

import android.content.DialogInterface;

import java.lang.reflect.Field;

/**
 * Created by zhang on 2016/8/30.
 */
public class DialogUtils {
    public static void dialogClose(DialogInterface dialogInterface,boolean flag){
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface,flag);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
