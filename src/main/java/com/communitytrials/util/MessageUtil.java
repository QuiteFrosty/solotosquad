package com.communitytrials.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class MessageUtil {

    private static String prefix = "&8[&bTrials&8] &r";

    private MessageUtil() {
    }

    public static void setPrefix(String rawPrefix) {
        prefix = rawPrefix == null ? "" : rawPrefix;
    }

    public static String color(String raw) {
        return raw == null ? "" : ChatColor.translateAlternateColorCodes('&', raw);
    }

    public static String prefixed(String raw) {
        return color(prefix) + color(raw);
    }

    public static void send(CommandSender target, String raw) {
        target.sendMessage(prefixed(raw));
    }

    public static void broadcast(String raw) {
        Bukkit.broadcastMessage(prefixed(raw));
    }
}
