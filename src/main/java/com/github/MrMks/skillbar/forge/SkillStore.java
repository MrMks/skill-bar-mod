package com.github.MrMks.skillbar.forge;

import com.github.MrMks.skillbar.common.SkillInfo;
import com.github.MrMks.skillbar.forge.skill.ForgeSkillInfo;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class should be thread-safe, since methods will be invoked from both net thread and main thread
 */
public class SkillStore {
    private final Map<String, ForgeSkillInfo> skillMap = new ConcurrentHashMap<>();
    private final Map<Integer, String> barMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> cdMap = new ConcurrentHashMap<>();
    private final List<Integer> lockedSlots = new ArrayList<>();

    private final ServerSetting setting;

    public SkillStore(ServerSetting setting) {
        this.setting = setting;
    }

    /**
     * @return a list of itemStack those will show in gui, ordered by hashmap
     */
    public List<ItemStack> getShownList() {
        List<ItemStack> list = new ArrayList<>();
        skillMap.forEach((key, info)->{
            if (info.isUnlock() && info.isCanCast()) list.add(info.getIcon().copy());
        });
        return list;
    }

    public List<ItemStack> getBarList(int page) {
        List<ItemStack> list = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            int index = i + page * 9;
            if (barMap.containsKey(index)) {
                String key = barMap.get(index);
                boolean flag = lockedSlots.contains(index);
                ForgeSkillInfo info;
                if (skillMap.containsKey(key) && (info = skillMap.get(key)).isUnlock() && info.isCanCast()) {
                    list.add(info.getIcon(flag).copy());
                } else {
                    list.add(flag ? ForgeSkillInfo.FIXED : ItemStack.EMPTY);
                }
            } else {
                list.add(ItemStack.EMPTY);
            }
        }
        return list;
    }

    public Map<Integer, String> getBarKeyMap() {
        return new HashMap<>(barMap);
    }

    public List<Integer> getSkillCooldown(int page) {
        List<Integer> list = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            int index = i + page * 9;
            if (barMap.containsKey(index)) {
                list.add(cdMap.getOrDefault(barMap.get(index), 0));
            } else {
                list.add(0);
            }
        }
        return list;
    }

    public String getKeyInBar(int index){
        return barMap.getOrDefault(index, null);
    }

    public ItemStack getIconInBar(int index) {
        if (barMap.containsKey(index)) {
            String key = barMap.get(index);
            boolean isLock = lockedSlots.contains(index);
            ForgeSkillInfo info;
            if (skillMap.containsKey(key) && (info = skillMap.get(key)).isUnlock() && info.isCanCast()) {
                return info.getIcon(isLock);
            } else return isLock ? ForgeSkillInfo.FIXED : ItemStack.EMPTY;
        } else return ItemStack.EMPTY;
    }

    public void addSkills(List<SkillInfo> infos){
        infos.forEach(this::updateSkill);
    }

    public void removeSkills(List<String> keys){
        keys.forEach(skillMap::remove);
    }

    public void updateSkill(SkillInfo info){
        skillMap.put(info.getKey(), new ForgeSkillInfo(info));
    }

    public void setBarSkills(Map<Integer, String> map){
        barMap.clear();
        barMap.putAll(map);
    }

    public boolean setBarSkillsClient(Map<Integer, String> map) {
        boolean changed = false;
        for (int i = 0; i < setting.getMaxPage() * 9 + 9; i++) {
            boolean isLock = lockedSlots.contains(i);
            if (!isLock) {
                String nKey = map.get(i);
                String oKey = barMap.get(i);
                if (nKey != null && oKey != null) {
                    if (!nKey.equals(oKey)) {
                        changed = true;
                        barMap.put(i, nKey);
                    }
                } else if (nKey != null) {
                    barMap.put(i, nKey);
                    changed = true;
                } else if (oKey != null) {
                    barMap.remove(i);
                    changed = true;
                }
            }
        }
        return changed;
    }

    public void setSlots(List<Integer> list){
        lockedSlots.clear();
        lockedSlots.addAll(list);
    }

    public void setCooldownMap(Map<String, Integer> map){
        cdMap.clear();
        cdMap.putAll(map);
    }

    public void cleanSkills(){
        skillMap.clear();
    }

    public void cleanBars(){
        barMap.clear();
    }

    public void cleanSettings(){
        lockedSlots.clear();
    }

    public boolean isValid(){
        return true;
    }
}
