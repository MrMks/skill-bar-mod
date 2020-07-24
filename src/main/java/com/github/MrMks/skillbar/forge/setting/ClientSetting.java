package com.github.MrMks.skillbar.forge.setting;

public class ClientSetting {
    private static ClientSetting instance;
    public static ClientSetting getInstance() {
        if (instance == null) instance = new ClientSetting();
        return instance;
    }
    public static void clean(){
        instance = new ClientSetting();
    }

    private boolean hide = false;
    public boolean isHide() {
        return hide;
    }
    public void setHide(boolean hide) {
        this.hide = hide;
    }
    public void switchHide(){
        this.hide = !this.hide;
    }

    private int size = 0;
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = Math.min(Math.max(size, 0),ServerSetting.getInstance().getMaxSize());
    }
}
