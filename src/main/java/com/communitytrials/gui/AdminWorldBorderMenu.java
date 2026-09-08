package com.communitytrials.gui;

import com.communitytrials.CommunityTrialsPlugin;
import com.communitytrials.manager.WorldBorderManager;
import com.communitytrials.model.WorldBorderConfig;
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

public class AdminWorldBorderMenu extends Menu {

    private final CommunityTrialsPlugin plugin;

    public AdminWorldBorderMenu(CommunityTrialsPlugin plugin, Player viewer) {
        super(viewer);
        this.plugin = plugin;
    }

    @Override
    public void build() {
        inventory = Bukkit.createInventory(this, 27, MessageUtil.color("&8World Border"));
        WorldBorderManager manager = plugin.getWorldBorderManager();
        WorldBorderConfig config = manager.getConfig();

        List<String> infoLore = new ArrayList<>();
        double size = manager.getCurrentSize();
        infoLore.add("&7Current size: &f" + (size >= 0 ? (long) size : "unknown"));
        infoLore.add("&7Next weekly expansion in: &f" + TimeUtil.formatDuration(config.getNextExpansion() - System.currentTimeMillis()));
        infoLore.add("&7Worlds: &f" + String.join(", ", config.getWorlds()));
        inventory.setItem(4, new ItemBuilder(Material.GRASS_BLOCK).name("&bWorld Border Status").lore(infoLore).build());

        inventory.setItem(10, new ItemBuilder(Material.LIME_CONCRETE).name("&eSet Weekly Amount")
                .lore("&7Current: &f" + config.getWeeklyAmount(), "&7Blocks added every 7 days").build());
        inventory.setItem(11, new ItemBuilder(Material.GOLD_BLOCK).name("&eSet Success Bonus")
                .lore("&7Current: &f" + config.getSuccessBonus(), "&7Added when the community goal succeeds").build());
        inventory.setItem(12, new ItemBuilder(config.isFailureShrinkEnabled() ? Material.LIME_DYE : Material.GRAY_DYE)
                .name("&eToggle Failure Shrink")
                .lore("&7Current: " + (config.isFailureShrinkEnabled() ? "&aenabled" : "&cdisabled")).build());
        inventory.setItem(13, new ItemBuilder(Material.RED_CONCRETE).name("&eSet Failure Shrink Amount")
                .lore("&7Current: &f" + config.getFailureShrinkAmount()).build());
        inventory.setItem(15, new ItemBuilder(Material.NETHER_STAR).name("&aForce Weekly Expansion Now")
                .lore("&7Applies the weekly amount immediately", "&7and resets the 7-day timer").build());

        inventory.setItem(22, new ItemBuilder(Material.ARROW).name("&7Back").build());
        MenuUtil.fillEmpty(inventory);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        WorldBorderManager manager = plugin.getWorldBorderManager();
        switch (event.getSlot()) {
            case 10 -> promptDouble("weekly amount", manager::setWeeklyAmount);
            case 11 -> promptDouble("success bonus", manager::setSuccessBonus);
            case 12 -> {
                manager.setFailureShrinkEnabled(!manager.getConfig().isFailureShrinkEnabled());
                build();
                viewer.updateInventory();
            }
            case 13 -> promptDouble("failure shrink amount", manager::setFailureShrinkAmount);
            case 15 -> {
                manager.forceWeeklyExpansion();
                MessageUtil.send(viewer, "&aWeekly expansion applied.");
                build();
                viewer.updateInventory();
            }
            case 22 -> new AdminMasterMenu(plugin, viewer).open();
            default -> {
            }
        }
    }

    private void promptDouble(String fieldName, java.util.function.DoubleConsumer setter) {
        viewer.closeInventory();
        MessageUtil.send(viewer, "&eType the new " + fieldName + " in chat (or 'cancel'):");
        plugin.getChatInputManager().await(viewer.getUniqueId(), input -> {
            if (!input.equalsIgnoreCase("cancel")) {
                try {
                    setter.accept(Double.parseDouble(input.trim()));
                    MessageUtil.send(viewer, "&aUpdated.");
                } catch (NumberFormatException e) {
                    MessageUtil.send(viewer, "&cThat isn't a valid number.");
                }
            }
            new AdminWorldBorderMenu(plugin, viewer).open();
        });
    }
}
