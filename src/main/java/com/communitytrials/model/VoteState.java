package com.communitytrials.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoteState {

    private boolean open;
    private boolean unlocked;
    private final Map<UUID, Integer> votes = new HashMap<>();
    private String lastWinnerName;

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public Map<UUID, Integer> getVotes() {
        return votes;
    }

    public String getLastWinnerName() {
        return lastWinnerName;
    }

    public void setLastWinnerName(String lastWinnerName) {
        this.lastWinnerName = lastWinnerName;
    }
}
