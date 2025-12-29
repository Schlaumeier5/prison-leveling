package de.schlaumeier.serialization;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import de.schlaumeier.PrisonClass;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ClassManager {

    private final File file;
    private final Map<String, PrisonClass> classes = new LinkedHashMap<>();

    public ClassManager(File dataFolder) {
        this.file = new File(dataFolder, "classes.yml");
    }

    public Map<String, PrisonClass> getClasses() {
        return classes;
    }

    public PrisonClass getClass(String name) {
        return classes.get(name);
    }

    // ----------------------------
    // LOADING
    // ----------------------------
    public void load() {
        classes.clear();

        if (!file.exists()) {
            Bukkit.getLogger().warning("[Prison] classes.yml nicht gefunden (erstes Laden?)");
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        Map<String, PrisonClassData> raw = new HashMap<>();

        // Phase 1: DTOs einlesen und Objekte ohne Verknüpfungen erzeugen
        for (String key : config.getKeys(false)) {
            PrisonClassData data = (PrisonClassData) config.get(key);
            raw.put(data.name, data);

            PrisonClass pc = new PrisonClass(
                    data.name,
                    data.maxLevel,
                    data.experienceMultiplier,
                    data.permissionNode,
                    null
            );
            classes.put(data.name, pc);
        }

        // Phase 2: Verknüpfungen herstellen
        for (PrisonClassData data : raw.values()) {
            PrisonClass pc = classes.get(data.name);

            // Parent
            if (data.parentName != null) {
                PrisonClass parent = classes.get(data.parentName);
                if (parent != null)
                    pc.setParentClass(parent);
            }

            // Enemies
            for (String en : data.enemies) {
                PrisonClass enemy = classes.get(en);
                if (enemy != null)
                    pc.getEnemies().add(enemy);
            }

            // XP Gains
            for (Map.Entry<String, Integer> e : data.xpGains.entrySet()) {
                PrisonClass target = classes.get(e.getKey());
                if (target != null)
                    pc.getXpGains().put(target, e.getValue());
            }

            // Respawn Items
            for (List<ItemStack> items : data.respawnItems) {
                pc.getRespawnItems().add(items.toArray(new ItemStack[0]));
            }
        }

        checkForCircularParents();
        Bukkit.getLogger().info("[Prison] Klassen erfolgreich geladen: " + classes.size());
    }

    // ----------------------------
    // SAVING
    // ----------------------------
    public void save() {
        YamlConfiguration config = new YamlConfiguration();

        for (PrisonClass pc : classes.values())
            config.set(pc.getName(), toData(pc));

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // PrisonClass -> PrisonClassData
    private PrisonClassData toData(PrisonClass pc) {
        PrisonClassData d = new PrisonClassData();

        d.name = pc.getName();
        d.maxLevel = pc.getMaxLevel();
        d.experienceMultiplier = pc.getExperienceMultiplier();
        d.permissionNode = pc.getPermissionNode();
        d.parentName = pc.getParentClass() != null
                ? pc.getParentClass().getName()
                : null;

        for (Map.Entry<PrisonClass, Integer> e : pc.getXpGains().entrySet())
            d.xpGains.put(e.getKey().getName(), e.getValue());

        for (PrisonClass en : pc.getEnemies())
            d.enemies.add(en.getName());

        for (ItemStack[] arr : pc.getRespawnItems())
            d.respawnItems.add(Arrays.asList(arr));

        return d;
    }

    // ----------------------------
    // Zirkuläre Eltern erkennen
    // ----------------------------
    private void checkForCircularParents() {
        for (PrisonClass pc : classes.values()) {
            Set<PrisonClass> visited = new HashSet<>();
            PrisonClass cur = pc.getParentClass();

            while (cur != null) {
                if (!visited.add(cur)) {
                    Bukkit.getLogger().severe("[Prison] FEHLER: Zirkuläre Elternkette entdeckt bei Klasse: " + pc.getName());
                    pc.setParentClass(null);
                    break;
                }
                cur = cur.getParentClass();
            }
        }
    }
}
