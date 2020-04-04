package com.github.MrMks.skillbarmod;

import com.github.MrMks.skillbarmod.gui.GuiHandler;
import com.github.MrMks.skillbarmod.listener.KeyListener;
import com.github.MrMks.skillbarmod.listener.MainListener;
import com.github.MrMks.skillbarmod.pkg.PackageHandler;
import com.github.MrMks.skillbarmod.pkg.PackageMessage;
import com.github.MrMks.skillbarmod.pkg.PackageSender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
@Mod(modid = "skillbarmod",name = "SkillBar Mod",version = "2", clientSideOnly = true)
public class SkillBarMod {
    @Mod.EventHandler
    public void postInit(FMLInitializationEvent e){
        GameSetting setting = new GameSetting();
        MinecraftForge.EVENT_BUS.register(new MainListener(setting));
        MinecraftForge.EVENT_BUS.register(new KeyListener(this,setting));
        SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.CHANNEL_NAME);
        wrapper.registerMessage(new PackageHandler(),PackageMessage.class,Constants.DISCRIMINATOR, Side.CLIENT);
        PackageSender.setWrapper(wrapper);
        NetworkRegistry.INSTANCE.registerGuiHandler(this,new GuiHandler());
    }
}