package com.communitytrials.command;

import com.communitytrials.CommunityTrialsPlugin;
import com.communitytrials.gui.SideQuestsMenu;
import com.communitytrials.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SideQuestsCommand implements CommandExecutor {

    private final CommunityTrialsPlugin plugin;

    public SideQuestsCommand(CommunityTrialsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtil.send(sender, "&cThis command can only be used in-game.");
            return true;
        }
        new SideQuestsMenu(plugin, player, 0).open();
        return true;
    }
}
