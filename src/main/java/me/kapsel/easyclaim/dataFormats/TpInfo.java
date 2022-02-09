package me.kapsel.easyclaim.dataFormats;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import org.bukkit.Location;

import java.util.Objects;

public class TpInfo {
    private double x;
    private double y;
    private double yaw;
    private String world;

    public TpInfo(double x, double y, double yaw, String world) {
        this.x = x;
        this.y = y;
        this.yaw = yaw;
        this.world = world;
    }

    public TpInfo(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.yaw = loc.getYaw();
        this.world = Objects.requireNonNull(loc.getWorld()).getName();
    }

    public TpInfo() {
    }

    public TpInfo(double x, double y, double yaw, BukkitWorld world) {
        this(x, y, yaw, world.getName());
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(BukkitWorld world) {
        this.world = world.getId();
    }

    public void setWorld(String world) {
        this.world = world;
    }
}
