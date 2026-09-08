package com.communitytrials.gui;

import com.communitytrials.CommunityTrialsPlugin;
import com.communitytrials.model.CommunityGoal;
import com.communitytrials.model.PlayerData;
import com.communitytrials.model.VoteState;
import com.communitytrials.util.ItemBuilder;
import com.communitytrials.util.MenuUtil;
import com.communitytrials.util.MessageUtil;
import com.communitytrials.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class TrialsHubMenu extends Menu {

    private final CommunityTrialsPlugin plugin;

    public TrialsHubMenu(CommunityTrialsPlugin plugin, Player viewer) {
        super(viewer);
        this.plugin = plugin;
    }

    @Override
    public void build() {
        inventory = Bukkit.createInventory(this, 36, MessageUtil.color("&8Community Trials"));

        CommunityGoal goal = plugin.getGoalManager().getGoal();
        int percent = goal.getTarget() > 0 ? Math.min(100, (goal.getCurrent() * 100) / goal.getTarget()) : 0;
        List<String> goalLore = new ArrayList<>();
        goalLore.add("&7Type: &f" + goal.getType());
        goalLore.add("&7Progress: &f" + goal.getCurrent() + " / " + goal.getTarget() + " &7(" + percent + "%)");
        if (goal.isActive()) {
            goalLore.add("&7Deadline: &f" + TimeUtil.formatDuration(goal.getDeadline() - System.currentTimeMillis()));
            goalLore.add("&aActive");
        } else {
            goalLore.add("&cNot active");
        }
        inventory.setItem(11, new ItemBuilder(Material.TARGET)
                .name("&b" + goal.getName())
                .lore(goalLore)
                .build());

        PlayerData data = plugin.getPlayerDataManager().get(viewer.getUniqueId());
        inventory.setItem(13, new ItemBuilder(Material.PLAYER_HEAD)
                .name("&bYour Contribution")
                .lore("&7You have contributed &f" + data.getGoalContribution() + " &7to the community goal.")
                .build());

        VoteState voteState = plugin.getVoteManager().getState();
        String status;
        if (voteState.isOpen()) {
            status = "&aOpen - use /trials_vote";
        } else if (voteState.isUnlocked()) {
            status = "&eUnlocked - waiting for an admin to start voting";
        } else {
            status = "&cLocked - reach the community goal first";
        }
        List<String> voteLore = new ArrayList<>();
        voteLore.add("&7Status: " + status);
        if (voteState.getLastWinnerName() != null) {
            voteLore.add("&7Last winner: &f" + voteState.getLastWinnerName());
        }
        inventory.setItem(15, new ItemBuilder(Material.PAPER)
                .name("&bPlugin Vote")
                .lore(voteLore)
                .build());

        inventory.setItem(20, new ItemBuilder(Material.CHEST)
                .name("&bSide Quests")
                .lore("&7Click to open /sidequests")
                .build());
        inventory.setItem(22, new ItemBuilder(Material.WRITABLE_BOOK)
                .name("&bSuggest a Plugin")
                .lore("&7Click to suggest a plugin in chat")
                .build());
        inventory.setItem(24, new ItemBuilder(Material.BOOK)
                .name("&bHelp")
                .lore("&7Click for the help page")
                .build());

        MenuUtil.fillEmpty(inventory);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot == 20) {
            viewer.closeInventory();
            new SideQuestsMenu(plugin, viewer, 0).open();
        } else if (slot == 22) {
            viewer.closeInventory();
            viewer.performCommand("suggestplugin");
        } else if (slot == 24) {
            viewer.closeInventory();
            viewer.performCommand("trialshelp");
        }
    }
}
