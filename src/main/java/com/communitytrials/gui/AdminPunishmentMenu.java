package com.communitytrials.gui;

import com.communitytrials.CommunityTrialsPlugin;
import com.communitytrials.model.PunishmentConfig;
import com.communitytrials.util.ItemBuilder;
import com.communitytrials.util.MenuUtil;
import com.communitytrials.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class AdminPunishmentMenu extends Menu {

    private final CommunityTrialsPlugin plugin;

    public AdminPunishmentMenu(CommunityTrialsPlugin plugin, Player viewer) {
        super(viewer);
        this.plugin = plugin;
    }

    @Override
    public void build() {
        inventory = Bukkit.createInventory(this, 27, MessageUtil.color("&8Punishment Setup"));
        PunishmentConfig punishment = plugin.getGoalManager().getPunishment();

        inventory.setItem(11, new ItemBuilder(Material.LAVA_BUCKET).name("&eSet Punishment Message")
                .lore("&7Current: &f" + punishment.getMessage(), "&7Broadcast to everyone on failure", "&7Click to change in chat").build());
        inventory.setItem(15, new ItemBuilder(Material.COMMAND_BLOCK).name("&eSet Punishment Command")
                .lore("&7Current: &f" + (punishment.getCommand().isBlank() ? "(none)" : punishment.getCommand()),
                        "&7Run from console on failure", "&7Click to change in chat").build());

        inventory.setItem(22, new ItemBuilder(Material.ARROW).name("&7Back").build());
        MenuUtil.fillEmpty(inventory);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        switch (event.getSlot()) {
            case 11 -> promptMessage();
            case 15 -> promptCommand();
            case 22 -> new AdminMasterMenu(plugin, viewer).open();
            default -> {
            }
        }
    }

    private void promptMessage() {
        viewer.closeInventory();
        MessageUtil.send(viewer, "&eType the new punishment message in chat (or 'cancel'):");
        plugin.getChatInputManager().await(viewer.getUniqueId(), input -> {
            if (!input.equalsIgnoreCase("cancel")) {
                plugin.getGoalManager().setPunishmentMessage(input);
                MessageUtil.send(viewer, "&aPunishment message updated.");
            }
            new AdminPunishmentMenu(plugin, viewer).open();
        });
    }

    private void promptCommand() {
        viewer.closeInventory();
        MessageUtil.send(viewer, "&eType the new punishment console command in chat (no leading slash), or 'none' to clear, or 'cancel':");
        plugin.getChatInputManager().await(viewer.getUniqueId(), input -> {
            if (!input.equalsIgnoreCase("cancel")) {
                plugin.getGoalManager().setPunishmentCommand(input.equalsIgnoreCase("none") ? "" : input);
                MessageUtil.send(viewer, "&aPunishment command updated.");
            }
            new AdminPunishmentMenu(plugin, viewer).open();
        });
    }
}
