package com.communitytrials.gui;

import com.communitytrials.CommunityTrialsPlugin;
import com.communitytrials.manager.VoteManager;
import com.communitytrials.model.PluginSuggestion;
import com.communitytrials.model.VoteState;
import com.communitytrials.util.ItemBuilder;
import com.communitytrials.util.MenuUtil;
import com.communitytrials.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdminVoteManagerMenu extends Menu {

    private final CommunityTrialsPlugin plugin;

    public AdminVoteManagerMenu(CommunityTrialsPlugin plugin, Player viewer) {
        super(viewer);
        this.plugin = plugin;
    }

    @Override
    public void build() {
        inventory = Bukkit.createInventory(this, 27, MessageUtil.color("&8Vote Manager"));
        VoteState state = plugin.getVoteManager().getState();

        List<String> statusLore = new ArrayList<>();
        statusLore.add("&7Unlocked: " + (state.isUnlocked() ? "&atrue" : "&cfalse"));
        statusLore.add("&7Open: " + (state.isOpen() ? "&atrue" : "&cfalse"));
        statusLore.add("&7Approved options: &f" + plugin.getVoteManager().getApproved().size());
        inventory.setItem(4, new ItemBuilder(Material.PAPER).name("&bVote Status").lore(statusLore).build());

        inventory.setItem(10, new ItemBuilder(Material.LIME_DYE).name("&aStart Vote")
                .lore("&7Requires the goal to have succeeded", "&7and at least one approved option").build());
        inventory.setItem(12, new ItemBuilder(Material.RED_DYE).name("&cEnd Vote")
                .lore("&7Tallies votes and announces the winner").build());
        inventory.setItem(14, new ItemBuilder(Material.BOOK).name("&eView Results")
                .lore(buildResultsLore()).build());
        inventory.setItem(16, new ItemBuilder(Material.BARRIER).name("&cClear Options")
                .lore("&7Removes all non-pending suggestions", "&7and resets votes").build());

        inventory.setItem(22, new ItemBuilder(Material.ARROW).name("&7Back").build());
        MenuUtil.fillEmpty(inventory);
    }

    private List<String> buildResultsLore() {
        List<String> lore = new ArrayList<>();
        Map<Integer, Long> tally = plugin.getVoteManager().tally();
        if (tally.isEmpty()) {
            lore.add("&7No votes cast yet.");
            return lore;
        }
        for (Map.Entry<Integer, Long> entry : tally.entrySet()) {
            Optional<PluginSuggestion> suggestion = plugin.getVoteManager().get(entry.getKey());
            lore.add("&7" + suggestion.map(PluginSuggestion::getName).orElse("Unknown") + ": &f" + entry.getValue());
        }
        return lore;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        VoteManager voteManager = plugin.getVoteManager();
        switch (event.getSlot()) {
            case 10 -> {
                if (voteManager.startVote()) {
                    MessageUtil.send(viewer, "&aVoting started.");
                } else {
                    MessageUtil.send(viewer, "&cCannot start voting: it must be unlocked and have at least one approved option.");
                }
                build();
                viewer.updateInventory();
            }
            case 12 -> {
                Optional<PluginSuggestion> winner = voteManager.endVote();
                if (winner.isPresent()) {
                    MessageUtil.send(viewer, "&aVoting ended. Winner: " + winner.get().getName());
                } else {
                    MessageUtil.send(viewer, "&cVoting wasn't open.");
                }
                build();
                viewer.updateInventory();
            }
            case 16 -> {
                voteManager.clearOptions();
                MessageUtil.send(viewer, "&aVote options cleared.");
                build();
                viewer.updateInventory();
            }
            case 22 -> new AdminMasterMenu(plugin, viewer).open();
            default -> {
            }
        }
    }
}
