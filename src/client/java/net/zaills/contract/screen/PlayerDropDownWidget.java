package net.zaills.contract.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class PlayerDropDownWidget extends AbstractWidget {
    private final List<UUID> players;
    private final Function<UUID, String> nameResolver;
    private final Consumer<UUID> onSelect;
    private int index = 0;
    private boolean expanded = false;

    public PlayerDropDownWidget(int x, int y, int width, int height,
                                List<UUID> players, Function<UUID, String> nameResolver,
                                Consumer<UUID> onSelect) {
        super(x, y, width, height, Component.empty());
        this.players = players;
        this.onSelect = onSelect;
        this.nameResolver = nameResolver;

    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int x, int y, float delta) {
        Minecraft minecraft = Minecraft.getInstance();
        int color = isHovered() ? 0xFF555555 : 0xFF333333;
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color);

        String player = players.isEmpty() ? "None" : nameResolver.apply(players.get(index));
        guiGraphics.drawString(minecraft.font,
                player + (expanded ? " ▲" : " ▼"),
                getX() + 4, getY() + (getHeight() - 8) / 2,
                0xFFFFFFFF
        );

        if (expanded) {
            int visible = Math.min(players.size(), 5);
            int listY = getY() + getHeight();

            for (int i = 0; i < visible; i++) {
                int itemY = listY + i * 16;
                boolean hovered = x >= getX() && x <= getX() + getWidth()
                        && y >= itemY && y < itemY + 16;

                guiGraphics.fill(getX(), itemY, getX() + getWidth(), itemY + 16,
                        hovered ? 0xFF666666 : 0xFF444444);
                guiGraphics.drawString(minecraft.font, nameResolver.apply(players.get(i)),
                        getX() + 4, itemY + (16 - 8) / 2,
                        i == index ? 0xFFFFD700 : 0xFFFFFFFF
                );
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        double x = mouseButtonEvent.x();
        double y = mouseButtonEvent.y();

        if (x >= getX() && x <= getX() + getWidth()
                && y >= getY() && y <= getY() + getHeight()) {
            expanded = !expanded;
            playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }

        if (expanded) {
            int visible = Math.min(players.size(), 5);
            int listY = getY() + getHeight();

            for (int i = 0; i < visible; i++) {
                int itemY = listY + i * 16;
                if (x >= getX() && x <= getX() + getWidth()
                        && y >= itemY && y < itemY + 16) {
                    index = i;
                    expanded = false;
                    setMessage(Component.literal(nameResolver.apply(players.get(index))));
                    onSelect.accept(players.get(index));

                    expanded = false;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!this.active || !this.visible) {
            return false;
        }

        boolean overMainButton = mouseX >= getX() && mouseX <= getX() + getWidth()
                && mouseY >= getY() && mouseY <= getY() + getHeight();

        boolean overExpandedList = false;
        if (expanded) {
            int visible = Math.min(players.size(), 5);
            int listY = getY() + getHeight();
            int expandedHeight = visible * 16;

            overExpandedList = mouseX >= getX() && mouseX <= getX() + getWidth()
                    && mouseY >= listY && mouseY <= listY + expandedHeight;
        }

        return overMainButton || overExpandedList;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        defaultButtonNarrationText(narrationElementOutput);
    }
}
