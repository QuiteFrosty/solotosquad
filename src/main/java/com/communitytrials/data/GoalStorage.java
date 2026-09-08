package com.communitytrials.data;

import com.communitytrials.model.CommunityGoal;
import com.communitytrials.model.GoalType;
import com.communitytrials.model.PunishmentConfig;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GoalStorage {

    private final File file;
    private final Logger logger;

    public GoalStorage(File dataFolder, Logger logger) {
        this.file = new File(dataFolder, "goal.yml");
        this.logger = logger;
    }

    public CommunityGoal loadGoal() {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        CommunityGoal goal = new CommunityGoal();
        goal.setName(yaml.getString("goal.name", goal.getName()));
        goal.setType(GoalType.fromString(yaml.getString("goal.type"), GoalType.MINE));
        goal.setTarget(yaml.getInt("goal.target", goal.getTarget()));
        goal.setCurrent(yaml.getInt("goal.current", 0));
        goal.setDeadline(yaml.getLong("goal.deadline", 0L));
        goal.setActive(yaml.getBoolean("goal.active", false));
        goal.setLastMilestoneAnnounced(yaml.getInt("goal.last-milestone", 0));
        return goal;
    }

    public PunishmentConfig loadPunishment(String defaultMessage, String defaultCommand) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        String message = yaml.getString("punishment.message", defaultMessage);
        String command = yaml.getString("punishment.command", defaultCommand);
        return new PunishmentConfig(message, command);
    }

    public void save(CommunityGoal goal, PunishmentConfig punishment) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        yaml.set("goal.name", goal.getName());
        yaml.set("goal.type", goal.getType().name());
        yaml.set("goal.target", goal.getTarget());
        yaml.set("goal.current", goal.getCurrent());
        yaml.set("goal.deadline", goal.getDeadline());
        yaml.set("goal.active", goal.isActive());
        yaml.set("goal.last-milestone", goal.getLastMilestoneAnnounced());
        yaml.set("punishment.message", punishment.getMessage());
        yaml.set("punishment.command", punishment.getCommand());
        try {
            yaml.save(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save goal.yml", e);
        }
    }
}
