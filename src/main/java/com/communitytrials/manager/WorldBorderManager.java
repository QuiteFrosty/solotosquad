package com.communitytrials.manager;

import com.communitytrials.data.BorderStorage;
import com.communitytrials.model.CommunityGoal;
import com.communitytrials.model.WorldBorderConfig;
import com.communitytrials.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WorldBorderManager implements GoalManager.GoalListener {

    private static final long WEEKLY_PERIOD_MILLIS = TimeUnit.DAYS.toMillis(7);
    private static final long CHECK_PERIOD_TICKS = 20L * 60L * 5L;

    private final Plugin plugin;
    private final BorderStorage storage;
    private WorldBorderConfig config;
    private BukkitTask task;

    public WorldBorderManager(Plugin plugin) {
        this.plugin = plugin;
        this.storage = new BorderStorage(plugin.getDataFolder(), plugin.getLogger());
    }

    public void load() {
        List<String> worlds = plugin.getConfig().getStringList("border.worlds");
        if (worlds.isEmpty()) {
            worlds = new ArrayList<>();
            worlds.add("world");
        }
        config = new WorldBorderConfig(
                worlds,
                plugin.getConfig().getDouble("border.weekly-amount", 100),
                plugin.getConfig().getDouble("border.success-bonus", 500),
                plugin.getConfig().getBoolean("border.failure-shrink-enabled", false),
                plugin.getConfig().getDouble("border.failure-shrink-amount", 0),
                plugin.getConfig().getInt("border.transition-seconds", 20),
                0L
        );
        storage.applySavedState(config);
        if (config.getNextExpansion() <= 0) {
            config.setNextExpansion(System.currentTimeMillis() + WEEKLY_PERIOD_MILLIS);
        }
        save();
    }

    public void save() {
        storage.save(config);
    }

    public WorldBorderConfig getConfig() {
        return config;
    }

    public void startScheduler() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::checkWeeklyExpansion, 20L * 10L, CHECK_PERIOD_TICKS);
    }

    public void stopScheduler() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void checkWeeklyExpansion() {
        if (System.currentTimeMillis() >= config.getNextExpansion()) {
            expand(config.getWeeklyAmount(), false);
            config.setNextExpansion(System.currentTimeMillis() + WEEKLY_PERIOD_MILLIS);
            save();
        }
    }

    public void forceWeeklyExpansion() {
        expand(config.getWeeklyAmount(), false);
        config.setNextExpansion(System.currentTimeMillis() + WEEKLY_PERIOD_MILLIS);
        save();
    }

    private void expand(double amount, boolean celebratory) {
        if (amount == 0) {
            return;
        }
        long transitionSeconds = Math.max(1, config.getTransitionSeconds());
        for (String worldName : config.getWorlds()) {
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                continue;
            }
            WorldBorder border = world.getWorldBorder();
            double newSize = Math.max(1, border.getSize() + amount);
            border.setSize(newSize, transitionSeconds);
        }
        if (celebratory) {
            MessageUtil.broadcast("&6&lTHE WORLD GROWS! &r&fThe world border has expanded by &b" +
                    formatAmount(amount) + " blocks &fas a reward for the community's success!");
        } else {
            String verb = amount > 0 ? "grown" : "shrunk";
            MessageUtil.broadcast("&bThe world border has " + verb + " by " + formatAmount(Math.abs(amount)) + " blocks!");
        }
    }

    private String formatAmount(double amount) {
        if (amount == Math.floor(amount)) {
            return String.valueOf((long) amount);
        }
        return String.valueOf(amount);
    }

    public void setWeeklyAmount(double amount) {
        config.setWeeklyAmount(amount);
        save();
    }

    public void setSuccessBonus(double amount) {
        config.setSuccessBonus(amount);
        save();
    }

    public void setFailureShrinkEnabled(boolean enabled) {
        config.setFailureShrinkEnabled(enabled);
        save();
    }

    public void setFailureShrinkAmount(double amount) {
        config.setFailureShrinkAmount(amount);
        save();
    }

    public double getCurrentSize() {
        for (String worldName : config.getWorlds()) {
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                return world.getWorldBorder().getSize();
            }
        }
        return -1;
    }

    @Override
    public void onGoalSuccess(CommunityGoal goal) {
        expand(config.getSuccessBonus(), true);
    }

    @Override
    public void onGoalFailure(CommunityGoal goal) {
        if (config.isFailureShrinkEnabled() && config.getFailureShrinkAmount() > 0) {
            expand(-config.getFailureShrinkAmount(), false);
        }
    }
}
