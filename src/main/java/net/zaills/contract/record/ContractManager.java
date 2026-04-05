package net.zaills.contract.record;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.*;

public class ContractManager extends SavedData {
    private static final Codec<Pair<UUID, ContractData>> CONTRACT_ENTRY = Codec.mapPair(
            UUIDUtil.CODEC.fieldOf("contract_id"),
            ContractData.CODEC.fieldOf("contract_data")
    ).codec();

    public static final Codec<ContractManager> CODEC = RecordCodecBuilder.create( instance -> instance.group(
            CONTRACT_ENTRY.listOf().optionalFieldOf("contracts", List.of()).forGetter(ContractManager::packContracts)
            ).apply(instance, ContractManager::fromPacked));

    public static final SavedDataType<ContractManager> TYPE = new SavedDataType<>(
            "active_contracts",
            ContractManager::new,
            CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    private final Map<UUID, ContractData> activeContracts;


    public ContractManager(Map<UUID, ContractData> activeContracts){
        this.activeContracts = new HashMap<>(activeContracts);
    }

    public ContractManager() {
        this(new HashMap<>());
    }

    private static ContractManager fromPacked(List<Pair<UUID, ContractData>> list) {
        Map<UUID, ContractData> map = new HashMap<>();
        for (Pair<UUID, ContractData> pair : list) {
            map.put(pair.getFirst(), pair.getSecond());
        }
        return new ContractManager(map);
    }

    private List<Pair<UUID, ContractData>> packContracts() {
        List<Pair<UUID, ContractData>> list = new ArrayList<>();
        for (Map.Entry<UUID, ContractData> entry : this.activeContracts.entrySet()) {
            list.add(new Pair<>(entry.getKey(), entry.getValue()));
        }
        return list;
    }


    public static ContractManager getServerState(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public Map<UUID, ContractData> getActiveContract() {
        return activeContracts;
    }

    public List<ContractData> getPlayerContract(UUID playerId) {
        List<ContractData> output = new ArrayList<>();
        for (Map.Entry<UUID, ContractData> entry : activeContracts.entrySet()) {
            if (entry.getValue().contractorId().equals(playerId) || entry.getValue().contracteeId().equals(playerId)) {
                output.add(entry.getValue());
            }
        }
        return output;
    }

    public List<Map.Entry<UUID, ContractData>> getPlayerContractEntries(UUID playerId) {
        List<Map.Entry<UUID, ContractData>> output = new ArrayList<>();
        for (Map.Entry<UUID, ContractData> entry : activeContracts.entrySet()) {
            if (entry.getValue().contracteeId().equals(playerId)) {
                output.add(entry);
            }
        }
        return output;
    }

    public void addContract(UUID contractID, ContractData data) {
        this.activeContracts.put(contractID, data);
        this.setDirty();
    }

    public boolean removeContract(UUID contractId) {
        if (this.activeContracts.remove(contractId) != null) {
            this.setDirty();
            return true;
        }
        return false;
    }

    public ContractData getContract(UUID contactId) {
        return this.activeContracts.get(contactId);
    }

}
