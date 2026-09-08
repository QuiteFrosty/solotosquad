package com.communitytrials.manager;

import com.communitytrials.data.SuggestionStorage;
import com.communitytrials.model.PluginSuggestion;
import com.communitytrials.model.SuggestionStatus;
import com.communitytrials.model.VoteState;
import com.communitytrials.util.MessageUtil;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class VoteManager implements GoalManager.GoalListener {

    private final SuggestionStorage storage;
    private final List<PluginSuggestion> suggestions = new ArrayList<>();
    private VoteState state;
    private int nextId;

    public VoteManager(Plugin plugin) {
        this.storage = new SuggestionStorage(plugin.getDataFolder(), plugin.getLogger());
    }

    public void load() {
        suggestions.clear();
        suggestions.addAll(storage.loadSuggestions());
        state = storage.loadVoteState();
        nextId = storage.loadNextId();
    }

    public void save() {
        storage.save(suggestions, state, nextId);
    }

    public VoteState getState() {
        return state;
    }

    public List<PluginSuggestion> getAll() {
        return suggestions;
    }

    public List<PluginSuggestion> getPending() {
        List<PluginSuggestion> pending = new ArrayList<>();
        for (PluginSuggestion suggestion : suggestions) {
            if (suggestion.getStatus() == SuggestionStatus.PENDING) {
                pending.add(suggestion);
            }
        }
        return pending;
    }

    public List<PluginSuggestion> getApproved() {
        List<PluginSuggestion> approved = new ArrayList<>();
        for (PluginSuggestion suggestion : suggestions) {
            if (suggestion.getStatus() == SuggestionStatus.APPROVED) {
                approved.add(suggestion);
            }
        }
        return approved;
    }

    public Optional<PluginSuggestion> get(int id) {
        for (PluginSuggestion suggestion : suggestions) {
            if (suggestion.getId() == id) {
                return Optional.of(suggestion);
            }
        }
        return Optional.empty();
    }

    public PluginSuggestion submit(UUID submitterUuid, String submitterName, String name) {
        PluginSuggestion suggestion = new PluginSuggestion(nextId++, submitterUuid, submitterName, name, SuggestionStatus.PENDING);
        suggestions.add(suggestion);
        save();
        return suggestion;
    }

    public void approve(int id) {
        get(id).ifPresent(s -> {
            s.setStatus(SuggestionStatus.APPROVED);
            save();
        });
    }

    public void deny(int id) {
        get(id).ifPresent(s -> {
            s.setStatus(SuggestionStatus.DENIED);
            save();
        });
    }

    public boolean isVotingUnlocked() {
        return state.isUnlocked();
    }

    public boolean isVotingOpen() {
        return state.isOpen();
    }

    public boolean startVote() {
        if (!state.isUnlocked() || getApproved().isEmpty()) {
            return false;
        }
        state.setOpen(true);
        state.getVotes().clear();
        save();
        MessageUtil.broadcast("&aPlugin voting is now open! Use &f/trials_vote &ato cast your vote.");
        return true;
    }

    public CastResult castVote(UUID voter, int suggestionId) {
        if (!state.isOpen()) {
            return CastResult.CLOSED;
        }
        if (get(suggestionId).filter(s -> s.getStatus() == SuggestionStatus.APPROVED).isEmpty()) {
            return CastResult.INVALID_OPTION;
        }
        state.getVotes().put(voter, suggestionId);
        save();
        return CastResult.SUCCESS;
    }

    public Optional<PluginSuggestion> endVote() {
        if (!state.isOpen()) {
            return Optional.empty();
        }
        state.setOpen(false);
        state.setUnlocked(false);
        Map<Integer, Long> tally = tally();
        Optional<PluginSuggestion> winner = tally.entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .flatMap(entry -> get(entry.getKey()));
        winner.ifPresent(w -> {
            state.setLastWinnerName(w.getName());
            MessageUtil.broadcast("&6&lVOTE RESULT: &r&fThe community has chosen &b" + w.getName() + "&f!");
        });
        save();
        return winner;
    }

    public Map<Integer, Long> tally() {
        Map<Integer, Long> tally = new HashMap<>();
        for (Integer suggestionId : state.getVotes().values()) {
            tally.merge(suggestionId, 1L, Long::sum);
        }
        return tally;
    }

    public void clearOptions() {
        suggestions.removeIf(s -> s.getStatus() != SuggestionStatus.PENDING);
        state.getVotes().clear();
        state.setOpen(false);
        save();
    }

    @Override
    public void onGoalSuccess(com.communitytrials.model.CommunityGoal goal) {
        state.setUnlocked(true);
        save();
    }

    @Override
    public void onGoalFailure(com.communitytrials.model.CommunityGoal goal) {
    }

    public enum CastResult {
        SUCCESS,
        CLOSED,
        INVALID_OPTION
    }
}
