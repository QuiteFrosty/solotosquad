package com.communitytrials.model;

import java.util.UUID;

public class PluginSuggestion {

    private final int id;
    private final UUID submitterUuid;
    private final String submitterName;
    private final String name;
    private SuggestionStatus status;

    public PluginSuggestion(int id, UUID submitterUuid, String submitterName, String name, SuggestionStatus status) {
        this.id = id;
        this.submitterUuid = submitterUuid;
        this.submitterName = submitterName;
        this.name = name;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public UUID getSubmitterUuid() {
        return submitterUuid;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public String getName() {
        return name;
    }

    public SuggestionStatus getStatus() {
        return status;
    }

    public void setStatus(SuggestionStatus status) {
        this.status = status;
    }
}
