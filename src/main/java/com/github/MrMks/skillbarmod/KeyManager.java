package com.github.MrMks.skillbarmod;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class KeyManager {
    private static final String cate = "key.categories.skillbar";
    private static final ArrayList<KeyBinding> keys = new ArrayList<>(9);
    private static final KeyBinding setting = new KeyBinding("key.skillbar.setting", KeyConflictContext.IN_GAME, Keyboard.KEY_GRAVE, cate);
    private static final KeyBinding toggle = new KeyBinding("key.skillbar.toggle", KeyConflictContext.IN_GAME, KeyModifier.CONTROL, Keyboard.KEY_GRAVE, cate);
    static {
        for (int i = 0; i < 9; i++){
            KeyBinding key = new KeyBinding("key.skillbar.hotkey_" + (i+1), KeyConflictContext.UNIVERSAL, Keyboard.getKeyIndex(String.valueOf(i + 1)),cate);
            keys.add(key);
        }
    }

    public static List<KeyBinding> getHotKeys(){
        return keys;
    }

    public static KeyBinding getSettingKey(){
        return setting;
    }

    public static KeyBinding getToggleKey(){return toggle;}
}