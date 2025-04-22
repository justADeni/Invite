package com.github.justadeni.invite;

import com.github.justadeni.invite.autocomplete.TrieManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class InviteCommand {

    @SuppressWarnings("UnstableApiUsage")
    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName) {
        return Commands.literal(commandName)
            .then(Commands.argument("player", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    for (String suggestion : TrieManager.getCompletions(builder.getRemaining())){
                        builder.suggest(suggestion);
                    }
                    return builder.buildFuture();
                })
                .executes(ctx -> {
                    String playerName = ctx.getArgument("player", String.class);
                    return 0;
                }))
            .build();


    }

}
