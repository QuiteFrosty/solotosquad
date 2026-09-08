package com.communitytrials.gui;

import com.communitytrials.CommunityTrialsPlugin;
import com.communitytrials.manager.VoteManager;
import com.communitytrials.model.PluginSuggestion;
import com.communitytrials.util.ItemBuilder;
import com.communitytrials.util.MenuUtil;
import com.communitytrials.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginVoteMenu extends Menu {

    private final CommunityTrialsPlugin plugin;
    private final Map<Integer, Integer> slotToSuggestionId = new HashMap<>();

    public PluginVoteMenu(CommunityTrialsPlugin plugin, Player viewer) {
        super(viewer);
        this.plugin = plugin;
    }

    @Override
    public void build() {
        inventory = Bukkit.createInventory(this, 27, MessageUtil.color("&8Plugin Vote"));
        slotToSuggestionId.clear();

        VoteManager voteManager = plugin.getVoteManager();
        List<PluginSuggestion> approved = voteManager.getApproved();
        Integer myVote = voteManager.getState().getVotes().get(viewer.getUniqueId());

        if (!voteManager.isVotingOpen()) {
            inventory.setItem(13, new ItemBuilder(Material.BARRIER)
                    .name("&cVoting is not open right now")
                    .lore("&7Check back once an admin opens the vote.")
                    .build());
        } else if (approved.isEmpty()) {
            inventory.setItem(13, new ItemBuilder(Material.BARRIER)
                    .name("&cNo approved options yet")
                    .build());
        } else {
            int slot = 0;
            for (PluginSuggestion suggestion : approved) {
                if (slot >= 27) {
                    break;
                }
                boolean isMyVote = myVote != null && myVote == suggestion.getId();
                inventory.setItem(slot, new ItemBuilder(isMyVote ? Material.LIME_DYE : Material.PAPER)
                        .name((isMyVote ? "&a" : "&b") + suggestion.getName())
                        .lore("&7Suggested by &f" + suggestion.getSubmitterName(),
                                isMyVote ? "&aYour current vote" : "&7Click to vote for this")
                        .build());
                slotToSuggestionId.put(slot, suggestion.getId());
                slot++;
            }
        }

        MenuUtil.fillEmpty(inventory);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Integer suggestionId = slotToSuggestionId.get(event.getSlot());
        if (suggestionId == null) {
            return;
        }
        VoteManager.CastResult result = plugin.getVoteManager().castVote(viewer.getUniqueId(), suggestionId);
        switch (result) {
            case SUCCESS -> {
                MessageUtil.send(viewer, "&aVote cast!");
                build();
                viewer.updateInventory();
            }
            case CLOSED -> MessageUtil.send(viewer, "&cVoting is not open.");
            case INVALID_OPTION -> MessageUtil.send(viewer, "&cThat option is no longer available.");
        }
    }
}
