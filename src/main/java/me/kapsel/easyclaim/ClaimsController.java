package me.kapsel.easyclaim;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.kapsel.easyclaim.dataFormats.ClaimInfo;
import me.kapsel.easyclaim.dataFormats.TpInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClaimsController {
    List<EasyClaim> claims;
    public void register(EasyClaim claim){
        ClaimData.get().set(claim.getOwner().getName(), new ClaimInfo(claim.getSize(), new TpInfo(claim.getLoc()), claim.getRegion()));
        ClaimData.save();
    }
    public void append(EasyClaim claim){
        if(claim.getP().hasPermission("EasyClaim.create")){

            RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(claim.getP().getWorld()));

            //checking if the flag does intersect with other claims
            for (ProtectedRegion intersectingRegion : claim.getRegion().getIntersectingRegions(regions.getRegions().values())){
                if(intersectingRegion.getFlag(Main.EASY_CLAIM) == StateFlag.State.ALLOW || intersectingRegion.getFlag(Main.EASY_CLAIM_ALLOW) == StateFlag.State.DENY){
                    Languages.intersection(claim.getP());
                    regions.removeRegion(claim.getRegion().getId());
                    return;
                }
            }
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
                regions.removeRegion(claim.getRegion().getId());
                return;
            }
            //remove required items from player's inventory
            for(ItemStack item: claim.getRequiredItems()){
                inv.removeItem(item);
            }
            ClaimInfo savedData = new ClaimInfo(claim.getSize(), new TpInfo(claim.getLoc()), claim.getRegion());

            ClaimData.get().set(claim.getP().getName(), savedData);
            ClaimData.save();

            regions.addRegion(claim.getRegion());
            Languages.claimCreated(claim.getP());
        }else{
            Languages.noPermission(claim.getP());
        }
    }
    public EasyClaim getClaim(Player p){
        String i = ClaimData.get().getString(p.getName()+".regionId");
        p.sendMessage(i);
        //if(i!=null) return new EasyClaim(i.getSize(), i.getTpInfo().getLocation(), i.getRegionId());
        return null;
    }
    public boolean delete(Player p, boolean isConfirmed){
        //check if player has a claim
        ClaimData.reload();
        ClaimInfo i = ClaimData.get().getObject(p.getName(), ClaimInfo.class);
        EasyClaim claim = new EasyClaim(i.getSize(), i.getTpInfo().getLocation(), i.getRegionId());
            if(isConfirmed){
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
                ClaimCommands.addConfirmation("remove", p);
                Languages.aboutToRemove(claim.getP());
                Languages.confirm(claim.getP());
            }


        return true;
    }
}
