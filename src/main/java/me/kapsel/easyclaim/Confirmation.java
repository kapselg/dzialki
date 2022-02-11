package me.kapsel.easyclaim;

import java.util.Date;

/**
 * Handles confirmation of error sensitive commands
 */
public class Confirmation {
    public Date date;
    public int exp = (int) Main.plugin.getConfig().get("ConfirmWaitTime");
    public String command;
    public Confirmation(String command){
        date = new Date();
        this.command = command;
    }
    public boolean expired(){
        return new Date().getTime() - date.getTime() > exp * 1000L;
    }
}
