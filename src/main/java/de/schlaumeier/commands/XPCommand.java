package de.schlaumeier.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.schlaumeier.PrisonClassHelper;
import de.schlaumeier.serialization.PlayerClassData;
import de.schlaumeier.serialization.PlayerDataManager;

public class XPCommand implements CommandExecutor {

    private final PlayerDataManager data;

    public XPCommand(PlayerDataManager data) {
        this.data = data;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {

        // /xp
        if (args.length == 0) {
            if (!(s instanceof Player p)) {
                s.sendMessage("Only for players.");
                return true;
            }

            PlayerClassData d = data.get(p.getUniqueId());
            p.sendMessage("§aLevel: §e" + d.getLevel());
            p.sendMessage("§aXP: §e" + d.getXp());
            return true;
        }

        // /xp <player>, /xp get <player>
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
            s.sendMessage("§aLevel of " + t.getName() + ": §e" + d.getLevel());
            s.sendMessage("§aXP of " + t.getName() + ": §e" + d.getXp());
            return true;
        }

        // /xp set <player> <xp>
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

            double amount;
            try { amount = Double.parseDouble(args[2]); }
            catch (Exception ex) { s.sendMessage("§cInvalid number."); return true; }

            PrisonClassHelper.setPlayerXp(t, amount);
            s.sendMessage("§aXP changed.");
            return true;
        }

        // /xp add <player> <xp>
        if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            if (!s.hasPermission("prison.op")) {
                s.sendMessage("§cNo permission.");
                return true;
            }

            Player t = Bukkit.getPlayer(args[1]);
            if (t == null) {
                s.sendMessage("§cPlayer not found.");
                return true;
            }

            double amount;
            try { amount = Double.parseDouble(args[2]); }
            catch (Exception ex) { s.sendMessage("§cInvalid number."); return true; }

            PrisonClassHelper.addXpToPlayer(t, amount);
            s.sendMessage("§aXP changed.");
            t.sendMessage("§bYou have gained §a" + amount + " §bXP!");
            return true;
        }

        // /xp setlevel <player> <level>
        if (args.length == 3 && args[0].equalsIgnoreCase("setlevel")) {
            if (!s.hasPermission("prison.op")) {
                s.sendMessage("§cNo permission.");
                return true;
            }

            Player t = Bukkit.getPlayer(args[1]);
            if (t == null) {
                s.sendMessage("§cPlayer not found.");
                return true;
            }

            int lvl;
            try { lvl = Integer.parseInt(args[2]); }
            catch (Exception ex) { s.sendMessage("§cInvalid number."); return true; }

            PlayerClassData d = data.get(t.getUniqueId());
            d.setLevel(lvl);
            s.sendMessage("§aLevel changed.");
            return true;
        }

        s.sendMessage("§cInvalid command.");
        return true;
    }
}