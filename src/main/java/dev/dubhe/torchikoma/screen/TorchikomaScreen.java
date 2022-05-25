package dev.dubhe.torchikoma.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.dubhe.torchikoma.Torchikoma;
import dev.dubhe.torchikoma.menu.TorchikomaMenu;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TorchikomaScreen extends AbstractDepartInvScreen<TorchikomaMenu> {
    private static final ResourceLocation BACKGROUND = Torchikoma.getId("textures/gui/torchikoma.png");

    private final TorchikomaButton[] buttons = new TorchikomaButton[3];

    public TorchikomaScreen(TorchikomaMenu pMenu, Inventory inventory, Component pTitle) {
        super(pMenu, inventory, pTitle, 178, 106);
        this.inventoryLabelY += 48;
        this.titleLabelY += 66;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(8, 83, new TranslatableComponent("torchikoma.mode.following"), 0);
        this.addButton(26, 83, new TranslatableComponent("torchikoma.mode.situ"), 1);
        this.addButton(44, 83, new TranslatableComponent("torchikoma.mode.standby"), 2);
        this.updateButtons();
    }

    @SuppressWarnings("SameParameterValue")
    private void addButton(int pX, int pY, Component tooltip, int index) {
        TorchikomaButton button = new TorchikomaButton(this.leftPos + pX, this.topPos + pY, tooltip, index);
        this.addRenderableWidget(button);
        this.buttons[index] = button;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.updateButtons();
    }

    private void updateButtons() {
        int index = this.menu.getEntity().getStatus();
        for (TorchikomaButton button : this.buttons) {
            button.setSelected(button.index == index);
        }
    }

    @Override
    protected void renderTooltip(PoseStack pPoseStack, int pX, int pY) {
        super.renderTooltip(pPoseStack, pX, pY);
        for (TorchikomaButton button : buttons) {
            if (button.isHoveredOrFocused()) this.renderTooltip(pPoseStack, button.tooltip, pX, pY);
        }
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);
        byte status = this.menu.getEntity().getStatus();
        int healLength = (int) (80 * this.menu.getEntity().getHealth() / this.menu.getEntity().getMaxHealth());
        int energyLength = (int) (80 * this.menu.getEntity().getEnergy() / 200.0F);
        if (status < 0 || status > 2) throw new IllegalArgumentException("Invalid status index: " + status);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        this.blit(pPoseStack, this.leftPos, this.topPos, 0, 0, this.menuWidth, this.menuHeight); // 背景板
        for (int i = 0; i < 3; i++) {
            this.renderItemBg(pPoseStack, this.leftPos + 9, this.topPos + i * 18 + 16, this.menuWidth, i * 16, i); // 物品背景
        }

        this.blit(pPoseStack, this.leftPos + 89, this.topPos + 84, 54, this.menuHeight, energyLength, 4); // 血条
        this.blit(pPoseStack, this.leftPos + 89, this.topPos + 94, 54, this.menuHeight + 4, healLength, 4); // 能量条
        // 51 60
        InventoryScreen.renderEntityInInventory(this.leftPos + 62, this.topPos + 57, 17, this.leftPos + 62 - pMouseX, this.topPos + 42 - pMouseY, this.menu.getEntity());
    }

    protected void renderItemBg(PoseStack pPoseStack, int x, int y, int u, int v, int index) {
        if (this.menu.isEmpty(index + 12)) {
            this.blit(pPoseStack, x, y, u, v, 16, 16);
        }
    }

    class TorchikomaButton extends AbstractButton {
        private final Component tooltip;
        private final int index;
        private boolean isSelected;
        public TorchikomaButton(int pX, int pY, Component tooltip, int index) {
            super(pX, pY, 18, 16, TextComponent.EMPTY);
            this.tooltip = tooltip;
            this.index = index;
        }

        @Override
        public void onPress() {
            if (!this.isSelected) TorchikomaScreen.this.menu.getEntity().setStatus((byte) this.index);
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, BACKGROUND);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.blit(pPoseStack, this.x, this.y, this.index * 18, this.isSelected ? 106 : 122, this.width, this.height);
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
            this.defaultButtonNarrationText(pNarrationElementOutput);
        }

        public void setSelected(boolean selected) {
            this.isSelected = selected;
        }
    }

}
