package com.github.MrMks.skillbarmod.gui;

import com.github.MrMks.skillbarmod.KeyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiSkillSettings extends GuiContainer {

    private static final String TEXTURE_PATH = "skillbarmod:textures/gui/container/gui_setting.png";
    private static final ResourceLocation TEXTURE = new ResourceLocation(TEXTURE_PATH);

    private static final int BUTTON_UP = 0;
    private static final int BUTTON_DOWN = 1;

    private ContainerSkillSetting containerSkill;

    public GuiSkillSettings(ContainerSkillSetting inventorySlotsIn) {
        super(inventorySlotsIn);
        this.xSize = 176;
        this.ySize = 128;
        containerSkill = inventorySlotsIn;
    }

    @Override
    public void initGui() {
        super.initGui();
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        this.buttonList.add(new GuiButton(BUTTON_UP, offsetX + 132, offsetY + 4, 19, 12, "")
        {
            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY,float sd)
            {
                if (this.visible)
                {
                    GlStateManager.color(1.0F, 1.0F, 1.0F);
                    mc.getTextureManager().bindTexture(TEXTURE);

                    int x = mouseX - this.x, y = mouseY - this.y;

                    if (x >= 0 && y >= 0 && x < this.width && y < this.height)
                    {
                        this.drawTexturedModalRect(this.x, this.y, 176, 12, this.width, this.height);
                    }
                    else
                    {
                        this.drawTexturedModalRect(this.x, this.y, 176, 0, this.width, this.height);
                    }
                }
            }
        });
        this.buttonList.add(new GuiButton(BUTTON_DOWN, offsetX + 151, offsetY + 4, 19, 12, "")
        {
            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY, float sd)
            {
                if (this.visible)
                {
                    GlStateManager.color(1.0F, 1.0F, 1.0F);

                    mc.getTextureManager().bindTexture(TEXTURE);
                    int x = mouseX - this.x, y = mouseY - this.y;

                    if (x >= 0 && y >= 0 && x < this.width && y < this.height)
                    {
                        this.drawTexturedModalRect(this.x, this.y, 195, 12, this.width, this.height);
                    }
                    else
                    {
                        this.drawTexturedModalRect(this.x, this.y, 195, 0, this.width, this.height);
                    }
                }
            }
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX,mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title_own = I18n.format("gui.skillbar.title_own");
        this.fontRenderer.drawString(title_own, 6,8,0x404040);
        String title_bar = I18n.format("gui.skillbar.title_bar");
        this.fontRenderer.drawString(title_bar,6,92,0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);

        int offsetX = (this.width - this.xSize) / 2;
        int offsetY = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        if (slotIn != null)
        {
            slotId = slotIn.slotNumber;
        }
        EntityPlayer player = this.mc.player;
        player.openContainer.slotClick(slotId, mouseButton, type, player);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1 || keyCode == KeyManager.getSettingKey().getKeyCode())
        {
            this.mc.player.closeScreen();
        }

        this.checkHotbarKeys(keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id){
            case BUTTON_UP:
                containerSkill.pageUp();
                break;
            case BUTTON_DOWN:
                containerSkill.pageDown();
                break;
        }
    }
}
