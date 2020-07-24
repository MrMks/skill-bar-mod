package com.github.MrMks.skillbar.forge.listener;

import com.github.MrMks.skillbar.forge.gui.GuiSkillBar;
import com.github.MrMks.skillbar.forge.setting.ClientSetting;
import com.github.MrMks.skillbar.forge.skill.Manager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class MainListener {
    //private GameSetting setting;


    // handle playerDisconnect when player is disconnected by channel close but did not receive disconnect package
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldUnload(WorldEvent.Unload event){
        NetHandlerPlayClient client = Minecraft.getMinecraft().getConnection();
        if (client == null || !client.getNetworkManager().isChannelOpen()) {
            getSetting().setHide(false);
            Manager.clean();
        }
    }

    // handle playerDisconnect
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event){
        ClientSetting.clean();
        Manager.clean();
    }

    // render Skill Bar
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderOverlay(RenderGameOverlayEvent.Post e){
        if (e.isCancelable() && e.isCanceled()) return;
        if (getSetting().isHide()) return;
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;
        if (player.isSpectator()) return;

        if (e.getType() == RenderGameOverlayEvent.ElementType.HOTBAR){
            Manager manager = Manager.getManager();
            if (manager.isActive()) {
                new GuiSkillBar(manager).render(e.getResolution());
            }
        }
    }

    // translate overlays
    private boolean translated;
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderTranslate(RenderGameOverlayEvent.Pre e){
        if (!Manager.isEnable() || translated || getSetting().isHide()) return;
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

    private ClientSetting getSetting(){
        return ClientSetting.getInstance();
    }
}
