package com.github.MrMks.skillbar.forge;

import com.github.MrMks.skillbar.common.Constants;
import com.github.MrMks.skillbar.forge.gui.GuiHandler;
import com.github.MrMks.skillbar.forge.listener.KeyListener;
import com.github.MrMks.skillbar.forge.listener.MainListener;
import com.github.MrMks.skillbar.forge.pkg.PackageHandler;
import com.github.MrMks.skillbar.forge.pkg.PackageMessage;
import com.github.MrMks.skillbar.forge.pkg.PackageSender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
@Mod(modid = "skillbarmod",name = "SkillBar Mod",version = "3", clientSideOnly = true)
public class SkillBarMod {
    @Mod.EventHandler
    public void postInit(FMLInitializationEvent e){
        // GameSetting generate;
        GameSetting setting = GameSetting.getInstance();

        // KeyBinding registry
        KeyManager.register();

        // Listener registry
        MinecraftForge.EVENT_BUS.register(new MainListener(setting));
        MinecraftForge.EVENT_BUS.register(new KeyListener(this,setting));

        // NetWrapper registry
        SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.CHANNEL_NAME);
        wrapper.registerMessage(new PackageHandler(),PackageMessage.class,Constants.DISCRIMINATOR, Side.CLIENT);
        PackageSender.setWrapper(wrapper);
        NetworkRegistry.INSTANCE.registerGuiHandler(this,new GuiHandler(setting));
    }
}
