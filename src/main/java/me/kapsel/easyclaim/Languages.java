package me.kapsel.easyclaim;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Languages {
    public static void notPlayer(CommandSender s){
        s.sendMessage(ChatColor.RED + "Komenda może być użyta tylko przez gracza!");
    }
    public static void noPermission(Player p){
        p.sendMessage(ChatColor.RED + "Nie posiadasz uprawnień do wykonania tej komendy");
    }
    public static void claimCreated(Player p){
        p.sendMessage(ChatColor.GREEN + "Działka została utworzona");
    }
    public static void confirm(Player p){
        TextComponent tc1 = new TextComponent("Potwierdź wpisując ");

        TextComponent tc2 = new TextComponent("/dzialka potwierdz ");
        tc2.setColor(ChatColor.GOLD);

        TextComponent tc2a = new TextComponent("(lub kliknij ");

        TextComponent tc3 = new TextComponent(ChatColor.GREEN +""+ ChatColor.UNDERLINE + "TUTAJ");
        tc3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/easyclaim confirm"));

        TextComponent tc4 = new TextComponent(")");

        p.spigot().sendMessage(tc1, tc2,tc2a, tc3, tc4);
    }
    public static void claimRemoved(Player p){
        p.sendMessage(ChatColor.GREEN + "Działka została usunięta");
    }
    public static void noClaimsToRemove(Player p){
        p.sendMessage(ChatColor.RED + "Brak działek do usunięcia");
    }
    public static void noConfirmations(CommandSender s){
        s.sendMessage(ChatColor.RED + "Brak komendy do potwierdzenia lub potwierdzenie wygasło");
    }
    public static void aboutToRemove(CommandSender s){
        s.sendMessage(ChatColor.GOLD + "Za chwilę usuniesz działkę");
    }
    public static void teleported(CommandSender s){
        s.sendMessage(ChatColor.GREEN + "Przeniesiono na twoją działkę");
    }
    public static void claimExists(CommandSender s){
        s.sendMessage(ChatColor.GREEN + "Już posiadasz działkę");
    }
    public static void memberIsOwner(Player p){
        p.sendMessage(ChatColor.RED + "Jesteś właścicielem tej działki");
    }
    public static void memberIsOffline(Player p){
        p.sendMessage(ChatColor.RED + "Ze względu na ograniczenia silnika serwera nie można dodawać graczy, którzy nie są online");
    }
    public static void noClaim(Player p){
        p.sendMessage(ChatColor.RED + "Nie posiadasz działki");
    }
    public static void memberOnList(Player p){
        p.sendMessage(ChatColor.GOLD + "Ten gracz już znajduje się na liście członków działki");
    }
    public static void memberAdded(Player p){
        p.sendMessage(ChatColor.GREEN + "Gracz dodany do członków działki");
    }
    public static void memberNotOnList(Player p){
        p.sendMessage(ChatColor.RED + "Ten gracz nie znajduje się na liście członków działki");
    }
    public static void noMembers(Player p){
        p.sendMessage(ChatColor.GOLD + "Ta działka nie posiada żadnych członków");
    }
    public static void memberRemoved(Player p){
        p.sendMessage(ChatColor.GREEN + "Gracz usunięty z działki");
    }
    public static void youHaveBeenAdded(Player p, Player owner){
        p.sendMessage(ChatColor.GREEN + "Zostałeś dodany do działki gracza " + owner.getName());
    }
    public static void playerList(Player p){
        p.sendMessage(ChatColor.GREEN + "Członkowie twojej działki:");
    }
    public static void intersection(Player p){
        p.sendMessage(ChatColor.RED + "Nie można stworzyć działki na tym terenie!");
    }
    public static void configReloaded(CommandSender s){
        s.sendMessage(ChatColor.GREEN + "Konfiguracja przeładowana");
    }
    public static void noSuchPlayer(CommandSender s){
        s.sendMessage(ChatColor.RED + "Nie ma takiego gracza");
    }
    public static void noReqItems(Player p, List<ItemStack> missing){
        p.sendMessage(ChatColor.RED + "Brakuje ci:");
        for(ItemStack item : missing){
            p.sendMessage(ChatColor.GRAY + "" + item.getAmount() + "x " + item.getType().name().replace("_", ""));
        }
    }
}
