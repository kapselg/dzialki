package me.kapsel.easyclaim;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
//we will now objectify B)
public final class EasyClaim extends JavaPlugin{
    public static StateFlag EASY_CLAIM;
    public static StateFlag EASY_CLAIM_ALLOW;
    public Logger log;
    public static EasyClaim plugin;

    @Override
    public void onLoad() {
        //define flags for claims and claiming area
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        StateFlag claimFlag = new StateFlag("easy-claim", false);
        registry.register(claimFlag);
        EASY_CLAIM = claimFlag;
        StateFlag claimingAreaFlag = new StateFlag("easy-claim-allow", true);
        registry.register(claimingAreaFlag);
        EASY_CLAIM_ALLOW = claimingAreaFlag;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        log = Bukkit.getLogger();
        log.info("Hi there!");
        plugin = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
        ClaimData.setup();
        this.getCommand("eclaim").setExecutor(new ClaimCommand());
        this.getCommand("eclaim").setTabCompleter(new TabCompletion());
    }

    @Override
    public void onDisable() {
       // Plugin shutdown logic
        log.info("Bye!");
    }


}
