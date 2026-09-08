package com.communitytrials.model;

import org.bukkit.inventory.ItemStack;

public class Quest {

    private final String id;
    private String name;
    private String description;
    private QuestType type;
    private int target;
    private ItemStack reward;
    private boolean active;

    public Quest(String id, String name, String description, QuestType type, int target, ItemStack reward, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.target = target;
        this.reward = reward;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QuestType getType() {
        return type;
    }

    public void setType(QuestType type) {
        this.type = type;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public ItemStack getReward() {
        return reward;
    }

    public void setReward(ItemStack reward) {
        this.reward = reward;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
