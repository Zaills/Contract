package net.zaills.contract.record;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record ContractData(UUID contractorId, UUID contracteeId, String option, int amount) {

    public static final Codec<ContractData> CODEC = RecordCodecBuilder.create( instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("contractor").forGetter(ContractData::contractorId),
            UUIDUtil.CODEC.fieldOf("contractee").forGetter(ContractData::contracteeId),
            Codec.STRING.fieldOf("option").forGetter(ContractData::option),
            Codec.INT.fieldOf("amount").forGetter(ContractData::amount)
        ).apply(instance, ContractData::new));

    }
