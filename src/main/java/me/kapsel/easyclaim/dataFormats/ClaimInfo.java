package me.kapsel.easyclaim.dataFormats;

import com.sk89q.worldguard.domains.PlayerDomain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class ClaimInfo {
    private Player owner;
    private int Size;
    private String date;
    private TpInfo tpInfo;
    private UUID[] members;

    public ClaimInfo(int size, String date, TpInfo tpInfo, UUID[] members) {
        Size = size;
        this.date = date;
        this.members = members;
        this.tpInfo = tpInfo;
    }

    public ClaimInfo(int size, String date, Location tpInfo, UUID [] members){
        this(size, date, new TpInfo(tpInfo) , members);
    }

    public  ClaimInfo(int size, String date, Location tpInfo, Set<UUID> members){
        this(size, date, tpInfo, members.toArray(new UUID[0]));
    }
    public ClaimInfo(){

    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int size) {
        Size = size;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public TpInfo getTpInfo() {
        return tpInfo;
    }

    public void setTpInfo(TpInfo tpInfo) {
        this.tpInfo = tpInfo;
    }

    public UUID[] getMembers() {
        return members;
    }

    public PlayerDomain getMemberDomain() {
        PlayerDomain membersDomain = new PlayerDomain();
        for(UUID member : members){
            Player newMember = Bukkit.getPlayer(member);
            if(newMember !=null){
                membersDomain.addPlayer(newMember.getUniqueId());
            }
        }
        return membersDomain;
    }

    public void setMembers(UUID[] members) {
        this.members = members;
    }
}
