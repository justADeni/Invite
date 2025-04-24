package com.github.justadeni.invite;

import com.github.justadeni.invite.autocomplete.TreeManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.concurrent.CompletableFuture;

public class InviteCommand {

    @SuppressWarnings("UnstableApiUsage")
    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName) {
        return Commands.literal(commandName)
            .then(Commands.argument("player", StringArgumentType.word())
                .suggests((ctx, builder) -> CompletableFuture.supplyAsync(() -> {
                    for (String suggestion : TreeManager.getCompletions(builder.getRemaining())){
                        builder.suggest(suggestion);
                    }
                    return builder.build();
                }))
                .executes(ctx -> {
                    String playerName = ctx.getArgument("player", String.class);
                    return Command.SINGLE_SUCCESS;
                }))
            .build();
    }

}
