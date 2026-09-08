package com.communitytrials.manager;

import com.communitytrials.data.QuestStorage;
import com.communitytrials.model.PlayerData;
import com.communitytrials.model.Quest;
import com.communitytrials.model.QuestProgress;
import com.communitytrials.model.QuestType;
import com.communitytrials.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class QuestManager {

    private final QuestStorage storage;
    private final PlayerDataManager playerDataManager;
    private final Map<String, Quest> quests = new LinkedHashMap<>();

    public QuestManager(Plugin plugin, PlayerDataManager playerDataManager) {
        this.storage = new QuestStorage(plugin.getDataFolder(), plugin.getLogger());
        this.playerDataManager = playerDataManager;
    }

    public void load() {
        quests.clear();
        for (Quest quest : storage.loadAll()) {
            quests.put(quest.getId(), quest);
        }
    }

    public void save() {
        storage.saveAll(new ArrayList<>(quests.values()));
    }

    public Collection<Quest> getAll() {
        return quests.values();
    }

    public Collection<Quest> getActive() {
        List<Quest> active = new ArrayList<>();
        for (Quest quest : quests.values()) {
            if (quest.isActive()) {
                active.add(quest);
            }
        }
        return active;
    }

    public Optional<Quest> get(String id) {
        return Optional.ofNullable(quests.get(id));
    }

    public Quest create(String id, String name, String description, QuestType type, int target, ItemStack reward) {
        Quest quest = new Quest(id, name, description, type, target, reward, true);
        quests.put(id, quest);
        save();
        return quest;
    }

    public void delete(String id) {
        quests.remove(id);
        save();
    }

    public void toggleActive(String id) {
        Quest quest = quests.get(id);
        if (quest != null) {
            quest.setActive(!quest.isActive());
            save();
        }
    }

    public void addProgress(QuestType type, Player player, int amount) {
        if (amount <= 0) {
            return;
        }
        PlayerData data = playerDataManager.get(player.getUniqueId());
        boolean changed = false;
        for (Quest quest : quests.values()) {
            if (!quest.isActive() || quest.getType() != type) {
                continue;
            }
            QuestProgress progress = data.getOrCreateQuestProgress(quest.getId());
            if (progress.isCompleted()) {
                continue;
            }
            progress.setProgress(progress.getProgress() + amount);
            changed = true;
            if (progress.getProgress() >= quest.getTarget()) {
                progress.setProgress(quest.getTarget());
                progress.setCompleted(true);
                player.sendMessage(MessageUtil.prefixed(
                        "&aQuest complete: &f" + quest.getName() + " &7- visit /sidequests to claim your reward!"));
            }
        }
        if (changed) {
            playerDataManager.save(player.getUniqueId());
        }
    }

    public boolean grantProgress(String questId, UUID uuid, int amount) {
        Quest quest = quests.get(questId);
        if (quest == null) {
            return false;
        }
        PlayerData data = playerDataManager.get(uuid);
        QuestProgress progress = data.getOrCreateQuestProgress(questId);
        progress.setProgress(Math.max(0, progress.getProgress() + amount));
        if (progress.getProgress() >= quest.getTarget()) {
            progress.setProgress(quest.getTarget());
            progress.setCompleted(true);
        } else {
            progress.setCompleted(false);
        }
        playerDataManager.save(uuid);
        return true;
    }

    public ClaimResult claim(Player player, String questId) {
        Quest quest = quests.get(questId);
        if (quest == null) {
            return ClaimResult.NOT_FOUND;
        }
        PlayerData data = playerDataManager.get(player.getUniqueId());
        QuestProgress progress = data.getQuestProgress().get(questId);
        if (progress == null || !progress.isCompleted()) {
            return ClaimResult.NOT_COMPLETE;
        }
        if (progress.isClaimed()) {
            return ClaimResult.ALREADY_CLAIMED;
        }
        ItemStack reward = quest.getReward().clone();
        Map<Integer, ItemStack> overflow = player.getInventory().addItem(reward);
        for (ItemStack leftover : overflow.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
        progress.setClaimed(true);
        playerDataManager.save(player.getUniqueId());
        return ClaimResult.SUCCESS;
    }

    public QuestProgress getProgress(UUID uuid, String questId) {
        return playerDataManager.get(uuid).getOrCreateQuestProgress(questId);
    }

    public enum ClaimResult {
        SUCCESS,
        NOT_FOUND,
        NOT_COMPLETE,
        ALREADY_CLAIMED
    }
}
