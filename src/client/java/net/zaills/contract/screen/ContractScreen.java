package net.zaills.contract.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.zaills.contract.component.Contract_Type;
import net.zaills.contract.packet.ContractPayload;

import java.util.Objects;
import java.util.UUID;

public class ContractScreen extends Screen {
    private PlayerSelectionWidget contractorWidget;
    private PlayerSelectionWidget contracteeWidget;
    private Contract_Type currentType = Contract_Type.Blocks;
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

        Button typeButton = Button.builder(getTranslation(currentType.name()), button -> {
            Contract_Type[] types = Contract_Type.values();
            this.currentType = types[(this.currentType.ordinal() + 1) % types.length];

            button.setMessage(getTranslation(currentType.name()));
            updateButtonState();

            updateWidget();
        }).bounds((this.width - 120) / 2, y, 120, 20).build();
        this.addRenderableWidget(typeButton);

        x = (this.width - 150) / 2;
        BIWidget = new BlockInputWidget(x, y + 15, 150, 80, this::updateButtonState);
        this.addRenderableWidget(BIWidget);

        this.sendButton = Button.builder(Component.literal("Send Contract"), button -> {
            UUID contractorId = this.contractorWidget.getSelectedPlayerId();
            UUID contracteeId = this.contracteeWidget.getSelectedPlayerId();
            String blockString = this.BIWidget.getBlockId();
            int amount = this.BIWidget.getAmount();

            if (canSendContract()) {
                if (currentType.equals(Contract_Type.Blocks))
                    ClientPlayNetworking.send(new ContractPayload(contractorId, contracteeId, blockString, amount));
                if (currentType.equals(Contract_Type.NON_AGGRESSION))
                    ClientPlayNetworking.send(new ContractPayload(contractorId, contracteeId, "non_aggression", 12));
                this.onClose();
            }
        }).bounds((this.width - 120) / 2, this.height - 30, 120, 20).build();

        this.addRenderableWidget(sendButton);
        updateButtonState();
    }

    private void updateWidget() {
        if (BIWidget != null) {
            boolean requiresBlocks = (this.currentType == Contract_Type.Blocks);
            BIWidget.visible = requiresBlocks;
            BIWidget.active = requiresBlocks;
        }
    }

    private boolean canSendContract() {
        if (contractorWidget ==null || contracteeWidget == null || BIWidget == null) return false;

        UUID contractorId = this.contractorWidget.getSelectedPlayerId();
        UUID contracteeId = this.contracteeWidget.getSelectedPlayerId();

        boolean playersValid = (contractorId != null && contracteeId != null && !contractorId.equals(contracteeId));

        if (!playersValid) return false;
        if (this.currentType.equals(Contract_Type.Blocks))
            return BIWidget.isValid();
        return true;
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


    private MutableComponent getTranslation(String name) {
        if (Objects.equals(name, "NON_AGGRESSION"))
            return Component.translatable("contract.type.nonagression");
        else if (Objects.equals(name, "Blocks"))
            return Component.translatable("contract.type.block");
        return Component.translatable("contract.type.block");
    }
}
