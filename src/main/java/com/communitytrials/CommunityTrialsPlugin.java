package com.communitytrials;

import com.communitytrials.command.SideQuestsCommand;
import com.communitytrials.command.SuggestPluginCommand;
import com.communitytrials.command.TrialsAdminCommand;
import com.communitytrials.command.TrialsCommand;
import com.communitytrials.command.TrialsGrantCommand;
import com.communitytrials.command.TrialsHelpCommand;
import com.communitytrials.command.TrialsReloadCommand;
import com.communitytrials.command.TrialsVoteCommand;
import com.communitytrials.gui.MenuListener;
import com.communitytrials.listener.ChatInputListener;
import com.communitytrials.listener.PlayerDataListener;
import com.communitytrials.listener.ProgressListener;
import com.communitytrials.manager.GoalManager;
import com.communitytrials.manager.PlayerDataManager;
import com.communitytrials.manager.QuestManager;
import com.communitytrials.manager.VoteManager;
import com.communitytrials.manager.WorldBorderManager;
import com.communitytrials.util.ChatInputManager;
import com.communitytrials.util.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class CommunityTrialsPlugin extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private GoalManager goalManager;
    private QuestManager questManager;
    private VoteManager voteManager;
    private WorldBorderManager worldBorderManager;
    private ChatInputManager chatInputManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        chatInputManager = new ChatInputManager();
        playerDataManager = new PlayerDataManager(this);
        goalManager = new GoalManager(this, playerDataManager);
        questManager = new QuestManager(this, playerDataManager);
        voteManager = new VoteManager(this);
        worldBorderManager = new WorldBorderManager(this);

        reloadAll();

        goalManager.addListener(voteManager);
        goalManager.addListener(worldBorderManager);

        getServer().getPluginManager().registerEvents(new ProgressListener(goalManager, questManager), this);
        getServer().getPluginManager().registerEvents(new PlayerDataListener(playerDataManager, chatInputManager), this);
        getServer().getPluginManager().registerEvents(new ChatInputListener(this, chatInputManager), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        getCommand("trials").setExecutor(new TrialsCommand(this));
        getCommand("sidequests").setExecutor(new SideQuestsCommand(this));
        getCommand("suggestplugin").setExecutor(new SuggestPluginCommand(this));
        getCommand("trialshelp").setExecutor(new TrialsHelpCommand());
        getCommand("trials_vote").setExecutor(new TrialsVoteCommand(this));
        getCommand("trialsadmin").setExecutor(new TrialsAdminCommand(this));
        getCommand("trialsgrant").setExecutor(new TrialsGrantCommand(this));
        getCommand("trialsreload").setExecutor(new TrialsReloadCommand(this));

        worldBorderManager.startScheduler();
        getServer().getScheduler().runTaskTimer(this, () -> goalManager.checkDeadline(), 20L * 30L, 20L * 60L * 5L);

        getLogger().info("CommunityTrials enabled.");
    }

    @Override
    public void onDisable() {
        if (worldBorderManager != null) {
            worldBorderManager.stopScheduler();
        }
        if (goalManager != null) {
            goalManager.save();
        }
        if (questManager != null) {
            questManager.save();
        }
        if (voteManager != null) {
            voteManager.save();
        }
        if (worldBorderManager != null) {
            worldBorderManager.save();
        }
        if (playerDataManager != null) {
            playerDataManager.saveAll();
        }
        getLogger().info("CommunityTrials disabled.");
    }

    public void reloadAll() {
        reloadConfig();
        MessageUtil.setPrefix(getConfig().getString("messages.prefix", "&8[&bTrials&8] &r"));
        goalManager.load();
        questManager.load();
        voteManager.load();
        worldBorderManager.load();
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public GoalManager getGoalManager() {
        return goalManager;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public VoteManager getVoteManager() {
        return voteManager;
    }

    public WorldBorderManager getWorldBorderManager() {
        return worldBorderManager;
    }

    public ChatInputManager getChatInputManager() {
        return chatInputManager;
    }
}
