package com.github.justadeni.invite.command;

import com.github.justadeni.invite.config.Config;
import com.github.justadeni.invite.db.Database;
import com.github.justadeni.invite.db.Invitor;
import com.github.justadeni.invite.invited.CheckName;
import com.github.justadeni.invite.suggestions.TreeManager;
import com.github.justadeni.invite.utils.Msg;
import com.github.justadeni.invite.utils.InvitorCheck;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.aniby.simplewhitelist.api.entity.WhitelistHandler;
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
                    CommandSender sender = ctx.getSource().getSender();
                    if (sender.hasPermission("invite.use") ) {
                        if (sender.hasPermission("invite.reload") && "reload".startsWith(builder.getRemaining()))
                            builder.suggest("reload");

                        if (builder.getRemaining().length() >= 3) {
                            TreeManager.getCompletions(builder.getRemaining()).forEach(builder::suggest);
                        }
                    }
                    return builder.build();
                }))
                .executes(ctx -> {
                    Thread.ofVirtual().start(() -> {
                        WhitelistHandler wh = WhitelistHandler.Api.instance;
                        CommandSender sender = ctx.getSource().getSender();
                        if (!sender.hasPermission("invite.use")) {
                            Msg.send(sender, Config.getInstance().NO_PERMISSION);
                            Config.getInstance().SOUND_FAILURE.play(sender);
                            return;
                        }
                        String invited = ctx.getArgument("player", String.class);
                        if (invited.equals("reload")) {
                            if (sender.hasPermission("invite.reload")) {
                                Config.reload();
                                Msg.send(sender, Config.getInstance().RELOADED);
                                Config.getInstance().SOUND_SUCCESS.play(sender);
                            } else {
                                Msg.send(sender, Config.getInstance().NO_PERMISSION);
                                Config.getInstance().SOUND_FAILURE.play(sender);
                            }
                            return;
                        }
                        if (ctx.getSource().getExecutor() instanceof Player player) {
                            Invitor invitor = Database.get().getKeys()
                                    .stream()
                                    .filter(p -> p.getUUID() == player.getUniqueId())
                                    .findFirst()
                                    .orElse(new Invitor(player.getUniqueId()));

                            if (!player.hasPermission("invite.bypass")) {
                                if (InvitorCheck.get(invitor) instanceof InvitorCheck.Timeout timeout) {
                                    Msg.send(sender, Config.getInstance().INVITE_TIMEOUT.replace("%time%", timeout.timeleftstring()));
                                    Config.getInstance().SOUND_FAILURE.play(sender);
                                    return;
                                }
                            }

                            if (!CheckName.exists(invited)) {
                                Msg.send(sender, Config.getInstance().INVITE_NONEXISTENT);
                                Config.getInstance().SOUND_FAILURE.play(sender);
                                return;
                            }
                            if (Arrays.stream(Bukkit.getOfflinePlayers()).anyMatch(p -> Objects.equals(p.getName(), invited))) {
                                Msg.send(sender, Config.getInstance().INVITE_PLAYING);
                                Config.getInstance().SOUND_FAILURE.play(sender);
                                return;
                            }
                            if (wh.isWhitelisted(invited)) {
                                Msg.send(sender, Config.getInstance().INVITE_INVITED);
                                Config.getInstance().SOUND_FAILURE.play(sender);
                                return;
                            }
                            invitor.setLastInvited();
                            invitor.decreaseInvitesLeft();
                            Database.get().put(invitor, invited);
                            wh.addWhitelist(invited);
                            Msg.send(sender, Config.getInstance().INVITE_SUCCESS.replace("%player%", invited));
                            Config.getInstance().SOUND_SUCCESS.play(sender);
                        } else {
                            Msg.send(sender, Config.getInstance().ONLY_PLAYER);
                        }
                    });
                    return Command.SINGLE_SUCCESS;
                }))
            .build();
    }

}
