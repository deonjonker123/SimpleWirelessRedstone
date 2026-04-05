package com.misterd.swr.gui.custom;

import com.misterd.swr.SimpleWirelessRedstone;
import com.misterd.swr.network.SetChannelPacket;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.network.PacketDistributor;
import com.misterd.swr.util.NineSliceButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class WirelessTransmitterScreen extends AbstractContainerScreen<WirelessTransmitterMenu> {

    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SimpleWirelessRedstone.MODID, "textures/gui/channel_gui.png");
    private static final ResourceLocation BTN_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SimpleWirelessRedstone.MODID, "textures/gui/btn_bg.png");

    private static final int BG_W = 172;
    private static final int BG_H = 92;
    private static final int BTN_TEX_W = 236;
    private static final int BTN_TEX_H = 24;
    private static final int CORNER = 4;
    private static final int EB_X = 8;
    private static final int EB_Y = 48;
    private static final int EB_W = 69;
    private static final int EB_H = 10;
    private static final int BTN_H = 18;
    private static final int BTN_GUTTER = 2;
    private static final int INC_X = 7;
    private static final int INC_Y = 22;
    private static final int DEC_X = 7;
    private static final int DEC_Y = 62;
    private static final int RSC_X = 101;
    private static final int RSC_Y = 22;

    private EditBox channelBox;

    public WirelessTransmitterScreen(WirelessTransmitterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth  = BG_W;
        this.imageHeight = BG_H;
    }

    @Override
    protected void init() {
        super.init();

        int left = this.leftPos;
        int top  = this.topPos;
        this.inventoryLabelY = Integer.MAX_VALUE;

        channelBox = new EditBox(font, left + EB_X, top + EB_Y, EB_W, EB_H,
                Component.translatable("gui.swr.transmitter.channel_box"));
        channelBox.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
        channelBox.setMaxLength(10);
        int stored = menu.getChannel();
        channelBox.setValue(String.valueOf(stored == -1 ? 0 : stored));
        channelBox.setBordered(false);
        addRenderableWidget(channelBox);

        int[] incDeltas = {1, 5, 10};
        int incX = left + INC_X;
        for (int delta : incDeltas) {
            final int d = delta;
            Component label = Component.literal("+" + delta);
            int w = font.width(label) + CORNER * 2;
            addRenderableWidget(new NineSliceButton(incX, top + INC_Y, w, BTN_H,
                    label, btn -> adjustChannel(d), BTN_TEXTURE, BTN_TEX_W, BTN_TEX_H, CORNER));
            incX += w + BTN_GUTTER;
        }

        int[] decDeltas = {1, 5, 10};
        int decX = left + DEC_X;
        for (int delta : decDeltas) {
            final int d = delta;
            Component label = Component.literal("-" + delta);
            int w = font.width(label) + CORNER * 2;
            addRenderableWidget(new NineSliceButton(decX, top + DEC_Y, w, BTN_H,
                    label, btn -> adjustChannel(-d), BTN_TEXTURE, BTN_TEX_W, BTN_TEX_H, CORNER));
            decX += w + BTN_GUTTER;
        }

        Component resetLabel  = Component.translatable("gui.swr.transmitter.reset");
        Component setLabel    = Component.translatable("gui.swr.transmitter.set");
        Component cancelLabel = Component.translatable("gui.swr.transmitter.cancel");
        int rscW = font.width(cancelLabel) + CORNER * 2;
        int rscX = left + RSC_X;
        int rscY = top  + RSC_Y;

        addRenderableWidget(new NineSliceButton(rscX, rscY, rscW, BTN_H,
                resetLabel, btn -> onReset(), BTN_TEXTURE, BTN_TEX_W, BTN_TEX_H, CORNER));
        addRenderableWidget(new NineSliceButton(rscX, rscY + BTN_H + BTN_GUTTER, rscW, BTN_H,
                setLabel, btn -> onSet(), BTN_TEXTURE, BTN_TEX_W, BTN_TEX_H, CORNER));
        addRenderableWidget(new NineSliceButton(rscX, rscY + (BTN_H + BTN_GUTTER) * 2, rscW, BTN_H,
                cancelLabel, btn -> onCancel(), BTN_TEXTURE, BTN_TEX_W, BTN_TEX_H, CORNER));
    }

    private void adjustChannel(int delta) {
        int next = Math.max(0, parseBox() + delta);
        channelBox.setValue(String.valueOf(next));
    }

    private void onReset()  { channelBox.setValue("0"); }
    private void onCancel() { this.onClose(); }

    private void onSet() {
        PacketDistributor.sendToServer(new SetChannelPacket(menu.getBlockPos(), parseBox()));
        this.onClose();
    }

    private int parseBox() {
        try {
            return Math.max(0, Integer.parseInt(channelBox.getValue()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTick, int mouseX, int mouseY) {
        gfx.blit(BG_TEXTURE, leftPos, topPos, 0, 0, BG_W, BG_H, 256, 256);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        super.render(gfx, mouseX, mouseY, partialTick);
        renderTooltip(gfx, mouseX, mouseY);
        if (channelBox != null && channelBox.isHoveredOrFocused()) {
            gfx.renderTooltip(font,
                    List.of(
                            Component.translatable("gui.swr.transmitter.channel_box.tooltip.line1").withStyle(ChatFormatting.GOLD).getVisualOrderText(),
                            Component.translatable("gui.swr.transmitter.channel_box.tooltip.line2").withStyle(ChatFormatting.ITALIC).getVisualOrderText()
                    ),
                    mouseX, mouseY);
        }
    }
}