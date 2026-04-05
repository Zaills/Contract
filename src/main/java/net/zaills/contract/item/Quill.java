package net.zaills.contract.item;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.zaills.contract.packet.OpenContractScreenPayload;

import javax.swing.*;

public class Quill extends Item {
    public Quill(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {

        ItemStack itemStack = player.getItemInHand(interactionHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {

            ItemStack offHandStack = player.getOffhandItem();

            if (offHandStack.is(Items.PAPER) || player.isCreative()) {
                itemStack.hurtAndBreak(1, serverPlayer, serverPlayer.getEquipmentSlotForItem(itemStack));
                player.awardStat(Stats.ITEM_USED.get(this));

                ServerPlayNetworking.send(serverPlayer, new OpenContractScreenPayload());

                return InteractionResult.SUCCESS;
            }
        }
        return super.use(level, player, interactionHand);
    }
}
