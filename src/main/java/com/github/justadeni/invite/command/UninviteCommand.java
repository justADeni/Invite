package com.github.justadeni.invite.command;

import com.github.justadeni.invite.config.Config;
import com.github.justadeni.invite.db.Database;
import com.github.justadeni.invite.invited.CheckName;
import com.github.justadeni.invite.utils.Msg;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.aniby.simplewhitelist.api.entity.WhitelistHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class UninviteCommand {

    @SuppressWarnings("UnstableApiUsage")
    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName) {
        return Commands.literal(commandName)
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests((ctx, builder) -> CompletableFuture.supplyAsync(() -> {
                            CommandSender sender = ctx.getSource().getSender();
                            if (sender.hasPermission("invite.uninvite.all")) {
                                Database.get().getValues().forEach(builder::suggest);
                            } else if (sender.hasPermission("invite.uninvite.own")) {
                                if (ctx.getSource().getExecutor() instanceof Player player) {
                                    Database.getByPlayer(player.getUniqueId()).forEach(builder::suggest);
                                }
                            }
                            return builder.build();
                        }))
                        .executes(ctx -> {
                            Thread.ofVirtual().start(() -> {
                                WhitelistHandler wh = WhitelistHandler.Api.instance;
                                CommandSender sender = ctx.getSource().getSender();
                                String invited = ctx.getArgument("player", String.class);
                                if (ctx.getSource().getExecutor() instanceof Player player) {
                                    Set<String> invitedByPlayer = Database.getByPlayer(player.getUniqueId());
                                    if (invitedByPlayer.contains(invited)) {
                                        if (!sender.hasPermission("invite.uninvite.own") && !sender.hasPermission("invite.uninvite.all")) {
                                            Msg.send(sender, Config.getInstance().NO_PERMISSION);
                                            Config.getInstance().SOUND_FAILURE.play(sender);
                                            return;
                                        }
                                    } else if (!sender.hasPermission("invite.uninvite.all")) {
                                        Msg.send(sender, Config.getInstance().NO_PERMISSION);
                                        Config.getInstance().SOUND_FAILURE.play(sender);
                                        return;
                                    }
                                }
                                if (!CheckName.exists(invited)) {
                                    Msg.send(sender, Config.getInstance().INVITE_NONEXISTENT);
                                    Config.getInstance().SOUND_FAILURE.play(sender);
                                    return;
                                }
                                if (Bukkit.getOfflinePlayer(invited).hasPlayedBefore()) {
                                    Msg.send(sender, Config.getInstance().INVITE_PLAYING);
                                    Config.getInstance().SOUND_FAILURE.play(sender);
                                    return;
                                }
                                if (!wh.isWhitelisted(invited)) {
                                    Msg.send(sender, Config.getInstance().UNINVITE_FAILURE);
                                    Config.getInstance().SOUND_FAILURE.play(sender);
                                    return;
                                }
                                wh.removeWhitelist(invited);
                                Msg.send(sender, Config.getInstance().UNINVITE_SUCCESS.replace("%player%", invited));
                                Config.getInstance().SOUND_SUCCESS.play(sender);
                            });
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }

}
