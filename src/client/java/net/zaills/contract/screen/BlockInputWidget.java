package net.zaills.contract.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.awt.*;

public class BlockInputWidget extends AbstractWidget {
    private final EditBox blockInput;
    private final EditBox amountInput;
    private final Font font;

    private final Runnable onUpdate;

    public BlockInputWidget(int x, int y, int width, int height, Runnable onUpdate) {
        super(x, y, width, height, Component.empty());
        this.font = Minecraft.getInstance().font;
        this.onUpdate = onUpdate;


        int centerX = x + (this.width - 150) / 2;

        this.blockInput = new EditBox(this.font,  centerX, y + 20, 150, 20, Component.literal("Block"));
        this.blockInput.setValue("minecraft:");
        this.blockInput.setResponder(text -> {
            if (isValidBlock(text)) {
                this.blockInput.setTextColor(0xfcf9fa);
            } else {
                this.blockInput.setTextColor(0xFF5555);
            }
            this.onUpdate.run();
        });

        this.amountInput = new EditBox(this.font, centerX, y + 55, width, 20, Component.literal("Amount"));
        this.amountInput.setFilter(s -> s.isEmpty() || s.matches("^[1-9]\\d*$"));
        this.amountInput.setValue("1");
        this.amountInput.setResponder(text -> this.onUpdate.run());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.drawCenteredString(this.font, "Block: ", getX() + (getWidth() / 2), this.blockInput.getY() - 10, 0xfcf9fa);
        guiGraphics.drawCenteredString(this.font, "Amount: ", getX() + (getWidth() / 2), this.amountInput.getY() - 10, 0xfcf9fa);

        this.blockInput.render(guiGraphics, i, j, f);
        this.amountInput.render(guiGraphics, i, j, f);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        boolean clickBlock = this.blockInput.mouseClicked(mouseButtonEvent, bl);
        boolean clickAmount = this.amountInput.mouseClicked(mouseButtonEvent, bl);

        if (clickBlock) {
            this.blockInput.setFocused(true);
            this.amountInput.setFocused(false);
        } else if (clickAmount) {
            this.blockInput.setFocused(false);
            this.amountInput.setFocused(true);
        }

        return clickBlock || clickAmount || super.mouseClicked(mouseButtonEvent, bl);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        return this.blockInput.keyPressed(keyEvent) ||
            this.amountInput.keyPressed(keyEvent) ||
            super.keyPressed(keyEvent);
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        return this.blockInput.charTyped(characterEvent) ||
                this.amountInput.charTyped(characterEvent) ||
                super.charTyped(characterEvent);
    }

    private boolean isValidBlock(String text) {
        Identifier id = Identifier.tryParse(text);
        if (id == null) return false;
        return BuiltInRegistries.BLOCK.containsKey(id);
    }

    public String getBlockId() {
        return this.blockInput.getValue();
    }

    public int getAmount() {
        System.out.println(Integer.parseInt(this.amountInput.getValue()));
        return this.amountInput.getValue().isEmpty() ? 0 : Integer.parseInt(this.amountInput.getValue());
    }

    public boolean isValid() {
        return isValidBlock(getBlockId()) && getAmount() > 0;
    }
}
