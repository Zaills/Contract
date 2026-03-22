package net.zaills.contract.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.zaills.contract.packet.ContractPayload;

import java.util.UUID;

public class ContractScreen extends Screen {
    private PlayerSelectionWidget contractorWidget;
    private PlayerSelectionWidget contracteeWidget;
    private BlockInputWidget BIWidget;
    private Button sendButton;

    public ContractScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        int widgetSize = 128;
        int y = this.height / 5;

        int x = this.width / 10;
        contractorWidget = new PlayerSelectionWidget(x, y, widgetSize, widgetSize, true);
        this.addRenderableWidget(contractorWidget);

        x = (this.width / 10) * 7;
        contracteeWidget = new PlayerSelectionWidget(x, y, widgetSize, widgetSize, false);
        this.addRenderableWidget(contracteeWidget);

        x = (this.width - 150) / 2;
        BIWidget = new BlockInputWidget(x, y + 15, 150, 80, this::updateButtonState);
        this.addRenderableWidget(BIWidget);

        this.sendButton = Button.builder(Component.literal("Send Contract"), button -> {
            UUID contractorId = this.contractorWidget.getSelectedPlayerId();
            UUID contracteeId = this.contracteeWidget.getSelectedPlayerId();
            String blockString = this.BIWidget.getBlockId();
            int amount = this.BIWidget.getAmount();

            if (canSendContract()) {
                ClientPlayNetworking.send(new ContractPayload(contractorId, contracteeId, blockString, amount));
                this.onClose();
            }
        }).bounds((this.width - 120) / 2, this.height - 30, 120, 20).build();

        this.addRenderableWidget(sendButton);
        updateButtonState();
    }

    private boolean canSendContract() {
        if (contractorWidget ==null || contracteeWidget == null || BIWidget == null) return false;

        UUID contractorId = this.contractorWidget.getSelectedPlayerId();
        UUID contracteeId = this.contracteeWidget.getSelectedPlayerId();

        boolean playersValid = (contractorId != null && contracteeId != null && !contractorId.equals(contracteeId));

        return playersValid && BIWidget.isValid();
    }

    private void updateButtonState() {
        if (sendButton != null) {
            sendButton.active = canSendContract();
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        boolean result = super.mouseClicked(mouseButtonEvent, bl);
        updateButtonState();
        return result;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
    }
}
