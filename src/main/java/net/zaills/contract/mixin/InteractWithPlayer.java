package net.zaills.contract.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.zaills.contract.item.BaseContract;
import net.zaills.contract.item.ModItem;
import net.zaills.contract.item.Quill;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class InteractWithPlayer extends Avatar implements ContainerUser {

    protected InteractWithPlayer(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    private ItemStack lastItemInMainHand;

    @Shadow
    public abstract Inventory getInventory();

    @Shadow
    @Final
    private GameProfile gameProfile;

    @Inject(at = @At("HEAD"), method = "interactOn", cancellable = true)
    private void CreateContract(Entity entity, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!this.isSpectator() && entity instanceof Player contractee
            && this.lastItemInMainHand.getItem() instanceof Quill) {
            ItemStack newContract = BaseContract.createContract(this.gameProfile, contractee.getGameProfile());
            Level level = entity.level();

            if (this.getInventory().getFreeSlot() != -1) {
                this.getInventory().add(newContract);
            } else {
                ItemEntity itemEntity = new ItemEntity(level, this.getX(), this.getY() + 1, this.getZ(), newContract);
                level.addFreshEntity(itemEntity);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
            cir.cancel();
        }
    }
}
