package com.github.MrMks.skillbarmod.gui;

import com.github.MrMks.skillbarmod.GameSetting;
import com.github.MrMks.skillbarmod.skill.Manager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    private GameSetting setting;
    public GuiHandler(GameSetting setting){
        this.setting = setting;
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 2) {
            Manager manager = Manager.getManager();
            if (manager.isActive()){
                return new GuiSkillSettings(new ContainerSkillSetting(manager, setting));
            }
        }
        return null;
    }
}
