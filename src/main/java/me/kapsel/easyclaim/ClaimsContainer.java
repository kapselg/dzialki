package me.kapsel.easyclaim;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
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

public class ClaimsContainer {
    List<EasyClaim> claims;
    public ClaimsContainer(){
        claims = new ArrayList<>();
    }
    public boolean register(EasyClaim claim){
        if(claim.getP().hasPermission("EasyClaim.create")){
            Inventory inv = claim.getP().getInventory();
            List<ItemStack> missing = new ArrayList<>();
            for(ItemStack item : claim.getRequiredItems()) {
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
                Languages.noReqItems(claim.getP(), missing);
                return false;
            }
            //remove required items from player's inventory
            for(ItemStack item: claim.getRequiredItems()){
                inv.removeItem(item);
            }

            RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(claim.getP().getWorld()));

            //checking if the flag does intersect with other claims
            for (ProtectedRegion intersectingRegion : claim.getRegion().getIntersectingRegions(regions.getRegions().values())){
                if(intersectingRegion.getFlag(Main.EASY_CLAIM) == StateFlag.State.ALLOW || intersectingRegion.getFlag(Main.EASY_CLAIM_ALLOW) == StateFlag.State.DENY){
                    Languages.intersection(claim.getP());
                    regions.removeRegion(claim.getRegion().getId());
                    return false;
                }
            }

            ClaimData.get().set(claim.getP().getName() + "._easy-claim-1.id", claim.getRegion().getId());
            ClaimData.get().set(claim.getP().getName() + "._easy-claim-1.location.X", claim.getLoc().getX());
            ClaimData.get().set(claim.getP().getName() + "._easy-claim-1.location.Y", claim.getLoc().getY());
            ClaimData.get().set(claim.getP().getName() + "._easy-claim-1.location.Z", claim.getLoc().getZ());
            ClaimData.get().set(claim.getP().getName() + "._easy-claim-1.location.yaw", claim.getLoc().getYaw());
            ClaimData.get().set(claim.getP().getName() + "._easy-claim-1.location.world", Objects.requireNonNull(claim.getLoc().getWorld()).getName());
            ClaimData.get().set(claim.getP().getName() + "._easy-claim-1.size", claim.getSize());
            ClaimData.get().set(claim.getP().getName() + "._easy-claim-1.creation-date", new Date().toString());
            ClaimData.get().set(claim.getP().getName() + "._easy-claim-1.members", new String[]{""});
            ClaimData.save();

            regions.addRegion(claim.getRegion());

            Languages.claimCreated(claim.getP());
            return true;
        }else{
            Languages.noPermission(claim.getP());
            return false;
        }
    }
    public EasyClaim getClaim(Player p){
        EasyClaim r = new EasyClaim();
        r.setP(p);
        RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(p.getWorld()));
        r.setRegion((ProtectedCuboidRegion) regions.getRegion(p.getName() + "_easy-claim-1"));
        r.setSize(ClaimData.get().getInt(p.getName() + "._easy-claim-1.size"));
        r.setLoc(new Location(
                p.getWorld(),
                ClaimData.get().getDouble(p.getName() + "._easy-claim-1.location.X"),
                ClaimData.get().getDouble(p.getName() + "._easy-claim-1.location.Y"),
                ClaimData.get().getDouble(p.getName() + "._easy-claim-1.location.Z")
                ));
        return r;
    }
    public boolean delete(EasyClaim claim, boolean isConfirmed){
        //check if player has a claim
        ClaimData.reload();
        if(isConfirmed){
            if(ClaimData.get().getString(claim.getP().getName()) != null){
                //remove region
                RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(claim.getP().getWorld()));
                assert regions != null;

                regions.removeRegion(claim.getP().getName() + "_easy-claim-1");
                //remove from claimdata
                ClaimData.get().set(claim.getP().getName(), null);
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
                    claim.getP().getInventory().addItem(item);
                }
                Languages.claimRemoved(claim.getP());
            }else{
                //no claim
                Languages.noClaimsToRemove(claim.getP());

            }
        }else{
            ClaimCommand.addConfirmation("remove", claim.getP());
            Languages.aboutToRemove(claim.getP());
            Languages.confirm(claim.getP());
        }

        return true;
    }
}
