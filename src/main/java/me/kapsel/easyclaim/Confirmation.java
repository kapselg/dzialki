package me.kapsel.easyclaim;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Date;


public class Confirmation {
    public Date date;
    public int exp = (int) EasyClaim.plugin.getConfig().get("ConfirmWaitTime");
    public String command;
    public Confirmation(String command){
        date = new Date();
        this.command = command;
    }
    public boolean expired(){
        if(new Date().getTime() - date.getTime() > exp * 1000L) {

            return true;
        }else{
            return false;
        }
    }
}
