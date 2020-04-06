package com.github.MrMks.skillbarmod.gui;

import com.github.MrMks.skillbarmod.pkg.PackageSender;
import com.github.MrMks.skillbarmod.skill.Manager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContainerSkillSetting extends Container {
    public ContainerSkillSetting(Manager manager){
        init(manager);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    private ItemStackHandler full = new ItemStackHandler(36);
    private ItemStackHandler slc = new ItemStackHandler(9);
    private int pageNow = 0;
    private List<ItemStack> enabled;
    private Manager manager;
    public void init(Manager manager){
        int i = 0;
        enabled = manager.getShowSkillList();
        for (ItemStack stack : enabled){
            full.setStackInSlot(i++,stack);
            if (i >= 36) break;
        }
        while (i < 36) full.setStackInSlot(i++, ItemStack.EMPTY);
        i = 0;
        ArrayList<ItemStack> bar = manager.getBarIconList();
        for (ItemStack stack : bar){
            slc.setStackInSlot(i++,stack);
            if (i >= 9) break;
        }
        for (i = 0; i < 4; i++){
            for (int j = 0; j < 9; j++){
                this.addSlotToContainer(new SlotItemHandler(full, j + i*9,8 + j * 18,i * 18 + 18));
            }
        }
        for (i = 0; i < 9; i++){
            this.addSlotToContainer(new SlotItemHandler(slc, i , 8 + i * 18, 104));
        }
        this.manager = manager;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        //super.onContainerClosed(playerIn);
        HashMap<Integer, String> map = new HashMap<>();
        for (int i = 0; i < 9; i ++){
            ItemStack stack = slc.getStackInSlot(i);
            if (stack.hasTagCompound() && stack.getTagCompound() != null && stack.getTagCompound().hasKey("key")){
                String key = stack.getTagCompound().getString("key");
                map.put(i, key);
            }
        }
        if (manager.setBarMap(map)){
            PackageSender.sendSaveBar(manager.getId(), map);
        }
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        InventoryPlayer inventoryPlayer = player.inventory;
        if (slotId < 0 || slotId > 45) inventoryPlayer.setItemStack(ItemStack.EMPTY);
        else if (clickTypeIn == ClickType.PICKUP){
            Slot slot = this.inventorySlots.get(slotId);
            if (slot != null){
                ItemStack item = slot.getStack();
                ItemStack mItem = inventoryPlayer.getItemStack();
                if (dragType == 1){
                    if (!mItem.isEmpty()){
                        inventoryPlayer.setItemStack(ItemStack.EMPTY);
                    } else if (!item.isEmpty() && slotId >= 36){
                        slot.putStack(ItemStack.EMPTY);
                    }
                } else {
                    if (slotId < 36){
                        ItemStack i = item.isEmpty() ? ItemStack.EMPTY : item.copy();
                        inventoryPlayer.setItemStack(i);
                    } else {
                        if (!mItem.isEmpty()){
                            slot.putStack(mItem);
                            inventoryPlayer.setItemStack(ItemStack.EMPTY);
                        }
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public void pageUp(){
        int pageMax = enabled.size() / 36;
        if (pageNow > 0 && pageNow <= pageMax){
            pageNow--;
        }
        listToFull();
    }

    public void pageDown(){
        int pageMax = enabled.size() / 36;
        if (pageNow >= 0 && pageNow < pageMax){
            pageNow++;
        }
        listToFull();
    }

    private void listToFull(){
        int i = 0;
        while (pageNow * 36 + i < enabled.size() && i < 36){
            full.setStackInSlot(i,enabled.get(pageNow * 36 + i));
            i++;
        }
        while(i < 36) full.setStackInSlot(i++, ItemStack.EMPTY);
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return false;
    }
}
