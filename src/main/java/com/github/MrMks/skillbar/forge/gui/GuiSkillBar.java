package com.github.MrMks.skillbar.forge.gui;

import com.github.MrMks.skillbar.forge.KeyManager;
import com.github.MrMks.skillbar.forge.setting.ClientSetting;
import com.github.MrMks.skillbar.forge.setting.ServerSetting;
import com.github.MrMks.skillbar.forge.skill.Manager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiSkillBar extends GuiIngame {
    private static final String TEXTURE_PATH = "minecraft:textures/gui/widgets.png";
    private static final ResourceLocation TEXTURE = new ResourceLocation(TEXTURE_PATH);

    private FontRenderer fr;
    private RenderItem ir;
    public GuiSkillBar(Manager manager){
        super(Minecraft.getMinecraft());
        fr = getFontRenderer();
        ir = mc.getRenderItem();
        this.zLevel = -100.0f;

        init(manager, ClientSetting.getInstance().getSize(), ServerSetting.getInstance().getMaxSize());
    }

    private List<ItemStack> ics = new ArrayList<>();
    private String pageStr;
    private void init(@Nonnull Manager manager, int page, int max){
        if (manager.isActive()) {
            ics.clear();
            Map<Integer, ItemStack> map = manager.getBarIconMap();
            for (int i = 0; i < 9; i++){
                ics.add(map.getOrDefault(i + page * 9, ItemStack.EMPTY));
            }
        }
        else ics.clear();
        pageStr = String.format("(%d/%d)",page + 1, max + 1);
    }

    public void render(ScaledResolution sr){
        GlStateManager.pushMatrix();
        this.renderBackground(sr);
        this.renderItemStacks(sr);
        this.renderForeground(sr);
        GlStateManager.popMatrix();
    }

    private void renderForeground(ScaledResolution sr) {
        if (sr == null) return;
        int x = sr.getScaledWidth() / 2 -  91 - fr.getStringWidth(pageStr) - 2;
        int y = sr.getScaledHeight() - 43 + 10 - fr.FONT_HEIGHT / 2;

        fr.drawStringWithShadow(pageStr, x, y, 0xDAA520);
    }

    private void renderBackground(ScaledResolution sr){
        if (sr == null) return;
        int x = sr.getScaledWidth() / 2 - 91;
        int y = sr.getScaledHeight() - 43;

        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(TEXTURE);

        this.drawTexturedModalRect(x, y, 0, 0, 182, 22);
        GlStateManager.disableBlend();
    }

    private void renderItemStacks(ScaledResolution sr){
        if (sr == null) return;
        float ir_z = ir.zLevel;
        ir.zLevel = -90.0f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        int xBase = sr.getScaledWidth() / 2 - 91;
        int yBase = sr.getScaledHeight() - 43;
        int count = 0;
        for (ItemStack stack : ics){
            if (stack.isEmpty()) {
                count ++;
                continue;
            }
            int x = xBase + 3 + count * 20;
            int y = yBase + 3;
            RenderHelper.enableGUIStandardItemLighting();
            ir.renderItemIntoGUI(stack, x, y);
            if (stack.getCount() > 1) {
                String s = String.valueOf(stack.getCount());
                fr.drawStringWithShadow(s, (x + 19 - 2 - fr.getStringWidth(s)), (y + 6 + 3),16777215);
            }
            String keyboard = KeyManager.getHotKeys().get(count).getDisplayName();
            if (!keyboard.isEmpty()){
                keyboard = keyboard.replace("CTRL + ", "C").replace("SHIFT + ", "S").replace("ALT + ", "A");
                x = x - 2;
                y = y - 4;
                fr.drawStringWithShadow(keyboard, x, y, 0xDAA520);
            }
            count++;
        }
        ir.zLevel = ir_z;
    }
}
