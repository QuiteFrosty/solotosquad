package com.communitytrials.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ChatInputManager {

    private final Map<UUID, Consumer<String>> pending = new ConcurrentHashMap<>();

    public void await(UUID uuid, Consumer<String> onInput) {
        pending.put(uuid, onInput);
    }

    public boolean isAwaiting(UUID uuid) {
        return pending.containsKey(uuid);
    }

    public void cancel(UUID uuid) {
        pending.remove(uuid);
    }

    public Consumer<String> consume(UUID uuid) {
        return pending.remove(uuid);
    }
}
