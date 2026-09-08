package com.communitytrials.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class Menu implements InventoryHolder {

    protected final Player viewer;
    protected Inventory inventory;

    protected Menu(Player viewer) {
        this.viewer = viewer;
    }

    public abstract void build();

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open() {
        build();
        viewer.openInventory(inventory);
    }

    public abstract void onClick(InventoryClickEvent event);

    public void onClose(InventoryCloseEvent event) {
    }
}
