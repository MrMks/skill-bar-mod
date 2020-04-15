package com.github.MrMks.skillbar.forge;

/**
 * This class is response to save all gaming settings;
 */
public class GameSetting {
    private static GameSetting instance = new GameSetting();
    public static GameSetting getInstance(){
        return instance;
    }
    public static void clean(){
        instance = null;
        instance = new GameSetting();
    }

    /**
     * should or not render {@link com.github.MrMks.skillbar.forge.gui.GuiSkillBar} overlay;
     */
    private boolean show = true;
    public void toggle(){
        show = !show;
    }

    public boolean isHide() {
        return !show;
    }

    /**
     * how many line of bar is active;
     * Mainly used in {@link com.github.MrMks.skillbar.forge.gui.ContainerSkillSetting}, {@link com.github.MrMks.skillbar.forge.gui.GuiSkillBar} and {@link com.github.MrMks.skillbar.forge.gui.GuiSkillSettings};
     */
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

    private boolean fixBar = false;
    public void setFixBar(boolean is){
        fixBar = is;
    }
    public boolean isFixBar() {
        return fixBar;
    }
}
