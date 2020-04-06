package com.github.MrMks.skillbarmod.skill;

import net.minecraft.item.ItemStack;
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

    private final int id;
    private final HashMap<String, SkillInfo> skillMap = new HashMap<>();
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

    public void setSkillMap(List<SkillInfo> aList, List<? extends CharSequence> reList){
        if (isEmpty()) return;
        synchronized (skillMap){
            for (SkillInfo info : aList) skillMap.put(info.getKey(), info);
            for (CharSequence key : reList) skillMap.remove(key.toString());
        }
    }

    public void setSkillMap(List<SkillInfo> nList){
        if (isEmpty()) return;
        synchronized (skillMap) {
            skillMap.clear();
            for (SkillInfo info : nList) skillMap.put(info.getKey(), info);
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
            for (SkillInfo info : skillMap.values()){
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

    public void addSkill(SkillInfo info){
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
        SkillInfo info = skillMap.get(key);
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
    public boolean setBarMap(HashMap<Integer, String> nMap){
        if (isEmpty()) return false;
        synchronized (barMap){
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

    private ArrayList<ItemStack> rIconList = new ArrayList<>(Collections.nCopies(9,ItemStack.EMPTY));
    public synchronized ArrayList<ItemStack> getBarIconList(){
        Collections.fill(rIconList,ItemStack.EMPTY);
        if (isEmpty()) return rIconList;
        ArrayList<Integer> list = new ArrayList<>(9);
        for (HashMap.Entry<Integer, String> entry : barMap.entrySet()){
            if (!skillMap.containsKey(entry.getValue())){
                list.add(entry.getKey());
                iconCache.remove(entry.getValue());
                continue;
            }
            ItemStack icon = iconCache.get(entry.getValue());
            if (icon != null){
                if (icon.getTagCompound() == null || !icon.hasTagCompound()) {
                    icon = null;
                    iconCache.remove(entry.getValue());
                }
                else if (!icon.getTagCompound().hasKey("key")) {
                    icon.setTagInfo("key", new NBTTagString(entry.getValue()));
                }
            }
            if (icon == null){
                SkillInfo info = skillMap.get(entry.getValue());
                if (info != null){
                    icon = info.getIcon().copy();
                    if (icon.hasTagCompound() && icon.getTagCompound() != null){
                        icon.setTagInfo("key", new NBTTagString(entry.getValue()));
                    } else {
                        icon = null;
                    }
                }
            }
            if (icon != null){
                int cd = cdMap.getOrDefault(entry.getValue(), 0) + 1;
                if (cd == 1) {
                    cdMap.remove(entry.getValue());
                }
                icon.setCount(cd);
                iconCache.put(entry.getValue(), icon);
                rIconList.set(entry.getKey(), icon);
            }
        }
        for (Integer o : list) barMap.remove(o);
        return rIconList;
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
