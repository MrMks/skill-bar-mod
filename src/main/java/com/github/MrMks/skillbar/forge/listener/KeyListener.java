package com.github.MrMks.skillbar.forge.listener;

import com.github.MrMks.skillbar.common.pkg.CPackage;
import com.github.MrMks.skillbar.forge.BarControl;
import com.github.MrMks.skillbar.forge.KeyManager;
import com.github.MrMks.skillbar.forge.SkillBarMod;
import com.github.MrMks.skillbar.forge.pkg.PackageSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.List;

public class KeyListener {
    @Mod.Instance(value = "skillbarmod")
    private static SkillBarMod mod;
    private final HashMap<Integer, Integer> cdMap = new HashMap<>();
    private final BarControl control;

    public KeyListener(BarControl control) {
        this.control = control;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onKeyPress(InputEvent.KeyInputEvent event){
        if (!control.isEnable()) return;
        if (KeyManager.getToggleKey().isPressed()){
            control.switchShow();
        } else if (KeyManager.getSettingKey().isPressed()){
            Minecraft.getMinecraft().player.openGui(mod,2,Minecraft.getMinecraft().player.world,0,0,0);
        } else if (KeyManager.getBarPageUpKey().isPressed()) {
            control.previousPage();
        } else if (KeyManager.getBarPageDownKey().isPressed()) {
            control.nextPage();
        } else {
            List<KeyBinding> keys = KeyManager.getHotKeys();
            for (int i = 0; i < 9; i ++){
                if (keys.get(i).isPressed()){
                    String key = control.getKeyInBar(i);
                    if (key != null && !key.isEmpty()) {
                        PackageSender.send(CPackage.BUILDER.buildCast(key));
                        cdMap.put(i, 20);
                        break;
                    }
                }
            }
        }
    }

    // 长按持续释放，客户端每5tick每次
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onKeyPressing(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            List<KeyBinding> keys = KeyManager.getHotKeys();
            for (int i = 0; i < 9; i ++){
                boolean contain = cdMap.containsKey(i);
                if (keys.get(i).isKeyDown()){
                    String key = control.getKeyInBar(i);
                    if (key != null && !key.isEmpty()) {
                        if (!contain) {
                            PackageSender.send(CPackage.BUILDER.buildCast(key));
                            cdMap.put(i, 5);
                        }
                    }
                }
                if (contain) {
                    int cd = cdMap.remove(i) - 1;
                    if (cd > 0) cdMap.put(i, cd);
                }
            }
        }
    }
}
