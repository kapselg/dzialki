package me.kapsel.easyclaim;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class EasyClaim {

    private Player p;
    private ProtectedCuboidRegion region;
    private List<ItemStack> requiredItems = Arrays.asList(
            new ItemStack(Material.ENDER_PEARL, 4),
            new ItemStack(Material.DIAMOND, 16),
            new ItemStack(Material.LAPIS_LAZULI, 32),
            new ItemStack(Material.BOOKSHELF, 16),
            new ItemStack(Material.APPLE, 32),
            new ItemStack(Material.IRON_INGOT, 32),
            new ItemStack(Material.GOLD_INGOT, 32),
            new ItemStack(Material.OBSIDIAN, 16),
            new ItemStack(Material.COPPER_INGOT, 32)
    );
    private Location loc;
    private int size;

    public EasyClaim(){

    }
    //class constructor
    public EasyClaim(Player p){
        this(p.getName());
    }
    public EasyClaim(String pl){
        p  = Bukkit.getPlayer(pl);
        if(p == null) return;
        if(p.hasPermission("EasyClaim.VIP")){
            size = (int) Main.plugin.getConfig().get("VIPSize") | 30;
        }else{
            size = (int) Main.plugin.getConfig().get("DefaultSize") | 16;
        }

        //check if player has needed items
        //TODO: read those items from config file

        loc = p.getLocation();
        BlockVector3 pt1, pt2;
        //calculate the region size
        //BlockVector3 vec = new BlockVector3(loc.getX(), loc.getY(), loc.getZ());
        pt1 = BlockVector3.at(loc.getX() + size / 2.0, loc.getY() + size /2, loc.getZ() + size /2);
        pt2 = BlockVector3.at(loc.getX() - size /2, loc.getY() - size /2, loc.getZ() - size /2);

        region = new ProtectedCuboidRegion(
                p.getName() + "_easy-claim-1",
                pt1,
                pt2
        );

        //antigrief flags
        region.setFlag(Flags.PASSTHROUGH, StateFlag.State.DENY);
        region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
        DefaultDomain owner = new DefaultDomain();
        owner.addPlayer(p.getUniqueId());
        region.setOwners(owner);

        //custom identification flag
        region.setFlag(Main.EASY_CLAIM, StateFlag.State.ALLOW);
    }
    public ProtectedRegion getRegion(){
        return region;
    }

    public Player getP() {
        return p;
    }

    public void setP(Player p) {
        this.p = p;
    }

    public void setRegion(ProtectedCuboidRegion region) {
        this.region = region;
    }

    public List<ItemStack> getRequiredItems() {
        return requiredItems;
    }

    public void setRequiredItems(List<ItemStack> requiredItems) {
        this.requiredItems = requiredItems;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
