package com.github.MrMks.skillbar.forge.listener;

import com.github.MrMks.skillbar.common.pkg.CPackage;
import com.github.MrMks.skillbar.forge.KeyManager;
import com.github.MrMks.skillbar.forge.pkg.PackageSender;
import com.github.MrMks.skillbar.forge.setting.ClientSetting;
import com.github.MrMks.skillbar.forge.skill.Manager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import java.util.List;

public class KeyListener {
    private Object mod;
    public KeyListener(Object mod){
        this.mod = mod;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onKeyPress(InputEvent.KeyInputEvent event){
        Manager manager = Manager.getManager();
        ClientSetting setting = ClientSetting.getInstance();
        if (!manager.isActive()) return;
        if (KeyManager.getToggleKey().isPressed()){
            setting.switchHide();
        } else if (KeyManager.getSettingKey().isPressed()){
            Minecraft.getMinecraft().player.openGui(mod,2,Minecraft.getMinecraft().player.world,0,0,0);
        } else if (KeyManager.getBarPageUpKey().isPressed()) {
            setting.setSize(setting.getSize() - 1);
        } else if (KeyManager.getBarPageDownKey().isPressed()) {
            setting.setSize(setting.getSize() + 1);
        } else {
            List<KeyBinding> keys = KeyManager.getHotKeys();
            for (int i = 0; i < 9; i ++){
                if (keys.get(i).isPressed()){
                    String key = manager.getKeyInBar(i + setting.getSize() * 9);
                    if (key != null && !key.isEmpty()) {
                        PackageSender.send(CPackage.BUILDER.buildCast(key));
                        break;
                    }
                }
            }
        }
    }
}
