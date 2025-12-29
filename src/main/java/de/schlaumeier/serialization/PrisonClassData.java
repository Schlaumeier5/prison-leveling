package de.schlaumeier.serialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PrisonClassData implements ConfigurationSerializable {
    public String name;
    public int maxLevel;
    public double experienceMultiplier;
    public String permissionNode;
    public String parentName;
    public Map<String, Integer> xpGains = new HashMap<>();
    public List<String> enemies = new ArrayList<>();
    public List<List<ItemStack>> respawnItems = new ArrayList<>();
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("name", name);
        map.put("maxLevel", maxLevel);
        map.put("experienceMultiplier", experienceMultiplier);
        map.put("permissionNode", permissionNode);
        map.put("parentName", parentName);
        map.put("xpGains", xpGains);
        map.put("enemies", enemies);
        map.put("respawnItems", respawnItems);

        return map;
    }

    @SuppressWarnings("unchecked")
    public static PrisonClassData deserialize(Map<String, Object> map) {
        PrisonClassData data = new PrisonClassData();

        data.name = (String) map.get("name");
        data.maxLevel = (int) map.get("maxLevel");
        data.experienceMultiplier = ((Number) map.get("experienceMultiplier")).doubleValue();
        data.permissionNode = (String) map.get("permissionNode");
        data.parentName = (String) map.get("parentName");

        data.xpGains = (Map<String, Integer>) map.getOrDefault("xpGains", new HashMap<>());
        data.enemies = (List<String>) map.getOrDefault("enemies", new ArrayList<>());

        // respawnItems ist eine Liste von Listen
        List<?> rawRespawn = (List<?>) map.getOrDefault("respawnItems", new ArrayList<>());
        for (Object o : rawRespawn) {
            data.respawnItems.add((List<ItemStack>) o);
        }

        return data;
    }
}
