package me.kapsel.easyclaim;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TeleportToClaim {

    public static void TeleportToClaim(Player p){
        ClaimData.reload();
        if(ClaimData.get().getString(p.getName()) != null){

            Location dest = new Location(
                    Bukkit.getWorld(Objects.requireNonNull(ClaimData.get().getString(p.getName() + ".location.world"))),
                    ClaimData.get().getDouble(p.getName() + ".location.X"),
                    ClaimData.get().getDouble(p.getName() + ".location.Y"),
                    ClaimData.get().getDouble(p.getName() + ".location.Z"),
                    (float) (ClaimData.get().getDouble(p.getName() + ".location.yaw")), 0);
            p.teleport(dest);
            Languages.teleported(p);

        }else{
            Languages.noClaim(p);
        }
    }

}
