package com.communitytrials.gui;

import com.communitytrials.CommunityTrialsPlugin;
import com.communitytrials.manager.QuestManager;
import com.communitytrials.model.Quest;
import com.communitytrials.model.QuestProgress;
import com.communitytrials.util.ItemBuilder;
import com.communitytrials.util.MenuUtil;
import com.communitytrials.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SideQuestsMenu extends Menu {

    private final CommunityTrialsPlugin plugin;
    private final int page;
    private final Map<Integer, String> slotToQuestId = new LinkedHashMap<>();

    public SideQuestsMenu(CommunityTrialsPlugin plugin, Player viewer, int page) {
        super(viewer);
        this.plugin = plugin;
        this.page = page;
    }

    @Override
    public void build() {
        inventory = Bukkit.createInventory(this, 54, MessageUtil.color("&8Side Quests"));
        slotToQuestId.clear();

        List<Quest> active = new ArrayList<>(plugin.getQuestManager().getActive());
        List<Quest> pageItems = Pagination.slice(active, page);

        int slot = 0;
        for (Quest quest : pageItems) {
            QuestProgress progress = plugin.getQuestManager().getProgress(viewer.getUniqueId(), quest.getId());
            inventory.setItem(slot, buildQuestIcon(quest, progress));
            slotToQuestId.put(slot, quest.getId());
            slot++;
        }

        int pageCount = Pagination.pageCount(active.size());
        MenuUtil.fillBorderRow(inventory, 45);
        inventory.setItem(Pagination.BACK_SLOT, new ItemBuilder(Material.BARRIER).name("&cClose").build());
        if (page > 0) {
            inventory.setItem(Pagination.PREV_SLOT, new ItemBuilder(Material.ARROW).name("&ePrevious Page").build());
        }
        inventory.setItem(Pagination.PAGE_INFO_SLOT, new ItemBuilder(Material.PAPER)
                .name("&7Page " + (page + 1) + " / " + pageCount).build());
        if (page + 1 < pageCount) {
            inventory.setItem(Pagination.NEXT_SLOT, new ItemBuilder(Material.ARROW).name("&eNext Page").build());
        }
    }

    private ItemStack buildQuestIcon(Quest quest, QuestProgress progress) {
        Material material = switch (quest.getType()) {
            case MINE -> Material.IRON_PICKAXE;
            case KILL -> Material.IRON_SWORD;
            case CRAFT -> Material.CRAFTING_TABLE;
            case FISH -> Material.FISHING_ROD;
            case MANUAL -> Material.NAME_TAG;
        };
        List<String> lore = new ArrayList<>();
        lore.add("&7" + quest.getDescription());
        lore.add("&7Type: &f" + quest.getType());
        lore.add("&7Progress: &f" + progress.getProgress() + " / " + quest.getTarget());
        lore.add("&7Reward: &f" + quest.getReward().getAmount() + "x " + quest.getReward().getType());
        lore.add("");
        if (progress.isClaimed()) {
            lore.add("&aReward claimed");
        } else if (progress.isCompleted()) {
            lore.add("&e&lClick to claim your reward!");
        } else {
            lore.add("&7In progress...");
        }

        ItemStack icon = new ItemBuilder(material)
                .name((progress.isCompleted() && !progress.isClaimed() ? "&a" : "&b") + quest.getName())
                .lore(lore)
                .build();
        if (progress.isCompleted() && !progress.isClaimed()) {
            ItemMeta meta = icon.getItemMeta();
            if (meta != null) {
                meta.setEnchantmentGlintOverride(true);
                icon.setItemMeta(meta);
            }
        }
        return icon;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot == Pagination.BACK_SLOT) {
            viewer.closeInventory();
            return;
        }
        if (slot == Pagination.PREV_SLOT && page > 0) {
            new SideQuestsMenu(plugin, viewer, page - 1).open();
            return;
        }
        if (slot == Pagination.NEXT_SLOT) {
            new SideQuestsMenu(plugin, viewer, page + 1).open();
            return;
        }
        String questId = slotToQuestId.get(slot);
        if (questId == null) {
            return;
        }
        QuestManager.ClaimResult result = plugin.getQuestManager().claim(viewer, questId);
        switch (result) {
            case SUCCESS -> {
                MessageUtil.send(viewer, "&aReward claimed!");
                new SideQuestsMenu(plugin, viewer, page).open();
            }
            case NOT_COMPLETE -> MessageUtil.send(viewer, "&cYou haven't completed this quest yet.");
            case ALREADY_CLAIMED -> MessageUtil.send(viewer, "&cYou've already claimed this reward.");
            case NOT_FOUND -> MessageUtil.send(viewer, "&cThat quest no longer exists.");
        }
    }
}
