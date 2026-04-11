package net.zaills.contract.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.zaills.contract.item.ModItem;

import java.util.concurrent.CompletableFuture;

public class ContractItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ContractItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        valueLookupBuilder(ItemTags.DYEABLE).add(ModItem.BASE_CONTRACT);
    }
}
