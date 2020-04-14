package com.github.MrMks.skillbar.forge.listener;

import com.github.MrMks.skillbar.forge.GameSetting;
import com.github.MrMks.skillbar.forge.KeyManager;
import com.github.MrMks.skillbar.common.pkg.CPackage;
import com.github.MrMks.skillbar.forge.pkg.ForgeByteBuilder;
import com.github.MrMks.skillbar.forge.pkg.PackageSender;
import com.github.MrMks.skillbar.forge.skill.Manager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

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
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onKeyPress(InputEvent.KeyInputEvent event){
        Manager manager = Manager.getManager();
        if (!manager.isActive()) return;
        if (KeyManager.getToggleKey().isPressed()){
            setting.toggle();
        } else if (KeyManager.getSettingKey().isPressed()){
            Minecraft.getMinecraft().player.openGui(mod,2,Minecraft.getMinecraft().player.world,0,0,0);
        } else if (KeyManager.getBarPageUpKey().isPressed()) {
            setting.setBarPage(setting.getBarPage() - 1);
        } else if (KeyManager.getBarPageDownKey().isPressed()) {
            setting.setBarPage(setting.getBarPage() + 1);
        } else {
            List<KeyBinding> keys = KeyManager.getHotKeys();
            for (int i = 0; i < 9; i ++){
                if (keys.get(i).isPressed()){
                    String key = manager.getKeyInBar(i + setting.getBarPage() * 9);
                    if (key != null && !key.isEmpty()) {
                        PackageSender.send(CPackage.BUILDER.buildCast(ForgeByteBuilder::new, key));
                        break;
                    }
                }
            }
        }
    }
}