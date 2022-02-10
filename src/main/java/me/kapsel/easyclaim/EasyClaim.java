package me.kapsel.easyclaim;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class EasyClaim {
    private RegionManager manager;
    private Player p;
    private ProtectedCuboidRegion region;
    //TODO: un hard-code it!!
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
    public EasyClaim(int size, Location location, String regionName){
        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld()));
        ProtectedRegion region = manager.getRegion(regionName);
        if(region!=null){
            this.region = (ProtectedCuboidRegion) region;
            this.size = size;
            this.loc = location;
            this.p = Bukkit.getPlayer(region.getOwners().getPlayerDomain().getUniqueIds().iterator().next());
            this.manager = manager;
        }
    }
    //class constructor
    public EasyClaim(Player p){
        this.p = p;
        if(p.hasPermission("EasyClaim.VIP")){
            size = (int) Main.plugin.getConfig().get("VIPSize") | 30;
        }else{
            size = (int) Main.plugin.getConfig().get("DefaultSize") | 16;
        }

        loc = p.getLocation();
        BlockVector3 pt1, pt2;
        //calculate the region size
        //BlockVector3 vec = new BlockVector3(loc.getX(), loc.getY(), loc.getZ());
        pt1 = BlockVector3.at(loc.getX() + size / 2.0, loc.getY() + size /2.0, loc.getZ() + size /2.0);
        pt2 = BlockVector3.at(loc.getX() - size /2.0, loc.getY() - size /2.0, loc.getZ() - size /2.0);

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
    public boolean teleport(Player p){
        Languages.teleported(p);
        return p.teleport(loc);
    }
    public Player getOwner(){
        return Bukkit.getPlayer(this.region.getOwners().getUniqueIds().iterator().next());
    }
    public void addPlayer(Player member){
        if(this.getOwner() == member) {
            Languages.memberIsOwner(member);
            return;
        }
        if(member.isOnline()) {
            Languages.memberAdded(getOwner());
            return;
        }
        this.region.getMembers().addPlayer(member.getUniqueId());

        Languages.memberAdded(member);
        Languages.youHaveBeenAdded(member, this.getOwner());
    }
    public boolean addPlayer(String memberName){
        Player playerToBeAdded = Bukkit.getPlayer(memberName);
        if(playerToBeAdded != null){
            this.addPlayer(playerToBeAdded);
            return true;
        }else{
            Languages.noSuchPlayer(p);
            return false;
        }
    }
    public List<Player> getMembers(){
        List<Player> result = new ArrayList<>();
        for(UUID playerId : this.getRegion().getMembers().getUniqueIds()) result.add(Bukkit.getPlayer(playerId));
        return result;
    }
    public void removeMember(Player sender, Player member){
        if(!this.getMembers().contains(member)){
            Languages.noSuchPlayer(sender);
            return;
        }
        if(!this.getMembers().isEmpty()){
            Languages.noMembers(sender);
            return;
        }
        if(sender.getName().equals(member.getName())){
            Languages.memberIsOwner(sender);
            return;
        }
        if(!this.getMembers().contains(member)){
            Languages.memberNotOnList(sender);
            return;
        }
        this.region.getMembers().removePlayer(member.getUniqueId());
        Languages.memberRemoved(p);
    }

    //region gettters setters
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
    //endregion
}
