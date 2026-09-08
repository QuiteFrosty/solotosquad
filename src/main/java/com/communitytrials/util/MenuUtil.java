package com.communitytrials.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class MenuUtil {

    private static final ItemStack FILLER = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();

    private MenuUtil() {
    }

    public static void fillEmpty(Inventory inventory) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, FILLER);
            }
        }
    }

    public static void fillBorderRow(Inventory inventory, int rowStartSlot) {
        for (int i = 0; i < 9; i++) {
            inventory.setItem(rowStartSlot + i, FILLER);
        }
    }
}
