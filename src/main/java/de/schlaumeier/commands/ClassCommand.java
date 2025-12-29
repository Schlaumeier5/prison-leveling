package de.schlaumeier.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.schlaumeier.PrisonClass;
import de.schlaumeier.serialization.ClassManager;
import de.schlaumeier.serialization.PlayerClassData;
import de.schlaumeier.serialization.PlayerDataManager;

public class ClassCommand implements CommandExecutor {

    private final ClassManager classManager;
    private final PlayerDataManager data;

    public ClassCommand(ClassManager cm, PlayerDataManager pdm) {
        this.classManager = cm;
        this.data = pdm;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {

        // /class
        if (args.length == 0) {
            if (!(s instanceof Player p)) {
                s.sendMessage("Only for Players.");
                return true;
            }

            PlayerClassData d = data.get(p.getUniqueId());
            p.sendMessage("§aYour class: §e" + d.getPrisonClass().getName());
            return true;
        }

        // /class <player>, /class get <player>
        if (args.length == 1 || (args.length == 2 && args[0].equalsIgnoreCase("get"))) {
            if (!s.hasPermission("prison.op")) {
                s.sendMessage("§cNo permission.");
                return true;
            }

            Player t = Bukkit.getPlayer(args.length == 1 ? args[0] : args[1]);
            if (t == null) {
                s.sendMessage("§cPlayer not found.");
                return true;
            }

            PlayerClassData d = data.get(t.getUniqueId());
            s.sendMessage("§aClass of " + t.getName() + ": §e" + d.getPrisonClass().getName());
            return true;
        }

        // /class set <spieler> <klasse>
        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {

            if (!s.hasPermission("prison.op")) {
                s.sendMessage("§cNo permission.");
                return true;
            }

            Player t = Bukkit.getPlayer(args[1]);
            if (t == null) {
                s.sendMessage("§cPlayer not found.");
                return true;
            }

            PrisonClass pc = classManager.getClass(args[2]);
            if (pc == null) {
                s.sendMessage("§cClass does not exist.");
                return true;
            }

            PlayerClassData d = data.get(t.getUniqueId());
            d.setPrisonClass(pc);

            s.sendMessage("§aClass changed.");
            return true;
        }

        s.sendMessage("§cInvalid command.");
        return true;
    }
}