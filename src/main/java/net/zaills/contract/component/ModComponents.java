package net.zaills.contract.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.zaills.contract.Contract;

public class ModComponents {
    public static final DataComponentType<String> CONTRACTOR = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(Contract.MOD_ID, "contractor"),
            DataComponentType.<String>builder().persistent(Codec.STRING).build()
    );
    public static final DataComponentType<String> CONTRACTEE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(Contract.MOD_ID, "contractee"),
            DataComponentType.<String>builder().persistent(Codec.STRING).build()
    );
    public static final DataComponentType<Integer> CONTRACT_TYPE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(Contract.MOD_ID, "contract_type"),
            DataComponentType.<Integer>builder().persistent(Codec.INT).build()
    );
    public static final DataComponentType<Boolean> SIGNED = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(Contract.MOD_ID, "contract_signed"),
            DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build()
    );


    public static void initialize() {
    }
}
