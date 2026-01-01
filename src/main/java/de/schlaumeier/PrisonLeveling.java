package de.schlaumeier;

import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.schlaumeier.commands.ActiveRespawnEditor;
import de.schlaumeier.commands.ClassCommand;
import de.schlaumeier.commands.RespawnItemsEditCommand;
import de.schlaumeier.commands.XPCommand;
import de.schlaumeier.listeners.RespawnItemEditListener;
import de.schlaumeier.serialization.ClassManager;
import de.schlaumeier.serialization.PlayerClassData;
import de.schlaumeier.serialization.PlayerDataManager;
import de.schlaumeier.serialization.PrisonClassData;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;

public class PrisonLeveling extends JavaPlugin implements Listener {
    private static PrisonLeveling instance;
    public static PrisonLeveling getInstance() {
        return instance;
    }
    private final ClassManager classManager = new ClassManager(getDataFolder());
    private final PlayerDataManager playerDataManager = new PlayerDataManager(getDataFolder(), classManager);
    public Map<UUID, ActiveRespawnEditor> activeEditor = new HashMap<>();
    private LuckPerms luckPerms;

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    public ClassManager getClassManager() {
        return classManager;
    }
    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
    
    @Override
    public void onLoad() {
        instance = this;
    }
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        ConfigurationSerialization.registerClass(PrisonClassData.class, "PrisonClassData");
        classManager.load();
        playerDataManager.load();
        getCommand("xp").setExecutor(new XPCommand(playerDataManager));
        getCommand("class").setExecutor(new ClassCommand(classManager, playerDataManager));
        getCommand("respawnitems").setExecutor(new RespawnItemsEditCommand(this, classManager));

        getServer().getPluginManager().registerEvents(new RespawnItemEditListener(this), this);

        luckPerms = LuckPermsProvider.get();
    }

    @Override
    public void onDisable() {
        classManager.save();
        playerDataManager.save();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (!playerDataManager.has(uuid)) {
            // Default Class/Level
            PrisonClass defaultClass = classManager.getClass("Prisoner");

            playerDataManager.set(new PlayerClassData(
                    uuid,
                    defaultClass,
                    1,
                    0
            ));
            PrisonClassHelper.setClass(event.getPlayer(), defaultClass);
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            PrisonClass killerClass = PrisonClassHelper.getClass(event.getEntity().getKiller());
            PrisonClass victimClass = PrisonClassHelper.getClass(event.getEntity());
            if (killerClass != null && victimClass != null) {
                Integer xpGain = killerClass.getXpGains().get(victimClass);
                if (xpGain != null) {
                    event.getEntity().getKiller().sendMessage(
                            Component.text("§bYou gained " + xpGain + " XP for killing " + event.getEntity().getName() + "!"));
                            PrisonClassHelper.addXpToPlayer(event.getEntity().getKiller(), xpGain);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerAttack(PrePlayerAttackEntityEvent e) {
        if (e.getAttacked() instanceof org.bukkit.entity.Player target && e.getPlayer() instanceof org.bukkit.entity.Player attacker) {
            PrisonClass attackerClass = PrisonClassHelper.getClass(attacker);
            PrisonClass targetClass = PrisonClassHelper.getClass(target);
            if (attackerClass == targetClass && attackerClass != null && !attackerClass.isFriendlyFire()) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (PrisonClassHelper.getClass(event.getPlayer()) != null) {
            List<ItemStack> respawnItems = Arrays.stream(PrisonClassHelper.getClass(event.getPlayer()).getRespawnItems(PrisonClassHelper.getPlayerLevel(event.getPlayer()))).filter(Objects::nonNull).toList();
            event.getPlayer().getInventory().addItem(respawnItems.toArray(new ItemStack[respawnItems.size()]));
        }
    }
}