package com.communitytrials.model;

import java.util.List;

public class WorldBorderConfig {

    private List<String> worlds;
    private double weeklyAmount;
    private double successBonus;
    private boolean failureShrinkEnabled;
    private double failureShrinkAmount;
    private int transitionSeconds;
    private long nextExpansion;

    public WorldBorderConfig(List<String> worlds, double weeklyAmount, double successBonus,
                              boolean failureShrinkEnabled, double failureShrinkAmount,
                              int transitionSeconds, long nextExpansion) {
        this.worlds = worlds;
        this.weeklyAmount = weeklyAmount;
        this.successBonus = successBonus;
        this.failureShrinkEnabled = failureShrinkEnabled;
        this.failureShrinkAmount = failureShrinkAmount;
        this.transitionSeconds = transitionSeconds;
        this.nextExpansion = nextExpansion;
    }

    public List<String> getWorlds() {
        return worlds;
    }

    public void setWorlds(List<String> worlds) {
        this.worlds = worlds;
    }

    public double getWeeklyAmount() {
        return weeklyAmount;
    }

    public void setWeeklyAmount(double weeklyAmount) {
        this.weeklyAmount = weeklyAmount;
    }

    public double getSuccessBonus() {
        return successBonus;
    }

    public void setSuccessBonus(double successBonus) {
        this.successBonus = successBonus;
    }

    public boolean isFailureShrinkEnabled() {
        return failureShrinkEnabled;
    }

    public void setFailureShrinkEnabled(boolean failureShrinkEnabled) {
        this.failureShrinkEnabled = failureShrinkEnabled;
    }

    public double getFailureShrinkAmount() {
        return failureShrinkAmount;
    }

    public void setFailureShrinkAmount(double failureShrinkAmount) {
        this.failureShrinkAmount = failureShrinkAmount;
    }

    public int getTransitionSeconds() {
        return transitionSeconds;
    }

    public void setTransitionSeconds(int transitionSeconds) {
        this.transitionSeconds = transitionSeconds;
    }

    public long getNextExpansion() {
        return nextExpansion;
    }

    public void setNextExpansion(long nextExpansion) {
        this.nextExpansion = nextExpansion;
    }
}
