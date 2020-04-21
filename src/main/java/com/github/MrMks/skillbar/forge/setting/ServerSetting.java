package com.github.MrMks.skillbar.forge.setting;

public class ServerSetting {
    private static ReadOnly readOnly;
    private static ServerSetting instance;

    public static void buildDefault(int size){
        readOnly = new ReadOnly(size);
    }

    public static ServerSetting getInstance() {
        if (readOnly == null) readOnly = new ReadOnly(0);
        if (instance == null) instance = new ServerSetting();
        return instance;
    }

    private int maxSize = -1;

    public int getMaxSize() {
        return maxSize < 0 ? readOnly.maxBarSize : maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = Math.max(Math.min(maxSize,readOnly.maxBarSize),-1);
    }

    private static class ReadOnly {
        private int maxBarSize;
        ReadOnly(int size){
            maxBarSize = Math.max(size,0);
        }
    }
}
