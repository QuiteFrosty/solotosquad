package com.communitytrials.gui;

import com.communitytrials.CommunityTrialsPlugin;
import com.communitytrials.model.PluginSuggestion;
import com.communitytrials.model.SuggestionStatus;
import com.communitytrials.util.ItemBuilder;
import com.communitytrials.util.MenuUtil;
import com.communitytrials.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminPluginSubmissionsMenu extends Menu {

    private final CommunityTrialsPlugin plugin;
    private final int page;
    private final Map<Integer, Integer> slotToSuggestionId = new HashMap<>();

    public AdminPluginSubmissionsMenu(CommunityTrialsPlugin plugin, Player viewer, int page) {
        super(viewer);
        this.plugin = plugin;
        this.page = page;
    }

    @Override
    public void build() {
        inventory = Bukkit.createInventory(this, 54, MessageUtil.color("&8Plugin Submissions"));
        slotToSuggestionId.clear();

        List<PluginSuggestion> all = new ArrayList<>(plugin.getVoteManager().getAll());
        List<PluginSuggestion> pageItems = Pagination.slice(all, page);

        int slot = 0;
        for (PluginSuggestion suggestion : pageItems) {
            inventory.setItem(slot, buildIcon(suggestion));
            slotToSuggestionId.put(slot, suggestion.getId());
            slot++;
        }

        int pageCount = Pagination.pageCount(all.size());
        MenuUtil.fillBorderRow(inventory, 45);
        inventory.setItem(Pagination.BACK_SLOT, new ItemBuilder(Material.ARROW).name("&7Back").build());
        if (page > 0) {
            inventory.setItem(Pagination.PREV_SLOT, new ItemBuilder(Material.ARROW).name("&ePrevious Page").build());
        }
        inventory.setItem(Pagination.PAGE_INFO_SLOT, new ItemBuilder(Material.PAPER)
                .name("&7Page " + (page + 1) + " / " + pageCount).build());
        if (page + 1 < pageCount) {
            inventory.setItem(Pagination.NEXT_SLOT, new ItemBuilder(Material.ARROW).name("&eNext Page").build());
        }
    }

    private ItemStack buildIcon(PluginSuggestion suggestion) {
        Material material = switch (suggestion.getStatus()) {
            case PENDING -> Material.PAPER;
            case APPROVED -> Material.LIME_DYE;
            case DENIED -> Material.RED_DYE;
        };
        List<String> lore = new ArrayList<>();
        lore.add("&7Suggested by &f" + suggestion.getSubmitterName());
        lore.add("&7Status: &f" + suggestion.getStatus());
        if (suggestion.getStatus() == SuggestionStatus.PENDING) {
            lore.add("");
            lore.add("&aLeft-click: &7approve");
            lore.add("&cRight-click: &7deny");
        }
        return new ItemBuilder(material).name("&b" + suggestion.getName()).lore(lore).build();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot == Pagination.BACK_SLOT) {
            new AdminMasterMenu(plugin, viewer).open();
            return;
        }
        if (slot == Pagination.PREV_SLOT && page > 0) {
            new AdminPluginSubmissionsMenu(plugin, viewer, page - 1).open();
            return;
        }
        if (slot == Pagination.NEXT_SLOT) {
            new AdminPluginSubmissionsMenu(plugin, viewer, page + 1).open();
            return;
        }
        Integer suggestionId = slotToSuggestionId.get(slot);
        if (suggestionId == null) {
            return;
        }
        plugin.getVoteManager().get(suggestionId).ifPresent(suggestion -> {
            if (suggestion.getStatus() != SuggestionStatus.PENDING) {
                return;
            }
            if (event.isLeftClick()) {
                plugin.getVoteManager().approve(suggestionId);
                MessageUtil.send(viewer, "&aApproved: " + suggestion.getName());
            } else if (event.isRightClick()) {
                plugin.getVoteManager().deny(suggestionId);
                MessageUtil.send(viewer, "&cDenied: " + suggestion.getName());
            }
            build();
            viewer.updateInventory();
        });
    }
}
