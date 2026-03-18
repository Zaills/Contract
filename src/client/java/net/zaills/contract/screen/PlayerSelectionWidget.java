package net.zaills.contract.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.zaills.contract.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerSelectionWidget extends AbstractWidget {
    private final Identifier BACK_TEXTURE;
    private final Identifier FRONT_TEXTURE;

    private int currentIndex = 0;
    private List<PlayerInfo> onlinePlayers = new ArrayList<>();

    public PlayerSelectionWidget(int x, int y, int height, int width, boolean isContractor) {
        super(x, y, height, width, Component.empty());String prefix = isContractor ? "contractor" : "contractee";
        this.BACK_TEXTURE = Identifier.fromNamespaceAndPath(Contract.MOD_ID, "textures/gui/" + prefix + "_back.png");
        this.FRONT_TEXTURE = Identifier.fromNamespaceAndPath(Contract.MOD_ID, "textures/gui/" + prefix + "_front.png");
        refreshPlayers();
    }

    private void refreshPlayers() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() != null) {
            this.onlinePlayers = new ArrayList<>(minecraft.getConnection().getOnlinePlayers());
        }
    }

    public UUID getSelectedPlayerId() {
        if (onlinePlayers.isEmpty()) return null;
        return onlinePlayers.get(currentIndex).getProfile().id();
    }

    @Override
    public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (!onlinePlayers.isEmpty()) {
            currentIndex = (currentIndex + 1) % onlinePlayers.size();
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACK_TEXTURE,
                getX(), getY(), 0, 0,
                getWidth(), getHeight(), getWidth(), getHeight()
        );

        if (!onlinePlayers.isEmpty()) {
            PlayerInfo player = onlinePlayers.get(currentIndex);

            int headSize = (int) (getHeight() / 3.5);
            int headX = getX() + (getWidth() - headSize) / 2;
            int headY = getY() + (getHeight() - headSize) / 2;

            PlayerFaceRenderer.draw(guiGraphics, player.getSkin(), headX, headY, headSize);
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, player.getProfile().name(),
                    getX() + (getWidth() / 2), getY() + getHeight(), 0xFFFFFFFF);
        }

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, FRONT_TEXTURE,
                getX(), getY(), 0, 0,
                getWidth(), getHeight(), getWidth(), getHeight()
        );
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
