package com.github.MrMks.skillbar.forge.skill;

import com.github.MrMks.skillbar.forge.ConditionManager;
import com.github.MrMks.skillbar.forge.GameSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBarrier;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagString;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Manager {
    private static final HashMap<Integer, Manager> map = new HashMap<>();
    private static final AtomicInteger activeId = new AtomicInteger(-1);
    private static final AtomicBoolean enable = new AtomicBoolean(false);
    private static final Manager empty = new Manager(-1){
        @Override
        public boolean isEmpty() {
            return true;
        }
    };

    @Nonnull
    public static Manager getManager(){
        synchronized (map){
            return (activeId.get() < 0 || !enable.get()) ? empty : map.getOrDefault(activeId.get(), empty);
        }
    }

    public static Manager getManager(int id){
        synchronized (map) {
            return (id < 0 || !enable.get()) ? empty : map.getOrDefault(id, empty);
        }
    }

    public static void setEnable(boolean enb){
        enable.set(enb);
    }

    public static boolean isEnable(){
        return enable.get();
    }

    public static void setActiveId(int id){
        activeId.set(id);
        if (id < 0) enable.set(false);
    }

    public static int getActiveId(){
        return activeId.get();
    }

    public static Manager prepareManager(int id){
        synchronized (map){
            if (id < 0) return empty;
            if (!map.containsKey(id)) map.put(id, new Manager(id));
            return map.get(id);
        }
    }

    public static void clean(){
        synchronized (map){
            for (Manager manager : map.values()){
                manager.clear();
            }
            map.clear();
            activeId.set(-1);
            enable.set(false);
        }
    }

    //private static List<Integer> freeList = new ArrayList<>();

    private final int id;
    private final HashMap<String, ForgeSkillInfo> skillMap = new HashMap<>();
    private final HashMap<Integer, String> barMap = new HashMap<>(9);
    private final HashMap<String, Integer> cdMap = new HashMap<>(9);
    private final HashMap<String, ItemStack> iconCache = new HashMap<>(9);

    private Manager(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public boolean isActive(){
        return !isEmpty() && this.id == activeId.get() && enable.get();
    }

    protected boolean isEmpty(){
        return false;
    }

    public void setSkillMap(List<ForgeSkillInfo> aList, List<? extends CharSequence> reList){
        if (isEmpty()) return;
        synchronized (skillMap){
            for (ForgeSkillInfo info : aList) skillMap.put(info.getKey(), info);
            for (CharSequence key : reList) skillMap.remove(key.toString());
        }
    }

    public void setSkillMap(List<ForgeSkillInfo> nList){
        if (isEmpty()) return;
        synchronized (skillMap) {
            skillMap.clear();
            for (ForgeSkillInfo info : nList) skillMap.put(info.getKey(), info);
        }
    }

    public boolean isListSkill(int size){
        if (isEmpty()) return false;
        synchronized (skillMap){
            return skillMap.size() != size;
        }
    }

    public List<ItemStack> getShowSkillList(){
        if (isEmpty()) return Collections.emptyList();
        synchronized (skillMap){
            ArrayList<ItemStack> list = new ArrayList<>();
            for (ForgeSkillInfo info : skillMap.values()){
                if (info.isCanCast() && info.isUnlock()){
                    ItemStack stack = info.getIcon().copy();
                    stack.setCount(1);
                    stack.setTagInfo("key", new NBTTagString(info.getKey()));
                    list.add(stack);
                }
            }
            return list;
        }
    }

    public List<String> getSkillKeyList(){
        if (isEmpty()) return Collections.emptyList();
        synchronized (skillMap){
            return new ArrayList<>(skillMap.keySet());
        }
    }

    public void addSkill(ForgeSkillInfo info){
        if (isEmpty()) return;
        synchronized (skillMap) {
            skillMap.put(info.getKey(), info);
        }
    }

    public void removeSkill(String key){
        if (isEmpty()) return;
        synchronized (skillMap) {
            skillMap.remove(key);
        }
    }

    public String getSkillDisplayName(String key){
        ForgeSkillInfo info = skillMap.get(key);
        if (info == null) return key;
        else {
            String name = info.getIcon().getDisplayName();
            return name.substring(2,name.lastIndexOf("=") - 4);
        }
    }

    /**
     *
     * @param nMap new map in selected bar
     * @return true if nMap have difference form barMap;
     */
    public boolean setBarMap(Map<Integer, String> nMap){
        return setBarMap(nMap,false);
    }

    public boolean setBarMap(Map<Integer, String> nMap, boolean forceSet){
        if (isEmpty()) return false;
        synchronized (barMap){
            if (!forceSet && ConditionManager.isAllowFree()) {
                List<Integer> list = new ArrayList<>(nMap.keySet());
                list.removeIf(key->ConditionManager.getFreeList().contains(key));
                list.forEach(key->{
                    if (barMap.containsKey(key)) nMap.put(key,barMap.get(key));
                    else nMap.remove(key);
                });
            }
            boolean flag = barMap.size() == nMap.size();
            if (flag){
                for (HashMap.Entry<Integer, String> entry : nMap.entrySet()){
                    flag = entry.getValue().equals(barMap.getOrDefault(entry.getKey(), ""));
                    if (!flag) break;
                }
            }
            if (!flag){
                barMap.clear();
                barMap.putAll(nMap);
            }
            return !flag;
        }
    }

    public String getKeyInBar(int i) {
        if (isEmpty()) return null;
        synchronized (barMap){
            return barMap.get(i);
        }
    }

    public Map<Integer, String> getBarMap() {
        return new HashMap<>(barMap);
    }

    public synchronized Map<Integer, ItemStack> getBarIconMap(){
        Map<Integer, ItemStack> rIconMap = new HashMap<>();
        if (isEmpty()) return rIconMap;
        ArrayList<Integer> list = new ArrayList<>(9);
        for (Map.Entry<Integer, String> entry : barMap.entrySet()){
            int index = entry.getKey();
            String key = entry.getValue();
            if (!skillMap.containsKey(key)){
                list.add(index);
                iconCache.remove(key);
                continue;
            } else if (!skillMap.get(key).isUnlock()){
                continue;
            }
            ItemStack icon = iconCache.get(key);
            if (icon != null){
                if (icon.getTagCompound() == null || !icon.hasTagCompound()) {
                    icon = null;
                    iconCache.remove(key);
                }
                else if (!icon.getTagCompound().hasKey("key")) {
                    icon.setTagInfo("key", new NBTTagString(key));
                }
            }
            if (icon == null){
                ForgeSkillInfo info = skillMap.get(key);
                if (info != null){
                    icon = info.getIcon().copy();
                    if (icon.hasTagCompound() && icon.getTagCompound() != null){
                        icon.setTagInfo("key", new NBTTagString(key));
                    } else {
                        icon = null;
                    }
                }
            }
            if (icon != null){
                int cd = cdMap.getOrDefault(key, 0) + 1;
                if (cd == 1) {
                    cdMap.remove(key);
                }
                icon.setCount(cd);
                iconCache.put(key, icon);
                rIconMap.put(index, icon);
            }
        }
        for (Integer o : list) barMap.remove(o);
        if (ConditionManager.isFix()) {
            ItemStack fixedSlot = new ItemStack(Item.getByNameOrId("minecraft:barrier"));
            fixedSlot.setTagInfo("fix", new NBTTagByte((byte) 0));
            for (int index = 0;index < GameSetting.getInstance().getMaxBarPage() * 9 + 9;index++)
                if (!barMap.containsKey(index) && !ConditionManager.getFreeList().contains(index))
                    rIconMap.put(index,fixedSlot);
        }
        return rIconMap;
    }

    public boolean isListBar(){
        if (isEmpty()) return false;
        synchronized (barMap){
            return barMap.isEmpty();
        }
    }

    public void setCoolDown(HashMap<String, Integer> nMap){
        if (isEmpty()) return;
        synchronized (cdMap){
            for (HashMap.Entry<String, Integer> entry : nMap.entrySet()){
                setCoolDown(entry.getKey(), entry.getValue());
            }
        }
    }

    public void setCoolDown(String key, Integer cd){
        if (isEmpty()) return;
        synchronized (cdMap){
            if (cd > 0) cdMap.put(key,cd);
            else cdMap.remove(key);
        }
    }

    public synchronized void clear(){
        skillMap.clear();
        barMap.clear();
        cdMap.clear();
        iconCache.clear();
    }
}
