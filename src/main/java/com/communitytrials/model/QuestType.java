package com.communitytrials.model;

public enum QuestType {
    MINE,
    KILL,
    CRAFT,
    FISH,
    MANUAL;

    public static QuestType fromString(String value, QuestType fallback) {
        if (value == null) {
            return fallback;
        }
        try {
            return QuestType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
