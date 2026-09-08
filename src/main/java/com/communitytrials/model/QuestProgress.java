package com.communitytrials.model;

public class QuestProgress {

    private int progress;
    private boolean completed;
    private boolean claimed;

    public QuestProgress() {
        this(0, false, false);
    }

    public QuestProgress(int progress, boolean completed, boolean claimed) {
        this.progress = progress;
        this.completed = completed;
        this.claimed = claimed;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }
}
