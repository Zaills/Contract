package net.zaills.contract;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.zaills.contract.component.Contract_Type;
import net.zaills.contract.component.ModComponents;
import net.zaills.contract.packet.OpenContractScreenPayload;
import net.zaills.contract.screen.ContractScreen;

import java.util.UUID;

public class ContractClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ItemTooltipCallback.EVENT.register((stack, context, type, tooltip) -> {

			String contractorId = stack.get(ModComponents.CONTRACTOR);
			String contracteeId = stack.get(ModComponents.CONTRACTEE);

			Minecraft minecraft = Minecraft.getInstance();

			if (contractorId != null && contracteeId != null && minecraft.getConnection() != null) {
				String contractor = "offline [" + contractorId + "]";
				try {
					contractor = minecraft.getConnection().getPlayerInfo(UUID.fromString(contractorId)).getProfile().name();
				} catch (Exception ignored) {}
                tooltip.add(Component.translatable("item.contract.contract.contractor", contractor).withStyle(ChatFormatting.GOLD));

				String contractee = "offline [" + contracteeId + "]";
				try {
					contractee = minecraft.getConnection().getPlayerInfo(UUID.fromString(contracteeId)).getProfile().name();
				} catch (Exception ignored) {}
				tooltip.add(Component.translatable("item.contract.contract.contractee", contractee).withStyle(ChatFormatting.DARK_RED));
			}

			try {
				if (stack.get(ModComponents.CONTRACT_TYPE) == Contract_Type.Blocks.ordinal()) {
					String blockId = stack.get(ModComponents.OPTION);
					int amount = stack.get(ModComponents.AMOUNT);
					tooltip.add(Component.translatable("item.contract.contract.block", blockId, amount));
				}
			} catch (Exception ignored) {}

			if (stack.has(ModComponents.SIGNED)) {
				boolean signedStatus = Boolean.TRUE.equals(stack.get(ModComponents.SIGNED));
				if (signedStatus)
					tooltip.add(Component.translatable("item.contract.contract.waiting.contractor"));
				else
					tooltip.add(Component.translatable("item.contract.contract.waiting.contractee"));
			}
        });


		ClientPlayNetworking.registerGlobalReceiver(OpenContractScreenPayload.ID, (payload, context) -> {
			context.client().execute(() -> {
				Minecraft.getInstance().setScreen(new ContractScreen(Component.literal("Contract Screen")));
			});
		});
	}
}