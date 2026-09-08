package com.communitytrials.listener;

import com.communitytrials.manager.GoalManager;
import com.communitytrials.manager.QuestManager;
import com.communitytrials.model.GoalType;
import com.communitytrials.model.QuestType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;

public class ProgressListener implements Listener {

    private final GoalManager goalManager;
    private final QuestManager questManager;

    public ProgressListener(GoalManager goalManager, QuestManager questManager) {
        this.goalManager = goalManager;
        this.questManager = questManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        track(GoalType.MINE, QuestType.MINE, player, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        track(GoalType.KILL, QuestType.KILL, killer, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int amount = event.getRecipe().getResult().getAmount();
        track(GoalType.CRAFT, QuestType.CRAFT, player, Math.max(1, amount));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        track(GoalType.FISH, QuestType.FISH, event.getPlayer(), 1);
    }

    private void track(GoalType goalType, QuestType questType, Player player, int amount) {
        goalManager.addProgress(goalType, player.getUniqueId(), player.getName(), amount);
        questManager.addProgress(questType, player, amount);
    }
}
