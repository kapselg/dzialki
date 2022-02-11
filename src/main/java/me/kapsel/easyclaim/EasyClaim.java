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

import java.util.*;

/**
 * Represents and manages data of a claim.
 * Can be sent to ClaimsController to be registered in plugin's data file.
 */
public class EasyClaim{
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

    /**
     * Constructor to be used ONLY by ClaimsController to create instance for existing claims
     * @param size Claim's size
     * @param location Claim's teleport location
     * @param regionName Claim's WorldGuard region name
     */
    public EasyClaim(int size, Location location, String regionName){
        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld())));
        assert manager != null;
        ProtectedRegion region = manager.getRegion(regionName);
        if(region!=null){
            this.region = (ProtectedCuboidRegion) region;
            this.size = size;
            this.loc = location;
            this.p = Bukkit.getPlayer(region.getOwners().getPlayerDomain().getUniqueIds().iterator().next());
        }
    }

    /**
     * Creates a new claim based on provided player's information
     * @param p - Player standing in the middle of desired claim
     */
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

    /**
     *
     * Teports specified player to the claims stored TP location
     * @param p Player to be teleported
     * @return true or false depending on teleportation success
     */
    public boolean teleport(Player p){
        Languages.teleported(p);
        return p.teleport(loc);
    }
    public Player getOwner(){
        return Bukkit.getPlayer(this.region.getOwners().getUniqueIds().iterator().next());
    }
    public boolean addMember(Player member){
        Player s = this.getOwner();
        if(s == member) {
            Languages.memberIsOwner(member);
            return false;
        }
        if(!member.isOnline()) {
            Languages.memberAdded(getOwner());
            return false;
        }
        if(this.region.getMembers().contains(member.getUniqueId())) {
            Languages.alreadyOnList(s);
            return false;
        }

        this.region.getMembers().addPlayer(member.getUniqueId());

        Languages.memberAdded(s);
        Languages.youHaveBeenAdded(member, s);
        return true;
    }

    /**
     * Adds claim's member to the claim by their name
     * @param member Player to be added
     * @return true if player was added
     */
    public boolean addMember(String member){
        Player playerToBeAdded = Main.plugin.getServer().getPlayer(member);
        if(playerToBeAdded != null){
            return this.addMember(playerToBeAdded);

        }else{
            Languages.noSuchPlayer(p);
            return false;
        }
    }

    /**
     * Gets list of claim's members
     * @return claim's members list
     */
    public List<Player> getMembers(){
        List<Player> result = new ArrayList<>();
        for(UUID playerId : this.getRegion().getMembers().getUniqueIds()) result.add(Bukkit.getPlayer(playerId));
        return result;
    }

    /**
     * Removes claim's member from the claim
     * @param member Player to be removed
     * @return true if player was removed
     */
    public boolean removeMember(Player member){
        Player s = this.getOwner();
        if(!this.getMembers().contains(member)){
            Languages.noSuchPlayer(s);
            return false;
        }
        if(!this.getMembers().isEmpty()){
            Languages.noMembers(s);
            return false;
        }
        if(s.getName().equals(member.getName())){
            Languages.memberIsOwner(s);
            return false;
        }
        if(!this.getMembers().contains(member)){
            Languages.memberNotOnList(s);
            return false;
        }
        this.region.getMembers().removePlayer(member.getUniqueId());
        Languages.memberRemoved(p);
        return true;
    }

    /**
     * Removes claim's member from claim by their name
     * @param memberName Player to be removed
     * @return true if player was removed
     */
    public boolean removeMember(String memberName){
        Player playerToBeRemoved = Main.plugin.getServer().getPlayer(memberName);
        if(playerToBeRemoved != null){
            return this.removeMember(playerToBeRemoved);
        }else{
            Languages.noSuchPlayer(p);
            return false;
        }
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
