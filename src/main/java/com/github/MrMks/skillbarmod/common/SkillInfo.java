package com.github.MrMks.skillbarmod.common;

import java.util.ArrayList;
import java.util.List;

public class SkillInfo {
    public static SkillInfo Empty = new SkillInfo("",false,false,0,(short)0,"",new ArrayList<>());
    String key;
    boolean isUnlock;
    boolean canCast;

    int itemId;
    short damage;
    String display;
    List<? extends CharSequence> lore;

    public SkillInfo(CharSequence key, boolean isUnlock, boolean canCast, int itemId, short damage, CharSequence display, List<? extends CharSequence> lore){
        this.key = (key == null ? "" : key.toString());
        this.isUnlock = isUnlock;
        this.canCast = canCast;
        this.itemId = itemId;
        this.damage = damage;
        this.display = (display == null ? "" : display.toString());
        this.lore = lore;
    }

    public String getKey() {
        return key;
    }

    public boolean isUnlock() {
        return isUnlock;
    }

    public boolean canCast() {
        return canCast;
    }

    public int getItemId() {
        return itemId;
    }

    public short getDamage() {
        return damage;
    }

    public String getDisplay() {
        return display;
    }

    public List<? extends CharSequence> getLore() {
        return lore;
    }
}