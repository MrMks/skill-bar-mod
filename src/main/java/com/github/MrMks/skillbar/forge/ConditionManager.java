package com.github.MrMks.skillbar.forge;

import java.util.Collections;
import java.util.List;

public class ConditionManager {
    private static boolean active = false;
    private static String key = "";
    private static int size = 0;
    private static boolean fix = false;
    private static boolean free = false;
    private static List<Integer> freeList = null;
    public static void enter(String k, int s, boolean f, boolean fr, List<Integer> fL){
        size = s;
        key = k;
        fix = f;
        free = fr;
        freeList = fL;
        active = true;
    }
    public static void leave(){
        active = false;
        key = "";
        size = 0;
        fix = true;
        freeList = null;
    }

    public static boolean isActive(){
        return active;
    }

    public static boolean isFix(){
        return active && fix;
    }

    public static boolean isAllowFree(){
        return active && fix && free && freeList != null && !freeList.isEmpty();
    }

    public static int getBarSize(){
        return size;
    }

    public static List<Integer> getFreeList(){
        return isAllowFree() ? freeList : Collections.emptyList();
    }
}
