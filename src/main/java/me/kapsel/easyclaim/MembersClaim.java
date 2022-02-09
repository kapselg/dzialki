package me.kapsel.easyclaim;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MembersClaim extends JavaPlugin {
    public static void AddMember(Player p, String mname){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(mname);

    }
    public static void RemoveMember(Player p, String mname){
        if(mname.equalsIgnoreCase(p.getName())) {
            Languages.memberIsOwner(p);
            return;
        }
        if(!ClaimData.get().isString(p.getName() + ".creation-date")) {
            Languages.noClaim(p);
            return;
        }

        ClaimData.reload();
        Player m = Bukkit.getPlayer(mname);

        //remove member from data file
        List<String> dataM = ClaimData.get().getStringList(p.getName() + ".members");
        if(dataM.isEmpty()){
            Languages.noMembers(p);
            return;
        }
        if(!dataM.contains(mname)){
            Languages.memberNotOnList(p);
            return;
        }

        dataM.remove(mname);
        ClaimData.get().set(p.getName() + ".members", dataM);

        //remove member from region
        RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld(ClaimData.get().getString(p.getName() + ".location.world"))));
        ProtectedRegion region = regions.getRegion(p.getName() + "_easy-claim-1");

        DefaultDomain regionM = region.getMembers();
        regionM.removePlayer(m.getUniqueId());

        region.setMembers(regionM);
        ClaimData.save();
        Languages.memberRemoved(p);
    }
    public static void ShowMembers(Player p){
        ClaimData.reload();
        if(!ClaimData.get().isString(p.getName() + ".creation-date")) {
            Languages.noClaim(p);
            return;
        }
        List<String> dataM = ClaimData.get().getStringList(p.getName() + ".members");
        if(dataM.isEmpty()){
            Languages.noMembers(p);
            return;
        }
        Languages.playerList(p);
        p.sendMessage(String.join(", ", dataM));
    }
}
