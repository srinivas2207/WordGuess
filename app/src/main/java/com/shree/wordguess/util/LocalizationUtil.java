package com.shree.wordguess.util;

import android.content.Context;

/**
 * Created by SrinivasDonapati on 3/9/2018.
 */

public class LocalizationUtil {
    public static String getString(Context context, int stringId, Object[] values) {
        String localString = context.getResources().getString(stringId);
        if (values != null && values.length > 0) {
            for(int i = 0; i< values.length; i++) {
                String key = "{" + i + "}" ;
                localString = localString.replaceAll(key, values[i].toString());
            }
        }
        return localString;
    }
}
