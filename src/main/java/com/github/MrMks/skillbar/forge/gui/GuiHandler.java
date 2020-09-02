package com.github.MrMks.skillbar.forge.gui;

import com.github.MrMks.skillbar.forge.BarControl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    private final BarControl control;
    public GuiHandler(BarControl control){
        this.control = control;
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
            if (control.isEnable()){
                return new GuiSkillSettings(new ContainerSkillSetting(control));
            }
        }
        return null;
    }
}
