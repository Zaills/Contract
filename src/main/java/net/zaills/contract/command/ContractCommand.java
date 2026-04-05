package net.zaills.contract.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.zaills.contract.record.ContractData;
import net.zaills.contract.record.ContractManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ContractCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("contracts")
                        .executes(ContractCommand::yourContract)
                .then(Commands.literal("list")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))
                        .executes(ContractCommand::listContract)
                )
                .then(Commands.literal("purge")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))
                        .executes(ContractCommand::purgeContract)
                )
                .then(Commands.literal("remove")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))
                        .then(Commands.argument("id", StringArgumentType.string())
                            .executes(ContractCommand::removeContract)
                        )
                )
        );
    }

    private static int yourContract(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        ContractManager manager = ContractManager.getServerState(server);
        Map<UUID, ContractData> contracts = manager.getActiveContract();

        UUID playerId = context.getSource().getPlayer().getUUID();

        List<ContractData> yourData = manager.getPlayerContract(playerId);

        if (contracts.isEmpty() || yourData.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("§cThere are no active contracts."), false);
            return 1;
        }

        context.getSource().sendSuccess(() -> Component.literal("§6--- Active Contracts (" + yourData.size() + ") ---"), false);

        for (ContractData data : yourData) {
            String contractor = getPlayerName(server, data.contractorId());
            String contractee = getPlayerName(server, data.contracteeId());

            MutableComponent infoLine = Component.literal("§f" + contractor + " §7§l-> §f" + contractee)
                    .append(Component.literal(" §7| §d" + data.amount() + " §7x §a" + data.option()));


            context.getSource().sendSuccess(() -> infoLine, false);

        }


        return 1;
    }

    private static int listContract(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        ContractManager manager = ContractManager.getServerState(server);
        Map<UUID, ContractData> contracts = manager.getActiveContract();

        if (contracts.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("§cThere are no active contracts."), false);
            return 1;
        }

        context.getSource().sendSuccess(() -> Component.literal("§6--- Active Contracts (" + contracts.size() + ") ---"), false);

        for (Map.Entry<UUID, ContractData> entry : contracts.entrySet()) {
            UUID id = entry.getKey();
            ContractData data = entry.getValue();

            String contractor = getPlayerName(server, data.contractorId());
            String contractee = getPlayerName(server, data.contracteeId());

            MutableComponent idComponent = Component.literal("§e[" + id.toString().substring(0, 8) + "] ")
                    .withStyle(style -> style
                            .withClickEvent(new ClickEvent.CopyToClipboard(id.toString()))
                            .withHoverEvent(new HoverEvent.ShowText(Component.literal("§7Full ID: " + id + "\n§eClick to copy to clipboard")))
                    );

            MutableComponent infoLine = Component.literal("§f" + contractor + " §7§l-> §f" + contractee)
                            .append(Component.literal(" §7| §d" + data.amount() + " §7x §a" + data.option()));


            context.getSource().sendSuccess(() -> idComponent.append(infoLine), false);
        }


        return 1;
    }

    private static String getPlayerName(MinecraftServer server, UUID uuid) {
        ServerPlayer player = server.getPlayerList().getPlayer(uuid);
        if (player != null) return player.getScoreboardName();
        else return uuid.toString();
    }

    private static int purgeContract(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        ContractManager manager = ContractManager.getServerState(server);
        Map<UUID, ContractData> contracts = manager.getActiveContract();

        for (Map.Entry<UUID, ContractData> entry : contracts.entrySet()) {
            manager.removeContract(entry.getKey());
        }
        return 1;
    }

    private static int removeContract(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        ContractManager manager = ContractManager.getServerState(server);
        String id = StringArgumentType.getString(context, "id");

        if (manager.removeContract(UUID.fromString(id)))
            context.getSource().sendSuccess(() -> Component.literal("Contract succefull removed"), false);
        else
            context.getSource().sendSuccess(() -> Component.literal("No contract found"), false);
        return 1;
    }
}
