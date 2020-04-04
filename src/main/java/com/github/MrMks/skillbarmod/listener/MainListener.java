package com.github.MrMks.skillbarmod.listener;

import com.github.MrMks.skillbarmod.GameSetting;
import com.github.MrMks.skillbarmod.gui.GuiSkillBar;
import com.github.MrMks.skillbarmod.skill.Manager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class MainListener {
    private GameSetting setting;
    public MainListener(GameSetting setting){
        this.setting = setting;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event){
        if (!setting.isShow()) setting.toggle();
        GuiSkillBar.clean();
        Manager.clean();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderOverlay(RenderGameOverlayEvent.Post e){
        if (e.isCancelable() && e.isCanceled()) return;
        if (!setting.isShow()) return;

        if (e.getType() == RenderGameOverlayEvent.ElementType.HOTBAR){
            Manager manager = Manager.getManager();
            if (manager.isActive()) {
                GuiSkillBar.getInstance(manager).render(e.getResolution());
            } else GuiSkillBar.clean();
        }
    }

    private boolean translated;
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderTranslate(RenderGameOverlayEvent.Pre e){
        if (!Manager.isEnable() || translated || !setting.isShow()) return;
        RenderGameOverlayEvent.ElementType type = e.getType();
        boolean flag = type == RenderGameOverlayEvent.ElementType.HEALTH
                || type == RenderGameOverlayEvent.ElementType.FOOD
                || type == RenderGameOverlayEvent.ElementType.HEALTHMOUNT
                || type == RenderGameOverlayEvent.ElementType.EXPERIENCE
                || type == RenderGameOverlayEvent.ElementType.ARMOR;
        if (flag){
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0, -21.0, 0.0);
            translated = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderTranslate(RenderGameOverlayEvent.Post e){
        if (!translated) return;
        RenderGameOverlayEvent.ElementType type = e.getType();
        boolean flag = type == RenderGameOverlayEvent.ElementType.HEALTH
                || type == RenderGameOverlayEvent.ElementType.FOOD
                || type == RenderGameOverlayEvent.ElementType.HEALTHMOUNT
                || type == RenderGameOverlayEvent.ElementType.EXPERIENCE
                || type == RenderGameOverlayEvent.ElementType.ARMOR;
        if (flag){
            GlStateManager.popMatrix();
            translated = false;
        }
    }

}
