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


public class BaseContract extends Item {
    public BaseContract(Properties properties) {
        super(properties);
    }

    public static ItemStack createContract(GameProfile contractor, GameProfile contractee) {
        ItemStack contract = new ItemStack(ModItem.BASE_CONTRACT);

        contract.set(ModComponents.CONTRACTOR, contractor.name());
        contract.set(ModComponents.CONTRACTEE, contractee.name());
        contract.set(ModComponents.CONTRACT_TYPE, Contract_Type.DeathExchange.ordinal());

        return contract;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (level.isClientSide()) return InteractionResult.PASS;
        ItemStack item = item = player.getItemInHand(interactionHand);
        String contractor = item.get(ModComponents.CONTRACTOR);
        String contractee = item.get(ModComponents.CONTRACTEE);
        int type = item.get(ModComponents.CONTRACT_TYPE);

        System.out.println("use Contract" + Contract_Type.fromInt(type) + ": " + contractor + "/" + contractee);
        return super.use(level, player, interactionHand);
    }
}
