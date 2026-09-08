package com.communitytrials.model;

public class PunishmentConfig {

    private String message;
    private String command;

    public PunishmentConfig(String message, String command) {
        this.message = message;
        this.command = command;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
