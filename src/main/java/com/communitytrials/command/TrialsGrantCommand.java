package com.communitytrials.command;

import com.communitytrials.CommunityTrialsPlugin;
import com.communitytrials.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TrialsGrantCommand implements CommandExecutor {

    private final CommunityTrialsPlugin plugin;

    public TrialsGrantCommand(CommunityTrialsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 3) {
            MessageUtil.send(sender, "&cUsage: /trialsgrant <questId> <player> <amount>");
            return true;
        }
        String questId = args[0];
        if (plugin.getQuestManager().get(questId).isEmpty()) {
            MessageUtil.send(sender, "&cNo quest exists with ID '" + questId + "'.");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            MessageUtil.send(sender, "&cThat player has never joined this server.");
            return true;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            MessageUtil.send(sender, "&cAmount must be a number.");
            return true;
        }
        plugin.getQuestManager().grantProgress(questId, target.getUniqueId(), amount);
        MessageUtil.send(sender, "&aGranted " + amount + " progress on '" + questId + "' to " + target.getName() + ".");
        return true;
    }
}
