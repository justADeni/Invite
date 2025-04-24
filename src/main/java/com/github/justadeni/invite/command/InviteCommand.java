package com.github.justadeni.invite.command;

import com.github.justadeni.invite.config.Config;
import com.github.justadeni.invite.db.Database;
import com.github.justadeni.invite.db.Invitor;
import com.github.justadeni.invite.invited.CheckName;
import com.github.justadeni.invite.invited.TreeManager;
import com.github.justadeni.invite.utils.Msg;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class InviteCommand {

    @SuppressWarnings("UnstableApiUsage")
    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName) {
        return Commands.literal(commandName)
            .then(Commands.argument("player", StringArgumentType.word())
                .suggests((ctx, builder) -> CompletableFuture.supplyAsync(() -> {
                    if (ctx.getSource().getExecutor() instanceof Player player) {
                        if (player.hasPermission("invite.use")) {
                            for (String suggestion : TreeManager.getCompletions(builder.getRemaining())) {
                                builder.suggest(suggestion);
                            }
                        }
                    }
                    if (ctx.getSource().getSender().hasPermission("invite.reload")) {
                        builder.suggest("reload");
                    }
                    return builder.build();
                }))
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    String invited = ctx.getArgument("player", String.class);
                    if (invited.equals("reload")) {
                        if (sender.hasPermission("invite.reload")) {
                            Config.reload();
                            Msg.send(sender, Config.getInstance().RELOADED);
                        } else {
                            Msg.send(sender, Config.getInstance().NO_PERMISSION);
                        }
                        return Command.SINGLE_SUCCESS;
                    }
                    if (ctx.getSource().getExecutor() instanceof Player player) {
                        Invitor invitor = Database.get().getKeys()
                                .stream()
                                .filter(p -> p.getUUID() == player.getUniqueId())
                                .findFirst()
                                .orElse(new Invitor(player.getUniqueId()));

                        if (!player.hasPermission("invite.bypass")) {
                            if (invitor.getInvitesLeft() <= 0) {
                                long timeleft = System.currentTimeMillis() - invitor.getLastInvited();
                                if (timeleft < Config.getInstance().TIMEOUT) {
                                    long totalSecs = timeleft / 1000;
                                    int hours = (int) totalSecs / 3600;
                                    int minutes = (int) (totalSecs % 3600) / 60;
                                    String timestring = hours + "h " + minutes + "m";
                                    Msg.send(sender, Config.getInstance().INVITE_TIMEOUT.replace("%time%", timestring));
                                    return Command.SINGLE_SUCCESS;
                                }
                            }
                        }

                        if (!CheckName.exists(invited)) {
                            Msg.send(sender, Config.getInstance().INVITE_NONEXISTENT);
                            return Command.SINGLE_SUCCESS;
                        }
                        if (Database.get().getValues().contains(invited)) {
                            Msg.send(sender, Config.getInstance().INVITE_INVITED);
                            return Command.SINGLE_SUCCESS;
                        }
                        if (Arrays.stream(Bukkit.getOfflinePlayers()).anyMatch(p -> Objects.equals(p.getName(), invited))) {
                            Msg.send(sender, Config.getInstance().INVITE_PLAYING);
                            return Command.SINGLE_SUCCESS;
                        }
                        invitor.setLastInvited();
                        invitor.decreaseInvitesLeft();
                        Database.get().put(invitor, invited);
                        Bukkit.getWhitelistedPlayers().add(Bukkit.getOfflinePlayer(invited));
                        Msg.send(sender, Config.getInstance().INVITE_SUCCESS.replace("%player%", invited));
                    } else {
                        Msg.send(sender, Config.getInstance().ONLY_PLAYER);
                    }
                    return Command.SINGLE_SUCCESS;
                }))
            .build();
    }

}
