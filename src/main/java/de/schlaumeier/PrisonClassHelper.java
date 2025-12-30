package de.schlaumeier;

import org.bukkit.entity.Player;

import de.schlaumeier.serialization.PlayerClassData;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;

public class PrisonClassHelper {
    private PrisonClassHelper() {}

    public static int getTotalXpForLevel(int level) {
        if (level <= 1) {
            return 0;
        }
        return (level - 1) * level * 50;
    }
    public static void setPlayerXp(Player player, double xp) {
        PlayerClassData data = PrisonLeveling.getInstance().getPlayerDataManager().get(player.getUniqueId());
        data.setXp(xp);
        while (data.getXp() >= getTotalXpForLevel(data.getLevel()+1) && data.getLevel() < data.getPrisonClass().getMaxLevel()) {
            data.setLevel(data.getLevel()+1);
            player.sendMessage("§b[PrisonLeveling] §aYou have reached level " + data.getLevel() + "!");
        }
        PrisonLeveling.getInstance().getPlayerDataManager().set(data);
    }
    public static void addXpToPlayer(Player player, double xp) {
        PlayerClassData data = PrisonLeveling.getInstance().getPlayerDataManager().get(player.getUniqueId());
        data.addXp(xp);
        while (data.getXp() >= getTotalXpForLevel(data.getLevel()+1) && data.getLevel() < data.getPrisonClass().getMaxLevel()) {
            data.setLevel(data.getLevel()+1);
        }
        PrisonLeveling.getInstance().getPlayerDataManager().set(data);
    }
    public static double getPlayerXp(Player player) {
        PlayerClassData data = PrisonLeveling.getInstance().getPlayerDataManager().get(player.getUniqueId());
        return data.getXp();
    }
    public static int getPlayerLevel(Player player) {
        PlayerClassData data = PrisonLeveling.getInstance().getPlayerDataManager().get(player.getUniqueId());
        return data.getLevel();
    }
    public static void addLevelsToPlayer(Player player, int levels) {
        PlayerClassData data = PrisonLeveling.getInstance().getPlayerDataManager().get(player.getUniqueId());
        data.setLevel(data.getLevel()+levels);
        PrisonLeveling.getInstance().getPlayerDataManager().set(data);
    }
    public static PrisonClass getClass(Player player) {
        PlayerClassData data = PrisonLeveling.getInstance().getPlayerDataManager().get(player.getUniqueId());
        return data.getPrisonClass();
    }
    public static void setClass(Player player, PrisonClass pc) {
        PlayerClassData data = PrisonLeveling.getInstance().getPlayerDataManager().get(player.getUniqueId());
        data.setPrisonClass(pc);
        PrisonLeveling.getInstance().getLuckPerms().getUserManager().loadUser(player.getUniqueId()).thenAcceptAsync((user) -> {
            user.data().clear((n) -> n.getType() == NodeType.INHERITANCE && PrisonLeveling.getInstance().getClassManager().getClasses().values().stream()
                    .anyMatch((cls) -> cls.getPermissionNode().equals(((InheritanceNode)n).getGroupName())));

            user.data().add(InheritanceNode.builder(pc.getPermissionNode()).build());
            PrisonLeveling.getInstance().getLuckPerms().getUserManager().saveUser(user);
        });
        PrisonLeveling.getInstance().getPlayerDataManager().set(data);
    }
}
