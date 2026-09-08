package com.communitytrials.command;

import com.communitytrials.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TrialsHelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(MessageUtil.color("&8&m----------&r &bCommunity Trials &8&m----------"));
        sender.sendMessage(MessageUtil.color("&f/trials &7- open the Community Trials hub"));
        sender.sendMessage(MessageUtil.color("&f/sidequests &7- browse and claim side quests"));
        sender.sendMessage(MessageUtil.color("&f/suggestplugin <name> &7- suggest a plugin for the next vote"));
        sender.sendMessage(MessageUtil.color("&f/trials_vote &7- vote for the next plugin (once voting opens)"));
        sender.sendMessage(MessageUtil.color("&f/trialshelp &7- show this page"));
        if (sender.hasPermission("communitytrials.admin")) {
            sender.sendMessage(MessageUtil.color("&8&m----------&r &cAdmin&r &8&m----------"));
            sender.sendMessage(MessageUtil.color("&f/trialsadmin &7- open the admin panel"));
            sender.sendMessage(MessageUtil.color("&f/trialsgrant <questId> <player> <amount> &7- grant quest progress"));
            sender.sendMessage(MessageUtil.color("&f/trialsreload &7- reload the configuration"));
        }
        sender.sendMessage(MessageUtil.color("&8&m--------------------------------------" + ChatColor.RESET));
        return true;
    }
}
