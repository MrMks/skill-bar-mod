package com.github.MrMks.skillbarmod.skill;

import net.minecraft.item.ItemStack;

public class SkillInfo {
    private String key;
    private ItemStack stack;
    private boolean unlock;
    private boolean canCast;
    public SkillInfo(String key, boolean unlock, boolean canCast, ItemStack stack){
        this.key = key;
        this.unlock = unlock;
        this.stack = stack;
        this.canCast = canCast;
    }

    public String getKey(){
        return key;
    }

    public ItemStack getIcon(){
        return stack == null ? ItemStack.EMPTY : stack;
    }

    public boolean isUnlock(){
        return unlock;
    }

    public boolean isCanCast(){
        return canCast;
    }
}
