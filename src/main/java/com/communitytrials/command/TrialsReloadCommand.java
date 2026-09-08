package com.communitytrials.command;

import com.communitytrials.CommunityTrialsPlugin;
import com.communitytrials.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TrialsReloadCommand implements CommandExecutor {

    private final CommunityTrialsPlugin plugin;

    public TrialsReloadCommand(CommunityTrialsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.reloadAll();
        MessageUtil.send(sender, "&aCommunityTrials configuration reloaded.");
        return true;
    }
}
