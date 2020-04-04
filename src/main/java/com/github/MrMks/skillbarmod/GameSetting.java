package com.github.MrMks.skillbarmod;

public class GameSetting {

    private boolean show = true;
    public void toggle(){
        show = !show;
    }

    public boolean isShow() {
        return show;
    }
}
