package net.zaills.contract.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.zaills.contract.Contract;
import net.zaills.contract.component.Contract_Type;
import net.zaills.contract.component.ModComponents;
import net.zaills.contract.record.ContractData;
import net.zaills.contract.record.ContractManager;
import org.jspecify.annotations.Nullable;

import java.util.UUID;


public class BaseContract extends Item {
    public BaseContract(Properties properties) {
        super(properties.component(ModComponents.CONTRACT_TYPE, Contract_Type.Blocks.ordinal()));
    }

    public static ItemStack createNAGRESSIONContract(GameProfile contractor, GameProfile contractee) {
        ItemStack contract = new ItemStack(ModItem.BASE_CONTRACT);

        contract.set(ModComponents.CONTRACTOR, contractor.id().toString());
        contract.set(ModComponents.CONTRACTEE, contractee.id().toString());
        contract.set(ModComponents.CONTRACT_TYPE, Contract_Type.NON_AGGRESSION.ordinal());
        contract.set(ModComponents.SIGNED, Boolean.FALSE);
        contract.set(ModComponents.AMOUNT, 1);

        return contract;

    }

    public static ItemStack createBlockContract(GameProfile contractor, GameProfile contractee, String option, int amount) {
        ItemStack contract = new ItemStack(ModItem.BASE_CONTRACT);

        contract.set(ModComponents.CONTRACTOR, contractor.id().toString());
        contract.set(ModComponents.CONTRACTEE, contractee.id().toString());
        contract.set(ModComponents.CONTRACT_TYPE, Contract_Type.Blocks.ordinal());
        contract.set(ModComponents.SIGNED, Boolean.FALSE);
        contract.set(ModComponents.OPTION, option);
        contract.set(ModComponents.AMOUNT, amount);

        return contract;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (level.isClientSide()) return InteractionResult.PASS;
        ItemStack item = player.getItemInHand(interactionHand);
        String contractorId = item.get(ModComponents.CONTRACTOR);
        String contracteeId = item.get(ModComponents.CONTRACTEE);

        if ((contractorId == null && contracteeId == null ) ||
                !item.has(ModComponents.SIGNED)
        ) {
            return InteractionResult.PASS;
        }

        boolean signed = Boolean.TRUE.equals(item.get(ModComponents.SIGNED));
        if (!item.has(ModComponents.CONTRACT_TYPE)) return InteractionResult.PASS;
        int type = item.get(ModComponents.CONTRACT_TYPE);
        String option = item.get(ModComponents.OPTION);
        Player contractee = level.getPlayerByUUID(UUID.fromString(contracteeId));
        if (player.equals(contractee)) {
            Contract.LOGGER.debug(contractee.getName() + " signed the contract");
            item.set(ModComponents.SIGNED, Boolean.TRUE);
        }
        Player contractor = level.getPlayerByUUID(UUID.fromString(contractorId));
        if (player.equals(contractor) && signed ) {
            Contract.LOGGER.debug(contractor.getName() + " signed the contract");
            item.remove(ModComponents.SIGNED);
            ContractData data = null;
            if (type == Contract_Type.Blocks.ordinal()) {
                int amount = item.get(ModComponents.AMOUNT);
                data = new ContractData(
                    contractor.getGameProfile().id(),
                    contractee.getGameProfile().id(),
                    option,
                    amount
                );
            } else if (type == Contract_Type.NON_AGGRESSION.ordinal()) {
                data = new ContractData(
                    contractor.getGameProfile().id(),
                    contractee.getGameProfile().id(),
                    "non_aggression",
                    1
                );
            }

            UUID newContractId = UUID.randomUUID();
            item.set(ModComponents.ID, newContractId.toString());
            ContractManager manager = ContractManager.getServerState(level.getServer());
            manager.addContract(newContractId, data);

        }

        return super.use(level, player, interactionHand);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel serverLevel, Entity entity, @Nullable EquipmentSlot equipmentSlot) {
        if (itemStack.has(ModComponents.ID)) {
            UUID id = UUID.fromString(itemStack.get(ModComponents.ID));
            ContractManager manager = ContractManager.getServerState(serverLevel.getServer());
            ContractData data = manager.getContract(id);
            if (data != null){
                int amount = data.amount();
                if (amount <= 0)
                    manager.removeContract(id);
                else if (amount != itemStack.get(ModComponents.AMOUNT))
                    itemStack.set(ModComponents.AMOUNT, amount);
            } else {
                itemStack.grow(-1);
            }
        }
        super.inventoryTick(itemStack, serverLevel, entity, equipmentSlot);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return !itemStack.has(ModComponents.SIGNED);
    }
}
