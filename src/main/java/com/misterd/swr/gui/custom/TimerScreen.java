package com.misterd.swr.gui.custom;

import com.misterd.swr.SimpleWirelessRedstone;
import com.misterd.swr.blockentity.custom.TimerBlockEntity;
import com.misterd.swr.network.SetIntervalPacket;
import com.misterd.swr.network.SetRunningPacket;
import com.misterd.swr.util.NineSliceButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class TimerScreen extends AbstractContainerScreen<TimerMenu> {

    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SimpleWirelessRedstone.MODID, "textures/gui/channel_gui.png");
    private static final ResourceLocation BTN_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SimpleWirelessRedstone.MODID, "textures/gui/btn_bg.png");

    private static final WidgetSprites SPRITES_RUNNING = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(SimpleWirelessRedstone.MODID, "on_btn"),
            ResourceLocation.fromNamespaceAndPath(SimpleWirelessRedstone.MODID, "on_btn"),
            ResourceLocation.fromNamespaceAndPath(SimpleWirelessRedstone.MODID, "on_btn"),
            ResourceLocation.fromNamespaceAndPath(SimpleWirelessRedstone.MODID, "on_btn")
    );

    private static final WidgetSprites SPRITES_STOPPED = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(SimpleWirelessRedstone.MODID, "off_btn"),
            ResourceLocation.fromNamespaceAndPath(SimpleWirelessRedstone.MODID, "off_btn"),
            ResourceLocation.fromNamespaceAndPath(SimpleWirelessRedstone.MODID, "off_btn"),
            ResourceLocation.fromNamespaceAndPath(SimpleWirelessRedstone.MODID, "off_btn")
    );

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
    private static final int TOGGLE_X = 154;
    private static final int TOGGLE_Y = 74;
    private static final int TOGGLE_W = 12;
    private static final int TOGGLE_H = 12;

    private EditBox   intervalBox;
    private boolean   running;
    private ImageButton toggleBtn;

    public TimerScreen(TimerMenu menu, Inventory inventory, Component title) {
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

        if (intervalBox == null) running = menu.isRunning();

        intervalBox = new EditBox(font, left + EB_X, top + EB_Y, EB_W, EB_H,
                Component.translatable("gui.swr.timer.interval_box"));
        intervalBox.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
        intervalBox.setMaxLength(5);
        intervalBox.setValue(String.valueOf(menu.getInterval()));
        intervalBox.setBordered(false);
        addRenderableWidget(intervalBox);

        int[] incDeltas = {1, 5, 10};
        int incX = left + INC_X;
        for (int delta : incDeltas) {
            final int d = delta;
            Component label = Component.literal("+" + delta);
            int w = font.width(label) + CORNER * 2;
            addRenderableWidget(new NineSliceButton(incX, top + INC_Y, w, BTN_H,
                    label, btn -> adjustInterval(d), BTN_TEXTURE, BTN_TEX_W, BTN_TEX_H, CORNER));
            incX += w + BTN_GUTTER;
        }

        int[] decDeltas = {1, 5, 10};
        int decX = left + DEC_X;
        for (int delta : decDeltas) {
            final int d = delta;
            Component label = Component.literal("-" + delta);
            int w = font.width(label) + CORNER * 2;
            addRenderableWidget(new NineSliceButton(decX, top + DEC_Y, w, BTN_H,
                    label, btn -> adjustInterval(-d), BTN_TEXTURE, BTN_TEX_W, BTN_TEX_H, CORNER));
            decX += w + BTN_GUTTER;
        }

        Component resetLabel  = Component.translatable("gui.swr.timer.reset");
        Component setLabel    = Component.translatable("gui.swr.timer.set");
        Component cancelLabel = Component.translatable("gui.swr.timer.cancel");
        int rscW = font.width(cancelLabel) + CORNER * 2;
        int rscX = left + RSC_X;
        int rscY = top  + RSC_Y;

        addRenderableWidget(new NineSliceButton(rscX, rscY, rscW, BTN_H,
                resetLabel, btn -> onReset(), BTN_TEXTURE, BTN_TEX_W, BTN_TEX_H, CORNER));
        addRenderableWidget(new NineSliceButton(rscX, rscY + BTN_H + BTN_GUTTER, rscW, BTN_H,
                setLabel, btn -> onSet(), BTN_TEXTURE, BTN_TEX_W, BTN_TEX_H, CORNER));
        addRenderableWidget(new NineSliceButton(rscX, rscY + (BTN_H + BTN_GUTTER) * 2, rscW, BTN_H,
                cancelLabel, btn -> onCancel(), BTN_TEXTURE, BTN_TEX_W, BTN_TEX_H, CORNER));

        toggleBtn = new ImageButton(
                left + TOGGLE_X, top + TOGGLE_Y, TOGGLE_W, TOGGLE_H,
                running ? SPRITES_STOPPED : SPRITES_RUNNING,
                btn -> onToggle());
        addRenderableWidget(toggleBtn);
    }

    private void adjustInterval(int delta) {
        int next = Math.clamp(parseBox() + delta, TimerBlockEntity.MIN_INTERVAL, TimerBlockEntity.MAX_INTERVAL);
        intervalBox.setValue(String.valueOf(next));
    }

    private void onReset() {
        intervalBox.setValue(String.valueOf(TimerBlockEntity.DEFAULT_INTERVAL));
    }

    private void onSet() {
        PacketDistributor.sendToServer(new SetIntervalPacket(menu.getBlockPos(), parseBox()));
        this.onClose();
    }

    private void onCancel() {
        this.onClose();
    }

    private void onToggle() {
        running = !running;
        PacketDistributor.sendToServer(new SetRunningPacket(menu.getBlockPos(), running));
        init();
    }

    private int parseBox() {
        try {
            return Math.clamp(Integer.parseInt(intervalBox.getValue()),
                    TimerBlockEntity.MIN_INTERVAL, TimerBlockEntity.MAX_INTERVAL);
        } catch (NumberFormatException e) {
            return TimerBlockEntity.DEFAULT_INTERVAL;
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

        if (toggleBtn != null && toggleBtn.isHoveredOrFocused()) {
            gfx.renderTooltip(font,
                    Component.translatable(running ? "gui.swr.timer.running" : "gui.swr.timer.stopped"),
                    mouseX, mouseY);
        }
        if (intervalBox != null && intervalBox.isHoveredOrFocused()) {
            gfx.renderTooltip(font,
                    List.of(
                            Component.translatable("gui.swr.receiver.interval_box.tooltip.line1").withStyle(ChatFormatting.GOLD).getVisualOrderText(),
                            Component.translatable("gui.swr.receiver.interval_box.tooltip.line2").withStyle(ChatFormatting.ITALIC).getVisualOrderText()
                    ),
                    mouseX, mouseY);
        }
    }
}