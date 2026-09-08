package com.communitytrials.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack stack;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder(ItemStack base) {
        this.stack = base.clone();
        this.meta = stack.getItemMeta();
    }

    public ItemBuilder name(String name) {
        if (meta != null) {
            meta.setDisplayName(MessageUtil.color(name));
        }
        return this;
    }

    public ItemBuilder lore(List<String> lines) {
        if (meta != null) {
            List<String> colored = new ArrayList<>();
            for (String line : lines) {
                colored.add(MessageUtil.color(line));
            }
            meta.setLore(colored);
        }
        return this;
    }

    public ItemBuilder lore(String... lines) {
        return lore(List.of(lines));
    }

    public ItemBuilder amount(int amount) {
        stack.setAmount(Math.max(1, amount));
        return this;
    }

    public ItemStack build() {
        if (meta != null) {
            stack.setItemMeta(meta);
        }
        return stack;
    }
}
