package com.github.MrMks.skillbar.forge.listener;

import com.github.MrMks.skillbar.forge.BarControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class MainListener {

    private final BarControl control;
    public MainListener(BarControl control){
        this.control = control;
    }

    // handle playerDisconnect when player is disconnected by channel close but did not receive disconnect package
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldUnload(WorldEvent.Unload event){
        NetHandlerPlayClient client = Minecraft.getMinecraft().getConnection();
        if (client == null || !client.getNetworkManager().isChannelOpen()) {
            control.onDisable();
            control.cleanAll();
        }
    }

    // handle playerDisconnect
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event){
        control.onDisable();
        control.cleanAll();
    }

}
