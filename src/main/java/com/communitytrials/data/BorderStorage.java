package com.communitytrials.data;

import com.communitytrials.model.WorldBorderConfig;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BorderStorage {

    private final File file;
    private final Logger logger;

    public BorderStorage(File dataFolder, Logger logger) {
        this.file = new File(dataFolder, "border.yml");
        this.logger = logger;
    }

    public boolean hasSavedState() {
        return file.exists();
    }

    public void applySavedState(WorldBorderConfig config) {
        if (!file.exists()) {
            return;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        if (yaml.contains("weekly-amount")) {
            config.setWeeklyAmount(yaml.getDouble("weekly-amount"));
        }
        if (yaml.contains("success-bonus")) {
            config.setSuccessBonus(yaml.getDouble("success-bonus"));
        }
        if (yaml.contains("failure-shrink-enabled")) {
            config.setFailureShrinkEnabled(yaml.getBoolean("failure-shrink-enabled"));
        }
        if (yaml.contains("failure-shrink-amount")) {
            config.setFailureShrinkAmount(yaml.getDouble("failure-shrink-amount"));
        }
        if (yaml.contains("transition-seconds")) {
            config.setTransitionSeconds(yaml.getInt("transition-seconds"));
        }
        config.setNextExpansion(yaml.getLong("next-expansion", 0L));
    }

    public void save(WorldBorderConfig config) {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("weekly-amount", config.getWeeklyAmount());
        yaml.set("success-bonus", config.getSuccessBonus());
        yaml.set("failure-shrink-enabled", config.isFailureShrinkEnabled());
        yaml.set("failure-shrink-amount", config.getFailureShrinkAmount());
        yaml.set("transition-seconds", config.getTransitionSeconds());
        yaml.set("next-expansion", config.getNextExpansion());
        try {
            yaml.save(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save border.yml", e);
        }
    }
}
