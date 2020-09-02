package com.github.MrMks.skillbar.forge;

import java.util.concurrent.atomic.AtomicBoolean;

public class BarControl {
    private final AtomicBoolean active = new AtomicBoolean(false);
    private final AtomicBoolean display = new AtomicBoolean(false);
    private final ServerSetting setting = new ServerSetting();
    private final SkillStore store = new SkillStore(setting);
    private int page = 0;

    public void onEnable(){
        active.set(true);
    }

    public void onDisable(){
        active.set(false);
    }

    public void switchShow(){
        display.set(!display.get());
    }

    public boolean isEnable(){
        return active.get();
    }

    public boolean isHide(){
        return !active.get() && !display.get();
    }

    public int getPage() {
        return page;
    }

    public void nextPage() {
        page = page + 1;
        if (page > getServerSetting().getMaxPage()) page = getServerSetting().getMaxPage();
    }

    public void previousPage() {
        page = page - 1;
        if (page < 0) page = 0;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getKeyInBar(int index) {
        return getSkillStore().getKeyInBar(index + page * 9);
    }

    public SkillStore getSkillStore() {
        return store;
    }

    public ServerSetting getServerSetting() {
        return setting;
    }

    public boolean isValid(){
        return getSkillStore().isValid() && getServerSetting().isValid();
    }

    public void cleanAll(){
        cleanSetting();
        cleanSkills();
        cleanBars();
    }

    public void cleanSetting(){
        getServerSetting().reset();
        getSkillStore().cleanSettings();
        setPage(0);
    }

    public void cleanSkills(){
        getSkillStore().cleanSkills();
    }

    public void cleanBars(){
        getSkillStore().cleanBars();
    }
}
