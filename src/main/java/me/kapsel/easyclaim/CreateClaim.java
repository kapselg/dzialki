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
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CreateClaim extends JavaPlugin {
    public static void CreateClaim(Player p, boolean args){

        ClaimData.reload();
        //does the player have a claim
        if(ClaimData.get().getString(p.getName()) != null){
            if(args == true) {
                Languages.claimExists(p);
                return;
            }
            //teleport to claim
            TeleportToClaim.TeleportToClaim(p);
        }else{
            if(p.hasPermission("EasyClaim.create")){

                int offset;
                if(p.hasPermission("EasyClaim.VIP")){
                    offset = (int) EasyClaim.plugin.getConfig().get("VIPSize") | 30;
                }else{
                    offset = (int) EasyClaim.plugin.getConfig().get("DefaultSize") | 16;
                }
                //check if player has needed items
                //TODO: read those items from config file
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
                Inventory inv = p.getInventory();
                List<ItemStack> missing = new ArrayList<>();
                for(ItemStack item : reqi) {
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
                    return;
                }
                //remove required items from player's inventory
                for(ItemStack item: reqi){
                    inv.removeItem(item);
                }
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(p.getWorld()));
                Location loc = p.getLocation();

                //calculate the region size
                //BlockVector3 vec = new BlockVector3(loc.getX(), loc.getY(), loc.getZ());
                BlockVector3 pt1 = BlockVector3.at(loc.getX() + offset/2, loc.getY() + offset/2, loc.getZ() + offset/2);
                BlockVector3 pt2 = BlockVector3.at(loc.getX() - offset/2, loc.getY() - offset/2, loc.getZ() - offset/2);

                ProtectedRegion newRegion = new ProtectedCuboidRegion(
                        p.getName() + "_easy-claim-1",
                        pt1,
                        pt2
                );
                regions.addRegion(newRegion);
                //checking if the flag does intersect with other claims
                for (ProtectedRegion intersectingRegion : newRegion.getIntersectingRegions(regions.getRegions().values())){
                    if(intersectingRegion.getFlag(EasyClaim.EASY_CLAIM) == StateFlag.State.ALLOW || intersectingRegion.getFlag(EasyClaim.EASY_CLAIM_ALLOW) == StateFlag.State.DENY){
                        Languages.intersection(p);
                        regions.removeRegion(newRegion.getId());
                        return;
                    }
                }

                //antigrief flags
                newRegion.setFlag(Flags.PASSTHROUGH, StateFlag.State.DENY);
                newRegion.setFlag(Flags.PVP, StateFlag.State.ALLOW);
                DefaultDomain owner = new DefaultDomain();
                owner.addPlayer(p.getUniqueId());
                newRegion.setOwners(owner);
                //custom identification flag
                newRegion.setFlag(EasyClaim.EASY_CLAIM, StateFlag.State.ALLOW);

                ClaimData.get().set(p.getName() + ".location.X", loc.getX());
                ClaimData.get().set(p.getName() + ".location.Y", loc.getY());
                ClaimData.get().set(p.getName() + ".location.Z", loc.getZ());
                ClaimData.get().set(p.getName() + ".location.yaw", loc.getYaw());
                ClaimData.get().set(p.getName() + ".location.world", Objects.requireNonNull(loc.getWorld()).getName());
                ClaimData.get().set(p.getName() + ".size", offset);
                ClaimData.get().set(p.getName() + ".creation-date", new Date().toString());
                ClaimData.get().set(p.getName() + "._easy-claim-1.members", new String[]{""});
                ClaimData.save();
                assert regions != null;

                Languages.claimCreated(p);

            }else{
                Languages.noPermission(p);
            }
        }


        //create a claim
        //register it in config's file

    }
}
