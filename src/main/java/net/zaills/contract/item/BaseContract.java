package net.zaills.contract.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.zaills.contract.component.Contract_Type;
import net.zaills.contract.component.ModComponents;
import net.zaills.contract.record.ContractData;
import net.zaills.contract.record.ContractManager;

import java.util.UUID;


public class BaseContract extends Item {
    public BaseContract(Properties properties) {
        super(properties);
        properties.component(ModComponents.CONTRACT_TYPE, Contract_Type.BASE.ordinal());
    }

    public static ItemStack createContract(GameProfile contractor, GameProfile contractee) {
        ItemStack contract = new ItemStack(ModItem.BASE_CONTRACT);

        contract.set(ModComponents.CONTRACTOR, contractor.id().toString());
        contract.set(ModComponents.CONTRACTEE, contractee.id().toString());
        contract.set(ModComponents.CONTRACT_TYPE, Contract_Type.DeathExchange.ordinal());
        contract.set(ModComponents.SIGNED, Boolean.FALSE);

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
        boolean signed = item.get(ModComponents.SIGNED);
        int type = item.get(ModComponents.CONTRACT_TYPE);
        String option = item.get(ModComponents.OPTION);
        int amount = item.get(ModComponents.AMOUNT);

        if (contractorId != null && contracteeId != null) {
            Player contractee = level.getPlayerByUUID(UUID.fromString(contracteeId));
            if (player.equals(contractee)) {
                System.out.println(contractee.getName() + " signed the contract");
                item.set(ModComponents.SIGNED, Boolean.TRUE);
            }
            Player contractor = level.getPlayerByUUID(UUID.fromString(contractorId));
            if (player.equals(contractor) && signed) {
                System.out.println(contractor.getName() + " signed the contract");

                ContractData data = new ContractData(
                        contractor.getGameProfile().id(),
                        contractee.getGameProfile().id(),
                        option,
                        amount
                );

                UUID newContractId = UUID.randomUUID();
                ContractManager manager = ContractManager.getServerState(level.getServer());
                manager.addContract(newContractId, data);

            }

        }
        return super.use(level, player, interactionHand);
    }
}
