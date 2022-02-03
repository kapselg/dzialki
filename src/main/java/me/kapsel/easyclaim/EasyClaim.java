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

import java.util.Arrays;
import java.util.List;

public class EasyClaim {

    final Player p;
    final ProtectedCuboidRegion region;
    final List<ItemStack> requiredItems;
    final Location loc;
    final BlockVector3 pt1;
    final BlockVector3 pt2;
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
            pt1 = BlockVector3.at(loc.getX() + offset/2, loc.getY() + offset/2, loc.getZ() + offset/2);
            pt2 = BlockVector3.at(loc.getX() - offset/2, loc.getY() - offset/2, loc.getZ() - offset/2);

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
    public EasyClaim(String playerName){
        p = Bukkit.getPlayer(playerName);

    }
    //pushes the claim to database
    //removes claim from db
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
