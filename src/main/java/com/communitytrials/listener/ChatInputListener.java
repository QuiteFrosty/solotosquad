package com.communitytrials.listener;

import com.communitytrials.util.ChatInputManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class ChatInputListener implements Listener {

    private final Plugin plugin;
    private final ChatInputManager chatInputManager;

    public ChatInputListener(Plugin plugin, ChatInputManager chatInputManager) {
        this.plugin = plugin;
        this.chatInputManager = chatInputManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!chatInputManager.isAwaiting(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
        String message = event.getMessage();
        Consumer<String> handler = chatInputManager.consume(player.getUniqueId());
        if (handler != null) {
            Bukkit.getScheduler().runTask(plugin, () -> handler.accept(message));
        }
    }
}
