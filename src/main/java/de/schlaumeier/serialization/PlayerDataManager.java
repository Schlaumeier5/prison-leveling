package de.schlaumeier.serialization;

import org.bukkit.configuration.file.YamlConfiguration;

import de.schlaumeier.PrisonClass;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerDataManager {

    private final File file;
    private final ClassManager classManager;

    private final Map<UUID, PlayerClassData> playerData = new HashMap<>();

    public PlayerDataManager(File dataFolder, ClassManager classManager) {
        this.file = new File(dataFolder, "playerdata.yml");
        this.classManager = classManager;
    }

    public PlayerClassData get(UUID uuid) {
        return playerData.get(uuid);
    }

    public void set(PlayerClassData data) {
        playerData.put(data.getUuid(), data);
    }

    public boolean has(UUID uuid) {
        return playerData.containsKey(uuid);
    }

    // -------------------------------------
    // LOAD
    // -------------------------------------
    public void load() {
        playerData.clear();

        if (!file.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            String className = config.getString(key + ".class");
            int level = config.getInt(key + ".level");
            double xp = config.getDouble(key + ".xp");

            PrisonClass pc = className != null ? classManager.getClass(className) : null;

            if (pc == null) {
                System.err.println("WARN: Klasse " + className + " nicht gefunden für Spieler " + key);
                continue;
            }

            PlayerClassData data = new PlayerClassData(uuid, pc, level, xp);
            playerData.put(uuid, data);
        }
    }

    // -------------------------------------
    // SAVE
    // -------------------------------------
    public void save() {
        YamlConfiguration config = new YamlConfiguration();

        for (PlayerClassData data : playerData.values()) {

            String path = data.getUuid().toString();

            config.set(path + ".class", data.getPrisonClass().getName());
            config.set(path + ".level", data.getLevel());
            config.set(path + ".xp", data.getXp());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePlayer(UUID uuid) {
        if (!playerData.containsKey(uuid)) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        PlayerClassData data = playerData.get(uuid);
        String path = uuid.toString();

        config.set(path + ".class", data.getPrisonClass().getName());
        config.set(path + ".level", data.getLevel());
        config.set(path + ".xp", data.getXp());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
