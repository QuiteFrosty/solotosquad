package com.communitytrials.model;

public enum GoalType {
    MINE,
    KILL,
    CRAFT,
    FISH;

    public static GoalType fromString(String value, GoalType fallback) {
        if (value == null) {
            return fallback;
        }
        try {
            return GoalType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
