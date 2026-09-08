package com.communitytrials.gui;

import com.communitytrials.CommunityTrialsPlugin;
import com.communitytrials.util.ItemBuilder;
import com.communitytrials.util.MenuUtil;
import com.communitytrials.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class AdminMasterMenu extends Menu {

    private final CommunityTrialsPlugin plugin;

    public AdminMasterMenu(CommunityTrialsPlugin plugin, Player viewer) {
        super(viewer);
        this.plugin = plugin;
    }

    @Override
    public void build() {
        inventory = Bukkit.createInventory(this, 27, MessageUtil.color("&8Trials Admin Panel"));

        inventory.setItem(10, new ItemBuilder(Material.TARGET)
                .name("&bGoal Manager")
                .lore("&7Set name, target, deadline", "&7Start / stop / reset the goal")
                .build());
        inventory.setItem(11, new ItemBuilder(Material.LAVA_BUCKET)
                .name("&bPunishment Setup")
                .lore("&7Set the failure message and command")
                .build());
        inventory.setItem(12, new ItemBuilder(Material.CHEST)
                .name("&bSide Quest Manager")
                .lore("&7Create, edit, delete and toggle quests")
                .build());
        inventory.setItem(14, new ItemBuilder(Material.WRITABLE_BOOK)
                .name("&bPlugin Submissions")
                .lore("&7Approve or deny plugin suggestions")
                .build());
        inventory.setItem(15, new ItemBuilder(Material.PAPER)
                .name("&bVote Manager")
                .lore("&7Start / end voting, view results")
                .build());
        inventory.setItem(16, new ItemBuilder(Material.GRASS_BLOCK)
                .name("&bWorld Border")
                .lore("&7Configure weekly growth and bonuses")
                .build());

        MenuUtil.fillEmpty(inventory);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        switch (event.getSlot()) {
            case 10 -> new AdminGoalMenu(plugin, viewer).open();
            case 11 -> new AdminPunishmentMenu(plugin, viewer).open();
            case 12 -> new AdminSideQuestMenu(plugin, viewer, 0).open();
            case 14 -> new AdminPluginSubmissionsMenu(plugin, viewer, 0).open();
            case 15 -> new AdminVoteManagerMenu(plugin, viewer).open();
            case 16 -> new AdminWorldBorderMenu(plugin, viewer).open();
            default -> {
            }
        }
    }
}
