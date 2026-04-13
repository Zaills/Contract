package net.zaills.contract.packet;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.zaills.contract.Contract;
import net.zaills.contract.item.BaseContract;

import java.util.UUID;

public class ModPacket {

    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(OpenContractScreenPayload.ID, OpenContractScreenPayload.CODEC);

        PayloadTypeRegistry.playC2S().register(ContractPayload.ID, ContractPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ContractPayload.ID, (payload, context) -> {
            ServerPlayer sender = context.player();

            UUID contractee = payload.contractee();
            UUID contractor = payload.contractor();

            context.server().execute(() -> {
                ItemStack offhandStack = sender.getOffhandItem();

                if (offhandStack.is(Items.PAPER)) {
                    offhandStack.shrink(1);

                    ServerPlayer target = context.server().getPlayerList().getPlayer(contractee);
                    ServerPlayer runner = context.server().getPlayerList().getPlayer(contractor);
                    if (target != null && runner != null) {
                        String blockString = payload.blockId();
                        ItemStack newContract;
                        if (blockString.equals("non_aggression")) {
                            newContract = BaseContract.createNAGRESSIONContract(
                                    runner.getGameProfile(),
                                    target.getGameProfile()
                            );
                        } else {
                            int amount = payload.amount();
                            Contract.LOGGER.info("New Contract between: " + runner.getScoreboardName() + " and " + target.getScoreboardName());
                            Contract.LOGGER.info("Block: " + blockString + " -> " + amount);
                            newContract = BaseContract.createBlockContract(
                                    runner.getGameProfile(),
                                    target.getGameProfile(),
                                    blockString,
                                    amount
                            );
                        }

                        if (runner.getInventory().getFreeSlot() != -1) {
                            runner.getInventory().add(newContract);
                        } else {
                            Level level = runner.level();
                            ItemEntity itemEntity = new ItemEntity(level,
                                    runner.getX(), runner.getY() + 1, runner.getZ(), newContract
                            );
                            level.addFreshEntity(itemEntity);
                        }

                    } else {
                        Contract.LOGGER.debug("Contract failed: No paper in offhand.");
                    }
                }
            });

        });
    }
}
