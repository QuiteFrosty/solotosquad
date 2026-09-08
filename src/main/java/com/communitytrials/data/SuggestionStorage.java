package com.communitytrials.data;

import com.communitytrials.model.PluginSuggestion;
import com.communitytrials.model.SuggestionStatus;
import com.communitytrials.model.VoteState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SuggestionStorage {

    private final File file;
    private final Logger logger;

    public SuggestionStorage(File dataFolder, Logger logger) {
        this.file = new File(dataFolder, "vote.yml");
        this.logger = logger;
    }

    public List<PluginSuggestion> loadSuggestions() {
        List<PluginSuggestion> list = new ArrayList<>();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yaml.getConfigurationSection("suggestions");
        if (section == null) {
            return list;
        }
        for (String key : section.getKeys(false)) {
            ConfigurationSection s = section.getConfigurationSection(key);
            if (s == null) {
                continue;
            }
            int id = Integer.parseInt(key);
            UUID submitter = UUID.fromString(s.getString("submitter-uuid"));
            String submitterName = s.getString("submitter-name", "Unknown");
            String name = s.getString("name", "Unknown");
            SuggestionStatus status = SuggestionStatus.valueOf(s.getString("status", "PENDING"));
            list.add(new PluginSuggestion(id, submitter, submitterName, name, status));
        }
        return list;
    }

    public VoteState loadVoteState() {
        VoteState state = new VoteState();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        state.setOpen(yaml.getBoolean("vote.open", false));
        state.setUnlocked(yaml.getBoolean("vote.unlocked", false));
        state.setLastWinnerName(yaml.getString("vote.last-winner", null));
        ConfigurationSection votes = yaml.getConfigurationSection("vote.votes");
        if (votes != null) {
            for (String uuidKey : votes.getKeys(false)) {
                state.getVotes().put(UUID.fromString(uuidKey), votes.getInt(uuidKey));
            }
        }
        return state;
    }

    public int loadNextId() {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        return yaml.getInt("next-id", 1);
    }

    public void save(List<PluginSuggestion> suggestions, VoteState state, int nextId) {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("next-id", nextId);
        for (PluginSuggestion suggestion : suggestions) {
            String base = "suggestions." + suggestion.getId();
            yaml.set(base + ".submitter-uuid", suggestion.getSubmitterUuid().toString());
            yaml.set(base + ".submitter-name", suggestion.getSubmitterName());
            yaml.set(base + ".name", suggestion.getName());
            yaml.set(base + ".status", suggestion.getStatus().name());
        }
        yaml.set("vote.open", state.isOpen());
        yaml.set("vote.unlocked", state.isUnlocked());
        if (state.getLastWinnerName() != null) {
            yaml.set("vote.last-winner", state.getLastWinnerName());
        }
        for (var entry : state.getVotes().entrySet()) {
            yaml.set("vote.votes." + entry.getKey(), entry.getValue());
        }
        try {
            yaml.save(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save vote.yml", e);
        }
    }
}
