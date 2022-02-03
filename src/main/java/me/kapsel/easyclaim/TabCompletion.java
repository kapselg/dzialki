package me.kapsel.easyclaim;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TabCompletion implements TabCompleter {
    List <String> commands = Arrays.asList("usun","dodaj","wyrzuc","czlonkowie","pomoc", "potwierdz", "reload");
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        Player p = (Player) sender;
        switch(args[0]){
            case("dodaj"):
            case("add"):
                List<String> onlinePlayers = new ArrayList<>();
                Bukkit.getOnlinePlayers().forEach((player)->{
                    onlinePlayers.add(p.getName());
                });
                return onlinePlayers;
            case("kick"):
            case("wyrzuc"):
                List<String> dataM = ClaimData.get().getStringList(p.getName() + ".members");
                Collections.sort(dataM);
                return dataM;
            default:
                return commands;
        }
    }
}
