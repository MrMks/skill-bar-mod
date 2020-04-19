package com.github.MrMks.skillbar.forge.skill;

import com.github.MrMks.skillbar.common.ICondition;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Condition implements ICondition {
    private int size;
    private boolean fix;
    private boolean free;
    private Map<Integer, String> map;
    private List<Integer> list;
    public Condition(int size, boolean fix, boolean free, Map<Integer, String> map ,List<Integer> list){
        this.size = size;
        this.fix = fix;
        this.free = free;
        this.map = map;
        this.list = list;
    }

    @Override
    public int getBarSize() {
        return size;
    }

    @Override
    public boolean isEnableFix() {
        return fix;
    }

    @Override
    public Map<Integer, String> getFixMap() {
        return map;
    }

    @Override
    public boolean isEnableFree() {
        return free;
    }

    @Override
    public List<Integer> getFreeList() {
        return free ? list : Collections.emptyList();
    }
}
