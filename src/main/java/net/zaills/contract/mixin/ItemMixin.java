package net.zaills.contract.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.zaills.contract.Contract;
import net.zaills.contract.component.ModComponents;
import net.zaills.contract.item.ModItem;
import net.zaills.contract.record.ContractManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "onDestroyed", at = @At("HEAD"))
    private void destroyContract(ItemEntity itemEntity, CallbackInfo ci){
        if (itemEntity.level().isClientSide() || itemEntity.level().getServer() == null)
            return;
        ItemStack itemStack = itemEntity.getItem();
        if (itemStack.is(ModItem.BASE_CONTRACT) && itemStack.has(ModComponents.ID)) {
            UUID id = UUID.fromString(itemStack.get(ModComponents.ID));

            ContractManager manager = ContractManager.getServerState(itemEntity.level().getServer());
            manager.removeContract(id);
            Contract.LOGGER.info("A contract was destroyed in the world.");
            Contract.LOGGER.debug(String.valueOf(id));
        }

    }
}
