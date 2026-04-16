package net.zaills.contract.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Pose;
import net.zaills.contract.component.Contract_Type;
import net.zaills.contract.packet.ContractPayload;

import java.util.*;
import java.util.function.Function;

public class ContractScreen extends Screen {
    private Contract_Type currentType = Contract_Type.Blocks;
    private BlockInputWidget BIWidget;
    private Button sendButton;

    private UUID contractor = null;
    private UUID contractee = null;

    private List<UUID> onlinePlayers = new ArrayList<>();

    private static final Identifier MAP_BACKGROUND = Identifier.withDefaultNamespace("textures/map/map_background.png");

    public ContractScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        if (minecraft.getConnection() != null) {
            onlinePlayers = minecraft.getConnection().getOnlinePlayerIds().stream().toList();
        }

        if (!onlinePlayers.isEmpty()) {
            contractor = onlinePlayers.getFirst();
            contractee = onlinePlayers.getFirst();
        }

        Function<UUID, String> nameOf = uuid -> {
            if (minecraft.getConnection() != null) {
                PlayerInfo playerInfo = minecraft.getConnection().getPlayerInfo(uuid);
                if (playerInfo != null) {
                    return playerInfo.getProfile().name();
                }
            }
            return "Disconnected";
        };

        int y = this.height / 5;
        int x = this.width / 10;
        PlayerDropDownWidget contractorDropDown1 = new PlayerDropDownWidget(x, y, 120, 18, onlinePlayers, nameOf, uuid -> contractor = uuid);
        this.addRenderableWidget(contractorDropDown1);

        x = (this.width / 10) * 7;
        PlayerDropDownWidget contractorDropDown2 = new PlayerDropDownWidget(x, y, 120, 18, onlinePlayers, nameOf, uuid -> contractee = uuid);
        this.addRenderableWidget(contractorDropDown2);

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
            String blockString = this.BIWidget.getBlockId();
            int amount = this.BIWidget.getAmount();

            if (canSendContract()) {
                if (currentType.equals(Contract_Type.Blocks))
                    ClientPlayNetworking.send(new ContractPayload(contractor, contractee, blockString, amount));
                if (currentType.equals(Contract_Type.NON_AGGRESSION))
                    ClientPlayNetworking.send(new ContractPayload(contractor, contractee, "non_aggression", 12));
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
        if (contractor == null || contractee == null || BIWidget == null || contractor.equals(contractee)) return false;

        if (minecraft.getConnection().getPlayerInfo(contractor) == null) return false;
        if (minecraft.getConnection().getPlayerInfo(contractee) == null) return false;

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
//        renderBackground(guiGraphics);

        super.render(guiGraphics, i, j, f);


        int y = this.height / 5 * 4;
        int x = this.width / 10;
        int scale = (int) (this.height * 0.18);

        renderPlayer(guiGraphics, x + 60, y, contractor, i, j, scale);
        renderPlayer(guiGraphics, x * 7 + 60, y, contractee, i, j, scale);

    }

    private void renderPlayer(GuiGraphics guiGraphics, int x, int y, UUID playerUUID, int mouseX, int mouseY, int scale) {
        if (playerUUID == null || Minecraft.getInstance().level == null) return;

        AbstractClientPlayer player = Minecraft.getInstance().level.players()
                .stream()
                .filter(p -> p.getUUID().equals(playerUUID))
                .findFirst()
                .orElse(null);

        if (player == null) return;

        RemotePlayer fakePlayer = new RemotePlayer(Minecraft.getInstance().level, player.getGameProfile());

        fakePlayer.setPose(Pose.STANDING);
        fakePlayer.setShiftKeyDown(false);
        fakePlayer.setSprinting(false);

        int x1 = x - scale;
        int y1 = y - (int)(scale * 2.2);
        int x2 = x + scale;
        int y2 = y + (int)(scale * 0.5);

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                guiGraphics,
                x1, y1,
                x2, y2,
                scale, 0.0625F,
                mouseX, mouseY,
                fakePlayer
        );
    }

    private void renderBackground(GuiGraphics guiGraphics) {
        int mapSize = (int) (this.height * 0.9);
        if (mapSize > this.width * 0.9) {
            mapSize = (int) (this.width * 0.9);
        }

        int x = (this.width - mapSize) / 2;
        int y = (this.height - mapSize) / 2;
        int texSize = 128;

        var pose = guiGraphics.pose();

        pose.pushMatrix();

        pose.translate(x, y);

        float scale = (float) mapSize / texSize;
        pose.scale(scale, scale);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MAP_BACKGROUND,
                0, 0,
                0, 0,
                texSize, texSize,
                texSize, texSize
        );

        pose.popMatrix();

    }

    private MutableComponent getTranslation(String name) {
        if (Objects.equals(name, "NON_AGGRESSION"))
            return Component.translatable("contract.type.nonagression");
        else if (Objects.equals(name, "Blocks"))
            return Component.translatable("contract.type.block");
        return Component.translatable("contract.type.block");
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
