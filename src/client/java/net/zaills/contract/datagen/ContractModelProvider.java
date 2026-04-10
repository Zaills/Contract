package net.zaills.contract.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.Identifier;
import net.zaills.contract.Contract;
import net.zaills.contract.item.ModItem;

public class ContractModelProvider extends FabricModelProvider {
    public ContractModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        ItemModel.Unbaked contract = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(ModItem.BASE_CONTRACT, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked contract_seal = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(ModItem.BASE_CONTRACT, "_seal", ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked seal = ItemModelUtils.tintedModel(Identifier.fromNamespaceAndPath(Contract.MOD_ID, "contract_seal"));

        itemModelGenerators.itemModelOutput.accept(
                ModItem.BASE_CONTRACT,
                ItemModelUtils.composite(contract, contract_seal)
        );
    }
}
