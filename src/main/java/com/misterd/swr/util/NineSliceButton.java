package com.misterd.swr.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class NineSliceButton extends Button {

    private final ResourceLocation texture;
    private final int texW;
    private final int texH;
    private final int corner;

    public NineSliceButton(int x, int y, int w, int h, Component label, OnPress onPress, ResourceLocation texture, int texW, int texH, int corner) {
        super(x, y, w, h, label, onPress, DEFAULT_NARRATION);
        this.texture = texture;
        this.texW = texW;
        this.texH = texH;
        this.corner = corner;
    }

    @Override
    public void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        blitNineSlice(gfx, getX(), getY(), getWidth(), getHeight());

        var font = Minecraft.getInstance().font;
        int labelColor = active ? 0x404040 : 0xA0A0A0;
        gfx.drawString(font, getMessage(), getX() + (getWidth() - font.width(getMessage())) / 2, getY() + (getHeight() - 8) / 2, labelColor, false);
    }

    private void blitNineSlice(GuiGraphics gfx, int x, int y, int w, int h) {
        int c = corner;
        int mw = texW - c * 2;
        int mh = texH - c * 2;
        int bw = w - c * 2;
        int bh = h - c * 2;

        gfx.blit(texture, x, y, c, c,0,0, c, c, texW, texH);
        gfx.blit(texture, x + w - c, y, c, c, c + mw, 0, c, c, texW, texH);
        gfx.blit(texture, x,y + h - c, c, c,0, c + mh, c, c, texW, texH);
        gfx.blit(texture, x + w - c, y + h - c, c, c, c + mw, c + mh, c, c, texW, texH);

        gfx.blit(texture, x + c, y, bw, c, c,0,mw, c, texW, texH);
        gfx.blit(texture, x + c, y + h - c, bw, c, c, c + mh, mw, c, texW, texH);
        gfx.blit(texture, x,y + c, c, bh, 0, c, c, mh, texW, texH);
        gfx.blit(texture, x + w - c, y + c, c, bh, c + mw, c, c, mh, texW, texH);
        gfx.blit(texture, x + c, y + c, bw, bh, c, c, mw, mh, texW, texH);
    }
}