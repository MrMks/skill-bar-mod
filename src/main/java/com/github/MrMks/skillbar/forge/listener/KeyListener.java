package com.github.MrMks.skillbar.forge.listener;

import com.github.MrMks.skillbar.common.pkg.CPackage;
import com.github.MrMks.skillbar.forge.KeyManager;
import com.github.MrMks.skillbar.forge.pkg.PackageSender;
import com.github.MrMks.skillbar.forge.setting.ClientSetting;
import com.github.MrMks.skillbar.forge.skill.Manager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public class KeyListener {
    @Mod.Instance
    private Object mod;

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

    // 长按持续释放，客户端每5tick每次
    private int ticked = 0;
    private int last = -1;
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onKeyPressing(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            boolean flag = false;
            List<KeyBinding> keys = KeyManager.getHotKeys();
            Manager manager = Manager.getManager();
            ClientSetting setting = ClientSetting.getInstance();
            for (int i = 0; i < 9; i ++){
                if (keys.get(i).isPressed()){
                    String key = manager.getKeyInBar(i + setting.getSize() * 9);
                    if (key != null && !key.isEmpty()) {
                        flag = true;
                        if (last == i) {
                            ticked += 1;
                            if (ticked >= 5) {
                                PackageSender.send(CPackage.BUILDER.buildCast(key));
                                ticked = 0;
                            }
                        } else {
                            ticked = 0;
                            last = i;
                        }
                        break;
                    }
                }
            }
            if (!flag) {
                ticked = 0;
                last = -1;
            }
        }
    }
}
