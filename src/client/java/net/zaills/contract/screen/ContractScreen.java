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

        this.sendButton = Button.builder(Component.literal("Send Contract"), button -> {
            UUID contractorId = this.contractorWidget.getSelectedPlayerId();
            UUID contracteeId = this.contracteeWidget.getSelectedPlayerId();

            if (contractorId != null && contracteeId != null && !contractorId.equals(contracteeId)) {
                ClientPlayNetworking.send(new ContractPayload(contractorId, contracteeId));
                this.onClose();
            }
        }).bounds((this.width - 120) / 2, this.height - 30, 120, 20).build();

        this.addRenderableWidget(sendButton);
        updateButtonState();
    }

    private void updateButtonState() {
        if (contractorWidget == null || contracteeWidget == null || sendButton == null) return;

        UUID id1 = contractorWidget.getSelectedPlayerId();
        UUID id2 = contracteeWidget.getSelectedPlayerId();

        sendButton.active = (id1 != null && id2 != null && !id1.equals(id2));
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
