package net.zaills.contract.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.zaills.contract.record.ContractData;
import net.zaills.contract.record.ContractManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin
        extends Entity {

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    private int pickupDelay;

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void sendContractItem(Player player, CallbackInfo ci) {
        if (player.level().isClientSide() || player.isSpectator() || this.pickupDelay > 0)
            return;

        ItemStack itemStack = this.getItem();
        if (itemStack.isEmpty()) return;

        MinecraftServer server = player.level().getServer();
        ContractManager manager = ContractManager.getServerState(server);

        List<Map.Entry<UUID, ContractData>> contracts = manager.getPlayerContractEntries(player.getUUID());
        if (contracts.isEmpty()) return;

        for (Map.Entry<UUID, ContractData> entry : contracts.reversed()) {
            UUID contractId = entry.getKey();
            ContractData data = entry.getValue();
            Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(data.option()));

            if (itemStack.is(item)) {
                int amountSend = sendItem(data.contractorId(), player.level().getServer(), data.amount());

                if (amountSend > 0) {
                    int nAmount = data.amount() - amountSend;

                    ContractData upData = new ContractData(
                            data.contractorId(),
                            data.contracteeId(),
                            data.option(),
                            nAmount
                    );
                    manager.removeContract(contractId);
                    manager.addContract(contractId, upData);
                }
                if (itemStack.isEmpty()) {
                    this.discard();
                    ci.cancel();
                    return;
                }
            }
        }
    }

    @Unique
    private int sendItem(UUID playerId, MinecraftServer server, int amount) {
        ServerPlayer contractor = server.getPlayerList().getPlayer(playerId);
        if (contractor == null || amount <= 0) return 0;

        ItemStack itemStack = this.getItem();

        int takeAmount = Math.min(itemStack.getCount(), amount);
        ItemStack sendStack = itemStack.split(takeAmount);

        contractor.getInventory().add(sendStack);

        int itemGiven = takeAmount - sendStack.getCount();

        if (itemGiven > 0) {
            Item item = itemStack.getItem();
            contractor.take((ItemEntity) (Object) this, itemGiven);
            contractor.awardStat(Stats.ITEM_PICKED_UP.get(item), itemGiven);
        }

        if (!sendStack.isEmpty())
            itemStack.grow(sendStack.getCount());
        return itemGiven;
    }

}
