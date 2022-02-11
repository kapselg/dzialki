package me.kapsel.easyclaim.dataFormats;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/**
 * Serializable class format for storing data of a claim's teleportation target in data file.
 */
@SerializableAs("TpInfo")
public class TpInfo implements ConfigurationSerializable {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private String world;



    public TpInfo(double x, double y, double z, float yaw, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.world = world;
    }

    public TpInfo(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.world = Objects.requireNonNull(loc.getWorld()).getName();
    }

    public TpInfo() {
    }

    public TpInfo(double x, double y, double z,  float yaw, BukkitWorld world) {
        this(x, y, z, yaw, world.getName());
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

    public void setYaw(float yaw) {
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

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Location getLocation(){
        return new Location(Bukkit.getWorld(world), this.x, this.y, this.z, this.yaw, 0);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("x", this.getX());
        result.put("y", this.getY());
        result.put("z", this.getZ());
        result.put("yaw", String.valueOf(this.getYaw()));
        result.put("world", this.getWorld());
        return result;
    }

    //deserialize()
    public TpInfo(Map<String, Object> input){
        this((double) input.get("x"), (double) input.get("y"), (double) input.get("z"),  Float.parseFloat(input.get("yaw").toString()), (String) input.get("world"));
    }
}
