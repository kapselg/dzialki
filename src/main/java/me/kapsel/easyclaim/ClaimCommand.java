package me.kapsel.easyclaim;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
public class ClaimCommand implements CommandExecutor {
    public static HashMap<Player, Confirmation> confirmations = new HashMap<>();
    //handle pending confirmations
    public static void addConfirmation(String command, Player p){
        confirmations.put(p, new Confirmation(command));
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /eclaim - tp
        if(command.getName().equalsIgnoreCase("easyclaim")){
            if(!(sender instanceof Player)) {
                Languages.notPlayer(sender);
            }
            Player p = (Player) sender;
            //code for /eclaim
            if(args.length == 0){
                //code for /eclaim
                CreateClaim.CreateClaim(p, false);
            }else{
                switch (args[0]){
                    default:
                        //code for /eclaim claim
                        CreateClaim.CreateClaim(p, true);
                        break;
                    case("remove"):
                    case("usun"):
                        //code for /eclaim remove
                        RemoveClaim.RemoveClaim(p, false);
                        break;
                    case("add"):
                    case("dodaj"):
                        //code for /eclaim add
                        MembersClaim.AddMember(p, args[1]);
                        break;
                    case("kick"):
                    case("wyrzuc"):
                        //code for /eclaim kick
                        MembersClaim.RemoveMember(p, args[1]);
                        break;
                    case("czlonkowie"):
                    case("members"):
                        //code for /eclaim members
                        MembersClaim.ShowMembers(p);
                        break;
                    case("help"):
                    case("pomoc"):
                        //code for /eclaim help
                        p.performCommand("help easyclaim");
                        break;
                    case("reload"):
                        if(p.hasPermission("EasyClaim.reload")){
                            EasyClaim.plugin.reloadConfig();
                            Languages.configReloaded(p);
                        }else {
                            Languages.noPermission(p);
                        }
                        break;
                    case("confirm"):
                    case("potwierdz"):
                        if(confirmations.get(p) == null || confirmations.get(p).expired()){
                            Languages.noConfirmations(sender);
                            return true;
                        }else{
                            if(confirmations.get(p).command == "remove") RemoveClaim.RemoveClaim(p, true);
                        }


                }
            }

        }
        return true;
    }
}
