package com.communitytrials.data;

import com.communitytrials.model.Quest;
import com.communitytrials.model.QuestType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuestStorage {

    private final File file;
    private final Logger logger;

    public QuestStorage(File dataFolder, Logger logger) {
        this.file = new File(dataFolder, "quests.yml");
        this.logger = logger;
    }

    public List<Quest> loadAll() {
        List<Quest> quests = new ArrayList<>();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yaml.getConfigurationSection("quests");
        if (section == null) {
            return quests;
        }
        for (String id : section.getKeys(false)) {
            ConfigurationSection q = section.getConfigurationSection(id);
            if (q == null) {
                continue;
            }
            String name = q.getString("name", id);
            String description = q.getString("description", "");
            QuestType type = QuestType.fromString(q.getString("type"), QuestType.MANUAL);
            int target = q.getInt("target", 1);
            boolean active = q.getBoolean("active", true);
            ItemStack reward = q.getItemStack("reward");
            if (reward == null) {
                reward = new ItemStack(Material.PAPER, 1);
            }
            quests.add(new Quest(id, name, description, type, target, reward, active));
        }
        return quests;
    }

    public void saveAll(List<Quest> quests) {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Quest quest : quests) {
            String base = "quests." + quest.getId();
            yaml.set(base + ".name", quest.getName());
            yaml.set(base + ".description", quest.getDescription());
            yaml.set(base + ".type", quest.getType().name());
            yaml.set(base + ".target", quest.getTarget());
            yaml.set(base + ".active", quest.isActive());
            yaml.set(base + ".reward", quest.getReward());
        }
        try {
            yaml.save(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save quests.yml", e);
        }
    }
}
