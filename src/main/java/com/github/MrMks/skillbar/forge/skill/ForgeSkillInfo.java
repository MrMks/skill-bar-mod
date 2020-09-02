package com.github.MrMks.skillbar.forge.skill;

import com.github.MrMks.skillbar.common.SkillInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

import java.util.List;

public class ForgeSkillInfo {

    public static final String SKILL_KEY = "skill_key";
    public static final String LOCKED_KEY = "__FIXED__";

    public static final ItemStack FIXED;
    static {
        Item item = Item.getByNameOrId("minecraft:barrier");
        FIXED = item == null ? ItemStack.EMPTY : new ItemStack(item,1,0);
        FIXED.setTagInfo(LOCKED_KEY, new NBTTagByte((byte) 0));
        FIXED.setTagInfo(SKILL_KEY, new NBTTagString("__fixed__"));
    }

    private final String key;
    private final ItemStack stack;
    private final boolean unlock;
    private final boolean canCast;

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
        this.stack.setTagInfo(SKILL_KEY, new NBTTagString(key));
    }

    public String getKey(){
        return key;
    }

    public ItemStack getIcon(){
        return stack == null ? ItemStack.EMPTY : stack;
    }

    public ItemStack getIcon(boolean isLock){
        ItemStack stack = getIcon().copy();
        if (isLock) stack.setTagInfo(LOCKED_KEY, new NBTTagByte((byte) 0));
        return stack;
    }

    public boolean isUnlock(){
        return unlock;
    }

    public boolean isCanCast(){
        return canCast;
    }
}
