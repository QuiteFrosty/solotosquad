package com.communitytrials.gui;

import com.communitytrials.CommunityTrialsPlugin;
import com.communitytrials.model.CommunityGoal;
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

public class AdminGoalMenu extends Menu {

    private final CommunityTrialsPlugin plugin;

    public AdminGoalMenu(CommunityTrialsPlugin plugin, Player viewer) {
        super(viewer);
        this.plugin = plugin;
    }

    @Override
    public void build() {
        inventory = Bukkit.createInventory(this, 36, MessageUtil.color("&8Goal Manager"));
        CommunityGoal goal = plugin.getGoalManager().getGoal();

        List<String> infoLore = new ArrayList<>();
        infoLore.add("&7Name: &f" + goal.getName());
        infoLore.add("&7Type: &f" + goal.getType());
        infoLore.add("&7Progress: &f" + goal.getCurrent() + " / " + goal.getTarget());
        infoLore.add("&7Deadline: &f" + (goal.getDeadline() > 0 ? TimeUtil.formatDuration(goal.getDeadline() - System.currentTimeMillis()) : "not set"));
        infoLore.add("&7Active: &f" + goal.isActive());
        inventory.setItem(4, new ItemBuilder(Material.TARGET).name("&bCurrent Goal").lore(infoLore).build());

        inventory.setItem(10, new ItemBuilder(Material.NAME_TAG).name("&eSet Name")
                .lore("&7Current: &f" + goal.getName(), "&7Click to change in chat").build());
        inventory.setItem(12, new ItemBuilder(Material.GOLD_INGOT).name("&eSet Target")
                .lore("&7Current: &f" + goal.getTarget(), "&7Click to change in chat").build());
        inventory.setItem(14, new ItemBuilder(Material.CLOCK).name("&eSet Deadline (days from now)")
                .lore("&7Click to change in chat").build());

        inventory.setItem(19, new ItemBuilder(Material.LIME_DYE).name("&aStart Goal")
                .lore("&7Resets progress and starts the timer").build());
        inventory.setItem(21, new ItemBuilder(Material.YELLOW_DYE).name("&eStop Goal")
                .lore("&7Pauses the goal without punishment").build());
        inventory.setItem(23, new ItemBuilder(Material.RED_DYE).name("&cReset Goal")
                .lore("&7Resets progress to zero").build());

        inventory.setItem(31, new ItemBuilder(Material.ARROW).name("&7Back").build());
        MenuUtil.fillEmpty(inventory);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        switch (event.getSlot()) {
            case 10 -> promptName();
            case 12 -> promptTarget();
            case 14 -> promptDeadline();
            case 19 -> {
                plugin.getGoalManager().start();
                build();
                viewer.updateInventory();
            }
            case 21 -> {
                plugin.getGoalManager().stop();
                build();
                viewer.updateInventory();
            }
            case 23 -> {
                plugin.getGoalManager().reset();
                build();
                viewer.updateInventory();
            }
            case 31 -> new AdminMasterMenu(plugin, viewer).open();
            default -> {
            }
        }
    }

    private void promptName() {
        viewer.closeInventory();
        MessageUtil.send(viewer, "&eType the new goal name in chat (or 'cancel'):");
        plugin.getChatInputManager().await(viewer.getUniqueId(), input -> {
            if (!input.equalsIgnoreCase("cancel")) {
                plugin.getGoalManager().setName(input);
                MessageUtil.send(viewer, "&aGoal name updated.");
            }
            new AdminGoalMenu(plugin, viewer).open();
        });
    }

    private void promptTarget() {
        viewer.closeInventory();
        MessageUtil.send(viewer, "&eType the new goal target amount in chat (or 'cancel'):");
        plugin.getChatInputManager().await(viewer.getUniqueId(), input -> {
            if (!input.equalsIgnoreCase("cancel")) {
                try {
                    plugin.getGoalManager().setTarget(Integer.parseInt(input.trim()));
                    MessageUtil.send(viewer, "&aGoal target updated.");
                } catch (NumberFormatException e) {
                    MessageUtil.send(viewer, "&cThat isn't a valid number.");
                }
            }
            new AdminGoalMenu(plugin, viewer).open();
        });
    }

    private void promptDeadline() {
        viewer.closeInventory();
        MessageUtil.send(viewer, "&eType the number of days from now for the deadline (or 'cancel'):");
        plugin.getChatInputManager().await(viewer.getUniqueId(), input -> {
            if (!input.equalsIgnoreCase("cancel")) {
                try {
                    plugin.getGoalManager().setDeadlineDays(Integer.parseInt(input.trim()));
                    MessageUtil.send(viewer, "&aDeadline updated.");
                } catch (NumberFormatException e) {
                    MessageUtil.send(viewer, "&cThat isn't a valid number.");
                }
            }
            new AdminGoalMenu(plugin, viewer).open();
        });
    }
}
