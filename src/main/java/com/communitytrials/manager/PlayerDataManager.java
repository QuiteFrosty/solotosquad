package com.communitytrials.manager;

import com.communitytrials.data.PlayerDataStorage;
import com.communitytrials.model.PlayerData;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    private final PlayerDataStorage storage;
    private final Map<UUID, PlayerData> cache = new ConcurrentHashMap<>();

    public PlayerDataManager(Plugin plugin) {
        this.storage = new PlayerDataStorage(plugin.getDataFolder(), plugin.getLogger());
    }

    public PlayerData get(UUID uuid) {
        return cache.computeIfAbsent(uuid, storage::load);
    }

    public void unload(UUID uuid) {
        PlayerData data = cache.remove(uuid);
        if (data != null) {
            storage.save(data);
        }
    }

    public void addContribution(UUID uuid, int amount) {
        PlayerData data = get(uuid);
        data.addGoalContribution(amount);
        storage.save(data);
    }

    public void save(UUID uuid) {
        PlayerData data = cache.get(uuid);
        if (data != null) {
            storage.save(data);
        }
    }

    public void saveAll() {
        for (PlayerData data : cache.values()) {
            storage.save(data);
        }
    }
}
