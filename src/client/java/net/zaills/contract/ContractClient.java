package net.zaills.contract;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.zaills.contract.component.ModComponents;

public class ContractClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ItemTooltipCallback.EVENT.register(((stack, context, type, tooltip) -> {
			String contractor = stack.get(ModComponents.CONTRACTOR);
			String contractee = stack.get(ModComponents.CONTRACTEE);


			if (contractor != null && contractee != null) {
				tooltip.add(Component.translatable("item.contract.contract.contractor", contractor).withStyle(ChatFormatting.GOLD));
				tooltip.add(Component.translatable("item.contract.contract.contractee", contractee).withStyle(ChatFormatting.DARK_RED));
			}
		}));
	}
}