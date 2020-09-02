package com.github.MrMks.skillbar.forge.gui;

import com.github.MrMks.skillbar.forge.BarControl;
import com.github.MrMks.skillbar.forge.KeyManager;
import com.github.MrMks.skillbar.forge.SkillStore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class GuiSkillBar extends GuiIngame {
    private static final String TEXTURE_PATH = "minecraft:textures/gui/widgets.png";
    private static final ResourceLocation TEXTURE = new ResourceLocation(TEXTURE_PATH);

    private final FontRenderer fr;
    private final RenderItem ir;
    private final List<ItemStack> ics = new ArrayList<>();
    private final List<Integer> cds = new ArrayList<>();
    private final String pageStr;

    public GuiSkillBar(BarControl control){
        super(Minecraft.getMinecraft());
        fr = getFontRenderer();
        ir = mc.getRenderItem();
        this.zLevel = -100.0f;

        ics.clear();
        cds.clear();
        if (control.isEnable()) {
            SkillStore store = control.getSkillStore();
            List<ItemStack> icList = store.getBarList(control.getPage());
            List<Integer> cdList = store.getSkillCooldown(control.getPage());
            for (int i = 0; i < 9; i++){
                ics.add(getOrDefault(icList, i, ItemStack.EMPTY));
                cds.add(getOrDefault(cdList, i, 0));
            }
        }
        pageStr = String.format("(%d/%d)",control.getPage() + 1, control.getServerSetting().getMaxPage() + 1);
    }

    private <T> T getOrDefault(List<T> list, int index, T def){
        T obj = list.get(index);
        return obj == null ? def : obj;
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
        for (int i = 0; i < 9; i++) {
            if (ics.get(i) == null) continue;
            ItemStack stack = ics.get(i);
            Integer cd = cds.get(i);
            if (cd == null) cd = 0;

            int x = xBase + 3 + i * 20;
            int y = yBase + 3;
            RenderHelper.enableGUIStandardItemLighting();
            ir.renderItemIntoGUI(stack, x, y);
            if (cd > 0) {
                String s = String.valueOf(cd);
                fr.drawStringWithShadow(s, (x + 19 - 2 - fr.getStringWidth(s)), (y + 6 + 3),16777215);
            }
            String keyboard = KeyManager.getHotKeys().get(i).getDisplayName();
            if (!keyboard.isEmpty()){
                keyboard = keyboard.replace("CTRL + ", "C").replace("SHIFT + ", "S").replace("ALT + ", "A");
                x = x - 2;
                y = y - 4;
                fr.drawStringWithShadow(keyboard, x, y, 0xDAA520);
            }
        }
        ir.zLevel = ir_z;
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
    }
}
