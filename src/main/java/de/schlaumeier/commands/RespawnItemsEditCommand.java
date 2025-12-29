package de.schlaumeier.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import de.schlaumeier.PrisonClass;
import de.schlaumeier.PrisonLeveling;
import de.schlaumeier.serialization.ClassManager;

public class RespawnItemsEditCommand implements CommandExecutor {

    private final ClassManager classManager;
    private final PrisonLeveling plugin;

    public RespawnItemsEditCommand(PrisonLeveling plugin, ClassManager cm) {
        this.classManager = cm;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {

        if (!(s instanceof Player p)) {
            s.sendMessage("Only for players.");
            return true;
        }

        if (!p.hasPermission("prison.op")) {
            p.sendMessage("§cNo permission.");
            return true;
        }

        if (args.length != 2) {
            p.sendMessage("§cUsage: /respawnitems <class> <level>");
            return true;
        }

        PrisonClass pc = classManager.getClass(args[0]);
        if (pc == null) {
            p.sendMessage("§cClass does not exist.");
            return true;
        }

        int level;
        try { level = Integer.parseInt(args[1]); }
        catch (Exception e) { p.sendMessage("§cInvalid number."); return true; }

        // Open inventory
        Inventory inv = Bukkit.createInventory(
                p, 54,
                "Respawn Items: " + pc.getName() + " / Level " + level
        );

        // Load existing items
        if (pc.getRespawnItems().size() > level) {
            for (int i = 0; i < pc.getRespawnItems().get(level).length && i < inv.getSize(); i++) {
                inv.setItem(i, pc.getRespawnItems().get(level)[i]);
            }
        }

        // Save session data
        plugin.activeEditor.put(p.getUniqueId(), new ActiveRespawnEditor(pc, level));

        p.openInventory(inv);
        return true;
    }
}
