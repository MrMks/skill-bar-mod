package com.github.MrMks.skillbar.forge.skill;

import com.github.MrMks.skillbar.common.SkillInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.List;

public class ForgeSkillInfo {
    private String key;
    private ItemStack stack;
    private boolean unlock;
    private boolean canCast;
    public ForgeSkillInfo(String key, boolean unlock, boolean canCast, ItemStack stack){
        this.key = key;
        this.unlock = unlock;
        this.stack = stack;
        this.canCast = canCast;
    }
    public ForgeSkillInfo(SkillInfo info){
        this.key = info.getKey();
        this.unlock = info.isUnlock();
        this.canCast = info.canCast();
        this.stack = new ItemStack(Item.getItemById(info.getItemId()),1, info.getDamage());
        NBTTagCompound display = new NBTTagCompound();
        display.setTag("Name", new NBTTagString(info.getDisplay()));
        NBTTagList loreList = new NBTTagList();
        List<? extends CharSequence> lores = info.getLore();
        for (CharSequence sequence : lores){
            loreList.appendTag(new NBTTagString(sequence.toString()));
        }
        display.setTag("Lore", loreList);
        this.stack.setTagInfo("display", display);
        this.stack.setTagInfo("HideFlags", new NBTTagInt(63));
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
