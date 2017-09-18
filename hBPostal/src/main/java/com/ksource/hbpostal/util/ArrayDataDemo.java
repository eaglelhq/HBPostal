package com.ksource.hbpostal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @version 0.1 king 2015-11
 */
public class ArrayDataDemo {

    private static final Map<String, Map<String, List<String>>> DATAs = new LinkedHashMap<>();

    private static void init() {
        if (!DATAs.isEmpty()) {
            return;
        }

        for (int i = 0; i < 10; i++) {
            Map<String, List<String>> city = new HashMap<>();
            for (int j = 0; j < 10; j++) {
                List<String> data = new ArrayList<>();
                for (int k = 0; k < 10; k++) {
                    data.add("某乡" + i + j+ k);
                }
                city.put("某县" + i + j, data);
            }
            DATAs.put("某区" + i, city);
        }
    }

    private static Random random = new Random();

    private static String getRandomText() {
        int num = random.nextInt(20);
        String str = "五";
        for (int i = 0; i < num; i++) {
            str += "五";
        }
        return str;
    }

    public static Map<String, Map<String, List<String>>> getAll() {
        init();
        return new HashMap<>(DATAs);
    }

}
