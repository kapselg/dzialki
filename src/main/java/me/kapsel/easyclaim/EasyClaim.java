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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EasyClaim {

    private Player p;
    private ProtectedCuboidRegion region;
    private final List<ItemStack> requiredItems;
    private Location loc;
    int offset;

    //class constructor
    public EasyClaim(Player playerName){
        this.p = playerName;

            if(p.hasPermission("EasyClaim.VIP")){
                offset = (int) Main.plugin.getConfig().get("VIPSize") | 30;
            }else{
                offset = (int) Main.plugin.getConfig().get("DefaultSize") | 16;
            }

            //check if player has needed items
            //TODO: read those items from config file
             requiredItems = Arrays.asList(
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

            loc = p.getLocation();

            //calculate the region size
            //BlockVector3 vec = new BlockVector3(loc.getX(), loc.getY(), loc.getZ());
            BlockVector3 pt1 = BlockVector3.at(loc.getX() + offset/2, loc.getY() + offset/2, loc.getZ() + offset/2);
            BlockVector3 pt2 = BlockVector3.at(loc.getX() - offset/2, loc.getY() - offset/2, loc.getZ() - offset/2);

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
    public boolean create(){
        if(p.hasPermission("EasyClaim.create")){
            Inventory inv = p.getInventory();
            List<ItemStack> missing = new ArrayList<>();
            for(ItemStack item : requiredItems) {
                if (!inv.containsAtLeast(item, item.getAmount())) {
                    //count amount left to be colected
                    int itemsLeft = 0;
                    for (ItemStack invStack : inv.all(item.getType()).values()) {
                        itemsLeft += invStack.getAmount();
                    }
                    missing.add(new ItemStack(item.getType(), item.getAmount() - itemsLeft));

                }
            }
            if(!missing.isEmpty()){
                Languages.noReqItems(p, missing);
                return false;
            }
            //remove required items from player's inventory
            for(ItemStack item: requiredItems){
                inv.removeItem(item);
            }

            RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(p.getWorld()));

            //checking if the flag does intersect with other claims
            for (ProtectedRegion intersectingRegion : region.getIntersectingRegions(regions.getRegions().values())){
                if(intersectingRegion.getFlag(Main.EASY_CLAIM) == StateFlag.State.ALLOW || intersectingRegion.getFlag(Main.EASY_CLAIM_ALLOW) == StateFlag.State.DENY){
                    Languages.intersection(p);
                    regions.removeRegion(region.getId());
                    return false;
                }
            }

            ClaimData.get().set(p.getName() + ".location.X", loc.getX());
            ClaimData.get().set(p.getName() + ".location.Y", loc.getY());
            ClaimData.get().set(p.getName() + ".location.Z", loc.getZ());
            ClaimData.get().set(p.getName() + ".location.yaw", loc.getYaw());
            ClaimData.get().set(p.getName() + ".location.world", Objects.requireNonNull(loc.getWorld()).getName());
            ClaimData.get().set(p.getName() + ".size", offset);
            ClaimData.get().set(p.getName() + ".creation-date", new Date().toString());
            ClaimData.get().set(p.getName() + "._easy-claim-1.members", new String[]{""});
            ClaimData.save();

            regions.addRegion(region);

            Languages.claimCreated(p);
            return true;
        }else{
            Languages.noPermission(p);
            return false;
        }
    }

    public boolean delete(boolean isConfirmed){
        //check if player has a claim
        ClaimData.reload();
        if(isConfirmed){
            if(ClaimData.get().getString(p.getName()) != null){
                //remove region
                RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(p.getWorld()));
                assert regions != null;
                regions.removeRegion(p.getName() + "_easy-claim-1");
                //remove from claimdata
                ClaimData.get().set(p.getName(), null);
                ClaimData.save();
                List<ItemStack> reqi = Arrays.asList(
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
                for(ItemStack item : reqi){
                    p.getInventory().addItem(item);
                }
                Languages.claimRemoved(p);
            }else{
                //no claim
                Languages.noClaimsToRemove(p);

            }
        }else{
            ClaimCommand.addConfirmation("remove", p);
            Languages.aboutToRemove(p);
            Languages.confirm(p);
        }

        return true;
    }

    public ProtectedRegion getRegion(){
        return region;
    }
}
