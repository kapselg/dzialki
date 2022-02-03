package me.kapsel.easyclaim;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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

        }


        //create a claim
        //register it in config's file

    }
}
