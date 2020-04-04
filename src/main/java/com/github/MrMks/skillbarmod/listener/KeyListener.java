package com.github.MrMks.skillbarmod.listener;

import com.github.MrMks.skillbarmod.GameSetting;
import com.github.MrMks.skillbarmod.KeyManager;
import com.github.MrMks.skillbarmod.pkg.PackageSender;
import com.github.MrMks.skillbarmod.skill.Manager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import java.util.List;

public class KeyListener {
    private Object mod;
    private GameSetting setting;
    public KeyListener(Object mod, GameSetting setting){
        this.mod = mod;
        this.setting = setting;
        for (KeyBinding key : KeyManager.getHotKeys()) {
            ClientRegistry.registerKeyBinding(key);
        }
        ClientRegistry.registerKeyBinding(KeyManager.getSettingKey());
        ClientRegistry.registerKeyBinding(KeyManager.getToggleKey());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onKeyPress(InputEvent.KeyInputEvent event){
        Manager manager = Manager.getManager();
        if (!manager.isActive()) return;
        if (KeyManager.getToggleKey().isPressed()){
            setting.toggle();
        } else if (KeyManager.getSettingKey().isPressed()){
            Minecraft.getMinecraft().player.openGui(mod,2,Minecraft.getMinecraft().player.world,0,0,0);
        } else {
            List<KeyBinding> keys = KeyManager.getHotKeys();
            for (int i = 0; i < 9; i ++){
                if (keys.get(i).isPressed()){
                    String key = manager.getKeyInBar(i);
                    if (key != null && !key.isEmpty()) {
                        PackageSender.sendCast(key);
                        break;
                    }
                }
            }
        }
    }
}
