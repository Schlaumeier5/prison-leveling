package de.schlaumeier.serialization;

import java.util.UUID;

import de.schlaumeier.PrisonClass;

public class PlayerClassData {
    private final UUID uuid;
    private PrisonClass prisonClass;
    private int level;
    private double xp;

    public PlayerClassData(UUID uuid, PrisonClass prisonClass, int level, double xp) {
        this.uuid = uuid;
        this.prisonClass = prisonClass;
        this.level = level;
        this.xp = xp;
    }

    public UUID getUuid() { return uuid; }
    public PrisonClass getPrisonClass() { return prisonClass; }
    public void setPrisonClass(PrisonClass pc) { this.prisonClass = pc; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public double getXp() { return xp; }
    public void setXp(double xp) { this.xp = xp; }
    public void addXp(double amount) { this.xp += amount; }
}
