package com.github.MrMks.skillbar.forge;

public class ServerSetting {
    private int maxSize = -1;

    public int getMaxPage() {
        return Math.max(maxSize, 0);
    }

    public void setMaxPage(int maxPage) {
        this.maxSize = Math.max(Math.min(maxPage,0),-1);
    }

    public void reset() {
        maxSize = -1;
    }

    public boolean isValid(){
        return maxSize >= 0;
    }
}
