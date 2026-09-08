package com.communitytrials.gui;

import com.communitytrials.CommunityTrialsPlugin;
import com.communitytrials.model.Quest;
import com.communitytrials.model.QuestType;
import com.communitytrials.util.ItemBuilder;
import com.communitytrials.util.MenuUtil;
import com.communitytrials.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class AdminEditQuestMenu extends Menu {

    private final CommunityTrialsPlugin plugin;
    private final String questId;
    private final int returnPage;

    public AdminEditQuestMenu(CommunityTrialsPlugin plugin, Player viewer, String questId, int returnPage) {
        super(viewer);
        this.plugin = plugin;
        this.questId = questId;
        this.returnPage = returnPage;
    }

    @Override
    public void build() {
        inventory = Bukkit.createInventory(this, 36, MessageUtil.color("&8Edit Quest"));
        Quest quest = plugin.getQuestManager().get(questId).orElse(null);
        if (quest == null) {
            inventory.setItem(13, new ItemBuilder(Material.BARRIER).name("&cThis quest no longer exists.").build());
            inventory.setItem(31, new ItemBuilder(Material.ARROW).name("&7Back").build());
            MenuUtil.fillEmpty(inventory);
            return;
        }

        inventory.setItem(10, new ItemBuilder(Material.NAME_TAG).name("&eSet Name")
                .lore("&7Current: &f" + quest.getName()).build());
        inventory.setItem(11, new ItemBuilder(Material.BOOK).name("&eSet Description")
                .lore("&7Current: &f" + quest.getDescription()).build());
        inventory.setItem(12, new ItemBuilder(Material.COMPASS).name("&eCycle Type")
                .lore("&7Current: &f" + quest.getType(), "&7Click to cycle").build());
        inventory.setItem(13, new ItemBuilder(Material.GOLD_INGOT).name("&eSet Target")
                .lore("&7Current: &f" + quest.getTarget()).build());
        inventory.setItem(14, new ItemBuilder(quest.getReward().getType()).name("&eSet Reward")
                .lore("&7Current: &f" + quest.getReward().getAmount() + "x " + quest.getReward().getType(),
                        "&7Click while holding an item to set it as the reward").build());
        inventory.setItem(15, new ItemBuilder(quest.isActive() ? Material.LIME_DYE : Material.GRAY_DYE)
                .name("&eToggle Active")
                .lore("&7Current: " + (quest.isActive() ? "&atrue" : "&cfalse")).build());

        inventory.setItem(22, new ItemBuilder(Material.TNT).name("&cDelete Quest")
                .lore("&7Shift-click to confirm deletion").build());

        inventory.setItem(31, new ItemBuilder(Material.ARROW).name("&7Back").build());
        MenuUtil.fillEmpty(inventory);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot == 31) {
            new AdminSideQuestMenu(plugin, viewer, returnPage).open();
            return;
        }
        Quest quest = plugin.getQuestManager().get(questId).orElse(null);
        if (quest == null) {
            return;
        }
        switch (slot) {
            case 10 -> promptText("name", input -> {
                quest.setName(input);
                plugin.getQuestManager().save();
            });
            case 11 -> promptText("description", input -> {
                quest.setDescription(input);
                plugin.getQuestManager().save();
            });
            case 12 -> {
                QuestType[] values = QuestType.values();
                QuestType next = values[(quest.getType().ordinal() + 1) % values.length];
                quest.setType(next);
                plugin.getQuestManager().save();
                build();
                viewer.updateInventory();
            }
            case 13 -> promptText("target amount", input -> {
                try {
                    quest.setTarget(Math.max(1, Integer.parseInt(input.trim())));
                    plugin.getQuestManager().save();
                } catch (NumberFormatException e) {
                    MessageUtil.send(viewer, "&cThat isn't a valid number.");
                }
            });
            case 14 -> {
                ItemStack hand = viewer.getInventory().getItemInMainHand();
                if (hand == null || hand.getType().isAir()) {
                    MessageUtil.send(viewer, "&cHold an item in your main hand first.");
                    return;
                }
                quest.setReward(hand.clone());
                plugin.getQuestManager().save();
                MessageUtil.send(viewer, "&aReward updated.");
                build();
                viewer.updateInventory();
            }
            case 15 -> {
                plugin.getQuestManager().toggleActive(questId);
                build();
                viewer.updateInventory();
            }
            case 22 -> {
                if (event.isShiftClick()) {
                    plugin.getQuestManager().delete(questId);
                    MessageUtil.send(viewer, "&cQuest deleted.");
                    new AdminSideQuestMenu(plugin, viewer, returnPage).open();
                } else {
                    MessageUtil.send(viewer, "&cShift-click to confirm deletion.");
                }
            }
            default -> {
            }
        }
    }

    private void promptText(String fieldName, java.util.function.Consumer<String> onInput) {
        viewer.closeInventory();
        MessageUtil.send(viewer, "&eType the new " + fieldName + " in chat (or 'cancel'):");
        plugin.getChatInputManager().await(viewer.getUniqueId(), input -> {
            if (!input.equalsIgnoreCase("cancel")) {
                onInput.accept(input);
            }
            new AdminEditQuestMenu(plugin, viewer, questId, returnPage).open();
        });
    }
}
