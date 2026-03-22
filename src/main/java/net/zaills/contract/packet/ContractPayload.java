package net.zaills.contract.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.zaills.contract.Contract;

import java.util.UUID;

public record ContractPayload(UUID contractor, UUID contractee, String blockId, int amount) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ContractPayload> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Contract.MOD_ID, "contract_payload"));
    public static final StreamCodec<FriendlyByteBuf, ContractPayload> CODEC =
            CustomPacketPayload.codec(
                    ContractPayload::write,
                    ContractPayload::new
            );

    public ContractPayload(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readUUID(), buf.readUtf(), buf.readInt());
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeUUID(contractor);
        buf.writeUUID(contractee);
        buf.writeUtf(blockId);
        buf.writeInt(amount);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
