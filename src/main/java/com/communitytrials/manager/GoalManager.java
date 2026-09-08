package com.communitytrials.manager;

import com.communitytrials.data.GoalStorage;
import com.communitytrials.model.CommunityGoal;
import com.communitytrials.model.GoalType;
import com.communitytrials.model.PunishmentConfig;
import com.communitytrials.util.MessageUtil;
import com.communitytrials.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GoalManager {

    public interface GoalListener {
        void onGoalSuccess(CommunityGoal goal);

        void onGoalFailure(CommunityGoal goal);
    }

    private final Plugin plugin;
    private final GoalStorage storage;
    private final PlayerDataManager playerDataManager;
    private final List<GoalListener> listeners = new ArrayList<>();

    private CommunityGoal goal;
    private PunishmentConfig punishment;
    private int milestoneInterval;
    private int milestonePercent;
    private long defaultDeadlineMillis;

    public GoalManager(Plugin plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
        this.storage = new GoalStorage(plugin.getDataFolder(), plugin.getLogger());
    }

    public void load() {
        this.milestoneInterval = plugin.getConfig().getInt("goal.milestone-interval", 1000);
        this.milestonePercent = plugin.getConfig().getInt("goal.milestone-percent", 10);
        int deadlineDays = plugin.getConfig().getInt("goal.default-deadline-days", 30);
        this.defaultDeadlineMillis = TimeUnit.DAYS.toMillis(deadlineDays);
        this.goal = storage.loadGoal();
        this.punishment = storage.loadPunishment(
                MessageUtil.color(plugin.getConfig().getString("punishment.message", "&cThe community failed its goal.")),
                plugin.getConfig().getString("punishment.command", "")
        );
    }

    public void addListener(GoalListener listener) {
        listeners.add(listener);
    }

    public void save() {
        storage.save(goal, punishment);
    }

    public CommunityGoal getGoal() {
        return goal;
    }

    public PunishmentConfig getPunishment() {
        return punishment;
    }

    public void setPunishmentMessage(String message) {
        punishment.setMessage(message);
        save();
    }

    public void setPunishmentCommand(String command) {
        punishment.setCommand(command);
        save();
    }

    public void setName(String name) {
        goal.setName(name);
        save();
    }

    public void setTarget(int target) {
        goal.setTarget(Math.max(1, target));
        save();
    }

    public void setDeadlineDays(int days) {
        goal.setDeadline(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days));
        save();
    }

    public void start() {
        goal.setCurrent(0);
        goal.setLastMilestoneAnnounced(0);
        if (goal.getDeadline() <= System.currentTimeMillis()) {
            goal.setDeadline(System.currentTimeMillis() + defaultDeadlineMillis);
        }
        goal.setActive(true);
        save();
        MessageUtil.broadcast("&aA new community goal has begun: &f" + goal.getName() +
                " &7(target: " + goal.getTarget() + ", deadline: " + TimeUtil.formatDuration(goal.getDeadline() - System.currentTimeMillis()) + ")");
    }

    public void stop() {
        goal.setActive(false);
        save();
    }

    public void reset() {
        goal.setActive(false);
        goal.setCurrent(0);
        goal.setLastMilestoneAnnounced(0);
        save();
    }

    public void addProgress(GoalType type, UUID contributor, String contributorName, int amount) {
        if (!goal.isActive() || goal.getType() != type || amount <= 0) {
            return;
        }
        goal.addCurrent(amount);
        playerDataManager.addContribution(contributor, amount);
        checkMilestone();
        if (goal.isComplete()) {
            complete(true);
            return;
        }
        save();
    }

    private void checkMilestone() {
        int announced = goal.getLastMilestoneAnnounced();
        int step = milestoneInterval > 0 ? milestoneInterval : Integer.MAX_VALUE;
        if (milestonePercent > 0) {
            int percentStep = Math.max(1, (goal.getTarget() * milestonePercent) / 100);
            step = Math.min(step, percentStep);
        }
        if (step <= 0 || step == Integer.MAX_VALUE) {
            return;
        }
        int reached = (goal.getCurrent() / step) * step;
        if (reached > announced && reached < goal.getTarget()) {
            goal.setLastMilestoneAnnounced(reached);
            MessageUtil.broadcast("&bMilestone! &f" + goal.getName() + " &7is now at &f" +
                    goal.getCurrent() + "&7/&f" + goal.getTarget());
        }
    }

    public void checkDeadline() {
        if (goal.isActive() && goal.isExpired() && !goal.isComplete()) {
            complete(false);
        }
    }

    private void complete(boolean success) {
        goal.setActive(false);
        save();
        if (success) {
            MessageUtil.broadcast("&a&lGOAL COMPLETE! &r&fThe community reached &b" + goal.getName() +
                    "&f! Plugin voting is now unlocked.");
            for (GoalListener listener : listeners) {
                listener.onGoalSuccess(goal);
            }
        } else {
            MessageUtil.broadcast(punishment.getMessage());
            String command = punishment.getCommand();
            if (command != null && !command.isBlank()) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            }
            for (GoalListener listener : listeners) {
                listener.onGoalFailure(goal);
            }
        }
    }
}
