package com.github.MrMks.skillbar.forge.gui;

import com.github.MrMks.skillbar.forge.GameSetting;
import com.github.MrMks.skillbar.common.pkg.CPackage;
import com.github.MrMks.skillbar.forge.pkg.ForgeByteBuilder;
import com.github.MrMks.skillbar.forge.pkg.PackageSender;
import com.github.MrMks.skillbar.forge.skill.Manager;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ContainerSkillSetting extends Container {
    private ItemStackHandler full = new ItemStackHandler(36);
    private ItemStackHandler slc = new ItemStackHandler(9);
    private int pageNow = 0;
    private int barPageNow = 0;
    private int barPageMax;
    private List<ItemStack> enabled;
    private Map<Integer, String> map;
    private Map<Integer, ItemStack> iconMap;
    private Manager manager;

    public ContainerSkillSetting(Manager manager, GameSetting setting){
        this.manager = manager;
        this.enabled = manager.getShowSkillList();
        this.map = manager.getBarMap();
        this.iconMap = manager.getBarIconMap();
        this.barPageMax = setting.getMaxBarPage();
        listToFull();
        listToSlc();
        int i;
        for (i = 0; i < 4; i++){
            for (int j = 0; j < 9; j++){
                this.addSlotToContainer(new SlotItemHandler(full, j + i*9,8 + j * 18,i * 18 + 18));
            }
        }
        for (i = 0; i < 9; i++){
            this.addSlotToContainer(new SlotItemHandler(slc, i , 8 + i * 18, 104));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        //super.onContainerClosed(playerIn);
        slcToMap();
        if (manager.setBarMap(map)) {
            PackageSender.send(CPackage.BUILDER.buildSaveBar(ForgeByteBuilder::new, map));
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
                if (item.hasTagCompound() && item.getTagCompound().hasKey("fix")) return ItemStack.EMPTY;
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
        if (canPageUp()){
            pageNow--;
        }
        listToFull();
    }

    public boolean canPageUp(){
        return pageNow > 0 && pageNow <= getPageMax();
    }

    public void pageDown(){
        if (canPageDown()){
            pageNow++;
        }
        listToFull();
    }

    public boolean canPageDown(){
        return pageNow >= 0 && pageNow < getPageMax();
    }

    public int getPageNow() {
        return pageNow;
    }

    public int getPageMax() {
        return enabled.size() / 36;
    }


    public void barPageUp(){
        slcToMap();
        barPageNow = Math.max(0, barPageNow - 1);
        listToSlc();
    }

    public boolean canBarPageUp(){
        return barPageNow > 0 && barPageNow <= barPageMax;
    }

    public void barPageDown(){
        slcToMap();
        barPageNow = Math.min(barPageMax, barPageNow + 1);
        listToSlc();
    }

    public boolean canBarPageDown(){
        return barPageNow >= 0 && barPageNow < barPageMax;
    }

    public int getBarPageNow() {
        return barPageNow;
    }

    public int getBarPageMax() {
        return barPageMax;
    }

    private void listToFull(){
        int i = 0;
        while (pageNow * 36 + i < enabled.size() && i < 36){
            full.setStackInSlot(i,enabled.get(pageNow * 36 + i));
            i++;
        }
        while(i < 36) full.setStackInSlot(i++, ItemStack.EMPTY);
    }

    private void listToSlc(){
        for (int i = 0; i < 9; i++){
            slc.setStackInSlot(i,iconMap.getOrDefault(i + barPageNow * 9, ItemStack.EMPTY));
        }
    }

    private void slcToMap(){
        for (int i = 0; i < 9; i ++){
            ItemStack stack = slc.getStackInSlot(i).copy();
            if (stack.hasTagCompound() && stack.getTagCompound() != null && stack.getTagCompound().hasKey("key")){
                String key = stack.getTagCompound().getString("key");
                map.put(i + barPageNow * 9, key);
                iconMap.put(i + barPageNow * 9, stack);
            } else {
                map.remove(i + barPageNow * 9);
                iconMap.remove(i + barPageNow * 9);
            }
        }
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return false;
    }
}
