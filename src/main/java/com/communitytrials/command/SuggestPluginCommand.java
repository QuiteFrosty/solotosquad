package com.communitytrials.command;

import com.communitytrials.CommunityTrialsPlugin;
import com.communitytrials.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SuggestPluginCommand implements CommandExecutor {

    private final CommunityTrialsPlugin plugin;

    public SuggestPluginCommand(CommunityTrialsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtil.send(sender, "&cThis command can only be used in-game.");
            return true;
        }
        if (args.length > 0) {
            submit(player, String.join(" ", args));
            return true;
        }
        MessageUtil.send(player, "&eType the plugin name you'd like to suggest in chat (or 'cancel'):");
        plugin.getChatInputManager().await(player.getUniqueId(), input -> {
            if (!input.equalsIgnoreCase("cancel")) {
                submit(player, input);
            }
        });
        return true;
    }

    private void submit(Player player, String name) {
        plugin.getVoteManager().submit(player.getUniqueId(), player.getName(), name);
        MessageUtil.send(player, "&aThanks! Your suggestion &f\"" + name + "\" &ahas been submitted for admin review.");
    }
}
