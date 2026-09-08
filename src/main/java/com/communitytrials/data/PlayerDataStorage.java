package com.communitytrials.data;

import com.communitytrials.model.PlayerData;
import com.communitytrials.model.QuestProgress;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerDataStorage {

    private final File playersFolder;
    private final Logger logger;

    public PlayerDataStorage(File dataFolder, Logger logger) {
        this.playersFolder = new File(dataFolder, "players");
        this.logger = logger;
        if (!playersFolder.exists()) {
            playersFolder.mkdirs();
        }
    }

    public PlayerData load(UUID uuid) {
        PlayerData data = new PlayerData(uuid);
        File file = new File(playersFolder, uuid.toString() + ".yml");
        if (!file.exists()) {
            return data;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        data.setGoalContribution(yaml.getInt("contribution", 0));
        ConfigurationSection quests = yaml.getConfigurationSection("quests");
        if (quests != null) {
            for (String questId : quests.getKeys(false)) {
                ConfigurationSection q = quests.getConfigurationSection(questId);
                if (q == null) {
                    continue;
                }
                QuestProgress progress = new QuestProgress(
                        q.getInt("progress", 0),
                        q.getBoolean("completed", false),
                        q.getBoolean("claimed", false)
                );
                data.getQuestProgress().put(questId, progress);
            }
        }
        return data;
    }

    public void save(PlayerData data) {
        File file = new File(playersFolder, data.getUuid().toString() + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("contribution", data.getGoalContribution());
        for (var entry : data.getQuestProgress().entrySet()) {
            String base = "quests." + entry.getKey();
            QuestProgress progress = entry.getValue();
            yaml.set(base + ".progress", progress.getProgress());
            yaml.set(base + ".completed", progress.isCompleted());
            yaml.set(base + ".claimed", progress.isClaimed());
        }
        try {
            yaml.save(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save player data for " + data.getUuid(), e);
        }
    }
}
