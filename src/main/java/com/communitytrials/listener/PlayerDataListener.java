package com.communitytrials.listener;

import com.communitytrials.manager.PlayerDataManager;
import com.communitytrials.util.ChatInputManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDataListener implements Listener {

    private final PlayerDataManager playerDataManager;
    private final ChatInputManager chatInputManager;

    public PlayerDataListener(PlayerDataManager playerDataManager, ChatInputManager chatInputManager) {
        this.playerDataManager = playerDataManager;
        this.chatInputManager = chatInputManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playerDataManager.get(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        chatInputManager.cancel(event.getPlayer().getUniqueId());
        playerDataManager.unload(event.getPlayer().getUniqueId());
    }
}
