package de.schlaumeier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PrisonClass {
    private final String name;
    private final int maxLevel;
    private final double experienceMultiplier;
    private final String permissionNode;
    private final List<ItemStack[]> respawnItems = new ArrayList<>();
    private final List<PrisonClass> enemies = new ArrayList<>();
    private final Map<PrisonClass, Integer> xpGains = new java.util.HashMap<>();
    @Nullable private PrisonClass parentClass;
    private final boolean friendlyFire;

    public PrisonClass(
            String name,
            int maxLevel,
            double experienceMultiplier,
            String permissionNode,
            @Nullable PrisonClass parentClass,
            boolean friendlyFire) {
        this.name = name;
        this.maxLevel = maxLevel;
        this.experienceMultiplier = experienceMultiplier;
        this.permissionNode = permissionNode;
        this.parentClass = parentClass;
        this.friendlyFire = friendlyFire;
    }

    public String getName() {
        return name;
    }
    public int getMaxLevel() {
        return maxLevel;
    }
    public double getExperienceMultiplier() {
        return experienceMultiplier;
    }
    public String getPermissionNode() {
        return permissionNode;
    }
    public List<ItemStack[]> getRespawnItems() {
        if (respawnItems.isEmpty() && parentClass != null) {
            return parentClass.getRespawnItems();
        }
        return respawnItems;
    }
    public List<PrisonClass> getEnemies() {
        return enemies;
    }
    public PrisonClass getParentClass() {
        return parentClass;
    }
    public Map<PrisonClass, Integer> getXpGains() {
        return xpGains;
    }
    public boolean isEnemy(PrisonClass otherClass) {
        return enemies.contains(otherClass);
    }
    public int getXpGainAgainst(PrisonClass otherClass) {
        return xpGains.getOrDefault(otherClass, 0);
    }
    public void addRespawnItems(ItemStack[] items, int level) {
        while (respawnItems.size() <= level) {
            respawnItems.add(new ItemStack[0]);
        }
        respawnItems.set(level, items);
    }
    public boolean isFriendlyFire() {
        return friendlyFire;
    }
    public ItemStack[] getRespawnItems(int level) {
        if (level < respawnItems.size()) {
            return respawnItems.get(level);
        } else if (respawnItems.size() > 0) {
            return respawnItems.get(respawnItems.size() - 1);
        } else if (parentClass != null) {
            return parentClass.getRespawnItems(level);
        } else {
            return new ItemStack[0];
        }
    }
    public void setParentClass(@Nullable PrisonClass parentClass) {
        this.parentClass = parentClass;
    }
}
