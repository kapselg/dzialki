package me.kapsel.easyclaim;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ClaimsContainer {
    List<EasyClaim> claims;
    public ClaimsContainer(){
        claims = new ArrayList<>();
    }

    public boolean create(EasyClaim claim){
        if(claim.p.hasPermission("EasyClaim.create")){
            Inventory inv = claim.p.getInventory();
            List<ItemStack> missing = new ArrayList<>();
            for(ItemStack item : claim.requiredItems) {
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
                Languages.noReqItems(claim.p, missing);
                return false;
            }
            //remove required items from player's inventory
            for(ItemStack item: claim.requiredItems){
                inv.removeItem(item);
            }

            RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(claim.p.getWorld()));

            //checking if the flag does intersect with other claims
            for (ProtectedRegion intersectingRegion : claim.region.getIntersectingRegions(regions.getRegions().values())){
                if(intersectingRegion.getFlag(Main.EASY_CLAIM) == StateFlag.State.ALLOW || intersectingRegion.getFlag(Main.EASY_CLAIM_ALLOW) == StateFlag.State.DENY){
                    Languages.intersection(claim.p);
                    regions.removeRegion(claim.region.getId());
                    return false;
                }
            }

            ClaimData.get().set(claim.p.getName() + ".location.X", claim.loc.getX());
            ClaimData.get().set(claim.p.getName() + ".location.Y", claim.loc.getY());
            ClaimData.get().set(claim.p.getName() + ".location.Z", claim.loc.getZ());
            ClaimData.get().set(claim.p.getName() + ".location.yaw", claim.loc.getYaw());
            ClaimData.get().set(claim.p.getName() + ".pt1", claim.pt1);
            ClaimData.get().set(claim.p.getName() + ".pt2", claim.pt2);
            ClaimData.get().set(claim.p.getName() + ".location.world", Objects.requireNonNull(claim.loc.getWorld()).getName());
            ClaimData.get().set(claim.p.getName() + ".size", claim.offset);
            ClaimData.get().set(claim.p.getName() + ".creation-date", new Date().toString());
            ClaimData.get().set(claim.p.getName() + "._easy-claim-1.members", new String[]{""});
            ClaimData.save();

            regions.addRegion(claim.region);

            Languages.claimCreated(claim.p);
            return true;
        }else{
            Languages.noPermission(claim.p);
            return false;
        }
    }
    public boolean getClaim(Player p){
        new EasyClaim(ClaimData.get().getList(p.getName()));
    }
}
