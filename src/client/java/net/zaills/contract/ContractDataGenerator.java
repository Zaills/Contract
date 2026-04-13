package net.zaills.contract;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.zaills.contract.datagen.ContractItemTagProvider;
import net.zaills.contract.datagen.ContractModelProvider;
import net.zaills.contract.datagen.ContractRecipeProvider;

public class ContractDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ContractModelProvider::new);
		pack.addProvider(ContractItemTagProvider::new);
		pack.addProvider(ContractRecipeProvider::new);

	}
}
