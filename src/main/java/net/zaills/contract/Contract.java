package net.zaills.contract;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.zaills.contract.command.ContractCommand;
import net.zaills.contract.component.ModComponents;
import net.zaills.contract.item.ModItem;
import net.zaills.contract.packet.ModPacket;
import net.zaills.contract.record.ContractData;
import net.zaills.contract.record.ContractManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Contract implements ModInitializer {
	public static final String MOD_ID = "contract";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModComponents.initialize();
		ModItem.initialize();
		ModPacket.initialize();

		registerEvent();

	}

	private void registerEvent(){
		CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
			ContractCommand.register(dispatcher);
		});

		ServerLivingEntityEvents.ALLOW_DAMAGE.register(((entity, source, amount) -> {
			if (entity instanceof Player victim && source.getEntity() instanceof Player attacker) {
				MinecraftServer server = attacker.level().getServer();
				ContractManager manager = ContractManager.getServerState(server);

				List<Map.Entry<UUID, ContractData>> contracts = manager.getPlayerContractEntries(attacker.getUUID(), true);
				if (!contracts.isEmpty()) {
					for (Map.Entry<UUID, ContractData> entry : contracts.reversed()) {
						ContractData data = entry.getValue();
						String option = data.option();
						if (!option.equals("non_aggression")) continue;
						if (data.contractorId().equals(victim.getUUID())) return false;
					}
				}

				contracts = manager.getPlayerContractEntries(attacker.getUUID(), false);
				if (contracts.isEmpty()) return true;
				for (Map.Entry<UUID, ContractData> entry : contracts.reversed()) {
					UUID contractId = entry.getKey();
					ContractData data = entry.getValue();
					String option = data.option();
					if (!option.equals("non_aggression")) continue;
					if (data.contracteeId().equals(victim.getUUID())) {
						manager.removeContract(contractId);
					}
				}

			}
			return true;
		}));
	}

}