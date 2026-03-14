package net.zaills.contract.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import javax.swing.*;

public class Quill extends Item {
    public Quill(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (level.isClientSide()) return InteractionResult.PASS;

        return super.use(level, player, interactionHand);
    }
}
