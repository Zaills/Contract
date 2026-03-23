package net.zaills.contract;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.zaills.contract.command.ContractCommand;
import net.zaills.contract.component.ModComponents;
import net.zaills.contract.item.ModItem;
import net.zaills.contract.packet.ModPacket;

public class Contract implements ModInitializer {
	public static final String MOD_ID = "contract";

	@Override
	public void onInitialize() {
		ModComponents.initialize();
		ModItem.initialize();
		ModPacket.initialize();

		CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
			ContractCommand.register(dispatcher);
		});

	}

}