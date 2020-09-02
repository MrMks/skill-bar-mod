package com.github.MrMks.skillbar.forge.listener;

import com.github.MrMks.skillbar.forge.BarControl;
import com.github.MrMks.skillbar.forge.gui.GuiSkillBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HOTBAR;

public class RenderListener {
    private final BarControl control;
    public RenderListener(BarControl control) {
        this.control = control;
    }

    // render Skill Bar
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderOverlay(RenderGameOverlayEvent.Post e){
        if ((e.isCancelable() && e.isCanceled()) || !control.isEnable() || control.isHide() || e.getType() != HOTBAR) return;
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        if (entity instanceof EntityPlayer && !((EntityPlayer) entity).isSpectator()) {
            new GuiSkillBar(control).render(e.getResolution());
        }
    }

    // translate overlays
    private boolean translated;
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderTranslate(RenderGameOverlayEvent.Pre e){
        if (!control.isEnable() || translated || control.isHide()) return;
        RenderGameOverlayEvent.ElementType type = e.getType();
        switch (type) {
            case HEALTH:
            case ARMOR:
            case FOOD:
            case HEALTHMOUNT:
            case EXPERIENCE:
            case JUMPBAR:
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0, -21.0, 0.0);
                translated = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderTranslate(RenderGameOverlayEvent.Post e){
        if (!translated) return;
        RenderGameOverlayEvent.ElementType type = e.getType();
        switch (type) {
            case HEALTH:
            case FOOD:
            case HEALTHMOUNT:
            case EXPERIENCE:
            case ARMOR:
            case JUMPBAR:
                GlStateManager.popMatrix();
                translated = false;
        }
    }
}
