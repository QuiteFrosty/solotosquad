package com.communitytrials.model;

public class CommunityGoal {

    private String name;
    private GoalType type;
    private int target;
    private int current;
    private long deadline;
    private boolean active;
    private int lastMilestoneAnnounced;

    public CommunityGoal() {
        this("Community Goal", GoalType.MINE, 50000, 0, 0L, false, 0);
    }

    public CommunityGoal(String name, GoalType type, int target, int current, long deadline, boolean active, int lastMilestoneAnnounced) {
        this.name = name;
        this.type = type;
        this.target = target;
        this.current = current;
        this.deadline = deadline;
        this.active = active;
        this.lastMilestoneAnnounced = lastMilestoneAnnounced;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GoalType getType() {
        return type;
    }

    public void setType(GoalType type) {
        this.type = type;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public void addCurrent(int amount) {
        this.current += amount;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getLastMilestoneAnnounced() {
        return lastMilestoneAnnounced;
    }

    public void setLastMilestoneAnnounced(int lastMilestoneAnnounced) {
        this.lastMilestoneAnnounced = lastMilestoneAnnounced;
    }

    public boolean isComplete() {
        return current >= target;
    }

    public boolean isExpired() {
        return deadline > 0 && System.currentTimeMillis() >= deadline;
    }
}
