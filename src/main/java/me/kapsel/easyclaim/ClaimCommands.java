package me.kapsel.easyclaim;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
public class ClaimCommands implements CommandExecutor {
    public static HashMap<Player, Confirmation> confirmations = new HashMap<>();
    //handle pending confirmations
    public static void addConfirmation(String command, Player p){
        confirmations.put(p, new Confirmation(command));
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /eclaim - tp
        if(command.getName().equalsIgnoreCase("easyclaim")){
            ClaimsController claimsController = new ClaimsController();
            if(!(sender instanceof Player)) {
                Languages.notPlayer(sender);
            }
            Player p = (Player) sender;
            //code for /eclaim
            EasyClaim ec = claimsController.getClaim(p);
            if(args.length == 0){
                //code for /eclaim
                if(ec !=null){
                    ec.teleport(p);
                }else{
                    ec = new EasyClaim(p);
                    claimsController.append(ec);
                    return true;
                }
            }else{
                if(ec==null){
                    Languages.noClaim(p);
                    return false;
                }
                switch (args[0]){
                    default:
                        //code for /eclaim claim
                        if(ec.teleport(p)){
                            Languages.teleported(p);
                        }else{
                            p.performCommand("help easyclaim");
                        }
                        break;
                    case("remove"):
                    case("usun"):
                        //code for /eclaim remove
                        claimsController.delete(p, false);
                        break;
                    case("add"):
                    case("dodaj"):
                        if(ec.addPlayer(args[0]))
                            claimsController.register(ec);
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
                            Main.plugin.reloadConfig();
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
                            if(confirmations.get(p).command.equalsIgnoreCase("remove")) claimsController.delete(p, true);
                        }


                }
            }

        }
        return true;
    }
}
