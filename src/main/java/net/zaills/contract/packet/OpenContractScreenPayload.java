package net.zaills.contract.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.zaills.contract.Contract;

public record OpenContractScreenPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenContractScreenPayload> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Contract.MOD_ID, "open_contract_screen"));
    public static final StreamCodec<FriendlyByteBuf, OpenContractScreenPayload> CODEC =
            StreamCodec.unit(new OpenContractScreenPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
