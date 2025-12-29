package de.schlaumeier.listeners;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import de.schlaumeier.PrisonLeveling;
import de.schlaumeier.commands.ActiveRespawnEditor;

public class RespawnItemEditListener implements Listener {
    private final PrisonLeveling plugin;

    public RespawnItemEditListener(PrisonLeveling plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        if (!plugin.activeEditor.containsKey(uuid))
            return;

        ActiveRespawnEditor edit = plugin.activeEditor.remove(uuid);

        ItemStack[] items = e.getInventory().getContents();

        edit.pc.addRespawnItems(items, edit.level);

        e.getPlayer().sendMessage("§aRespawn-Items gespeichert!");

        // Klassen speichern
        plugin.getClassManager().save();
    }
}