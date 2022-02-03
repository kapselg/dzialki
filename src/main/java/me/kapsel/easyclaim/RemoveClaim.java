package me.kapsel.easyclaim;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class RemoveClaim {
    public static void RemoveClaim(Player p, boolean isConfirmed){
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


    }
}
