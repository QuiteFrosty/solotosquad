package com.communitytrials.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private int goalContribution;
    private final Map<String, QuestProgress> questProgress = new HashMap<>();

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getGoalContribution() {
        return goalContribution;
    }

    public void setGoalContribution(int goalContribution) {
        this.goalContribution = goalContribution;
    }

    public void addGoalContribution(int amount) {
        this.goalContribution += amount;
    }

    public Map<String, QuestProgress> getQuestProgress() {
        return questProgress;
    }

    public QuestProgress getOrCreateQuestProgress(String questId) {
        return questProgress.computeIfAbsent(questId, id -> new QuestProgress());
    }
}
