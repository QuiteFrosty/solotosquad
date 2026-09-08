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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminSideQuestMenu extends Menu {

    private static final int CREATE_SLOT = 46;

    private final CommunityTrialsPlugin plugin;
    private final int page;
    private final Map<Integer, String> slotToQuestId = new HashMap<>();

    public AdminSideQuestMenu(CommunityTrialsPlugin plugin, Player viewer, int page) {
        super(viewer);
        this.plugin = plugin;
        this.page = page;
    }

    @Override
    public void build() {
        inventory = Bukkit.createInventory(this, 54, MessageUtil.color("&8Side Quest Manager"));
        slotToQuestId.clear();

        List<Quest> quests = new ArrayList<>(plugin.getQuestManager().getAll());
        List<Quest> pageItems = Pagination.slice(quests, page);

        int slot = 0;
        for (Quest quest : pageItems) {
            inventory.setItem(slot, buildIcon(quest));
            slotToQuestId.put(slot, quest.getId());
            slot++;
        }

        int pageCount = Pagination.pageCount(quests.size());
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
        inventory.setItem(CREATE_SLOT, new ItemBuilder(Material.EMERALD).name("&aCreate New Quest")
                .lore("&7Click to create a quest in chat").build());
    }

    private ItemStack buildIcon(Quest quest) {
        List<String> lore = new ArrayList<>();
        lore.add("&7Type: &f" + quest.getType());
        lore.add("&7Target: &f" + quest.getTarget());
        lore.add("&7Active: " + (quest.isActive() ? "&atrue" : "&cfalse"));
        lore.add("");
        lore.add("&eLeft-click: &7edit");
        lore.add("&eRight-click: &7toggle active");
        lore.add("&eShift-right-click: &7delete");
        return new ItemBuilder(quest.isActive() ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE)
                .name((quest.isActive() ? "&a" : "&7") + quest.getName())
                .lore(lore)
                .build();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot == Pagination.BACK_SLOT) {
            new AdminMasterMenu(plugin, viewer).open();
            return;
        }
        if (slot == Pagination.PREV_SLOT && page > 0) {
            new AdminSideQuestMenu(plugin, viewer, page - 1).open();
            return;
        }
        if (slot == Pagination.NEXT_SLOT) {
            new AdminSideQuestMenu(plugin, viewer, page + 1).open();
            return;
        }
        if (slot == CREATE_SLOT) {
            promptCreate();
            return;
        }
        String questId = slotToQuestId.get(slot);
        if (questId == null) {
            return;
        }
        switch (event.getClick()) {
            case LEFT -> new AdminEditQuestMenu(plugin, viewer, questId, page).open();
            case RIGHT -> {
                plugin.getQuestManager().toggleActive(questId);
                build();
                viewer.updateInventory();
            }
            case SHIFT_RIGHT -> {
                plugin.getQuestManager().delete(questId);
                MessageUtil.send(viewer, "&cQuest deleted.");
                build();
                viewer.updateInventory();
            }
            default -> {
            }
        }
    }

    private void promptCreate() {
        viewer.closeInventory();
        MessageUtil.send(viewer, "&eType a unique ID for the new quest in chat (or 'cancel'):");
        plugin.getChatInputManager().await(viewer.getUniqueId(), idInput -> {
            if (idInput.equalsIgnoreCase("cancel")) {
                new AdminSideQuestMenu(plugin, viewer, page).open();
                return;
            }
            String id = idInput.trim().toLowerCase().replace(" ", "_");
            if (plugin.getQuestManager().get(id).isPresent()) {
                MessageUtil.send(viewer, "&cA quest with that ID already exists.");
                new AdminSideQuestMenu(plugin, viewer, page).open();
                return;
            }
            plugin.getQuestManager().create(id, id, "A new side quest.",
                    QuestType.MANUAL, 1,
                    new ItemStack(Material.PAPER));
            MessageUtil.send(viewer, "&aQuest created. Opening the editor...");
            new AdminEditQuestMenu(plugin, viewer, id, page).open();
        });
    }
}
