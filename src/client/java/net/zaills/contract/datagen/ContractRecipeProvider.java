package net.zaills.contract.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.zaills.contract.item.ModItem;

import java.util.concurrent.CompletableFuture;

public class ContractRecipeProvider extends FabricRecipeProvider {
    public ContractRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        return new RecipeProvider(provider, recipeOutput) {
            @Override
            public void buildRecipes() {
                HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);

                shapeless(
                    RecipeCategory.MISC,
                    ModItem.QUILL
                )
                .requires(Items.FEATHER).requires(Items.INK_SAC).unlockedBy(getHasName(Items.INK_SAC), has(Items.INK_SAC))
                .save(output);

                shapeless(
                        RecipeCategory.MISC,
                        ModItem.QUILL
                )
                .requires(ModItem.QUILL).requires(Items.INK_SAC).unlockedBy(getHasName(ModItem.QUILL), has(ModItem.QUILL))
                .save(output);
            }
        };
    }

    @Override
    public String getName() {
        return "ContractLandsRecipeProvider";
    }
}
