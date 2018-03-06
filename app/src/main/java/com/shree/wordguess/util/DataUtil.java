package com.shree.wordguess.util;

import org.json.JSONArray;

/**
 * Created by SrinivasDonapati on 12/28/2017.
 */

public class DataUtil {


    public static <T> JSONArray arrayToJson(T[] array) {
        JSONArray jsonArray = new JSONArray();
        if (array == null) {
            return jsonArray;
        }

        for(T element : array) {
            jsonArray.put(element);
        }

        return jsonArray;
    }


    public static Integer[] getIntegerArray(int[] array) {
        Integer[] result = new Integer[array.length];
        for(int i=0;i<array.length;i++) {
            result[i] = array[i];
        }
        return result;
    }
}
