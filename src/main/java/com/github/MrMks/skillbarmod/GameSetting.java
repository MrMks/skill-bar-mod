package com.github.MrMks.skillbarmod;

public class GameSetting {
    private static GameSetting instance = new GameSetting();
    public static GameSetting getInstance(){
        return instance;
    }

    private boolean show = true;
    public void toggle(){
        show = !show;
    }

    public boolean isShow() {
        return show;
    }

    private int barPage = 0;
    private int maxPage = 2;
    public void setBarPage(int nP){
        barPage = Math.min(Math.max(0,nP),maxPage);
    }

    public int getBarPage(){
        return barPage;
    }

    public void setMaxBarPage(int np){
        maxPage = np;
    }

    public int getMaxBarPage() {
        return maxPage;
    }
}
