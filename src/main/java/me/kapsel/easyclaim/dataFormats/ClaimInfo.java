package me.kapsel.easyclaim.dataFormats;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.Date;
import java.util.UUID;

public class ClaimInfo {
    private String regionId;
    private int Size;
    private String date;
    private TpInfo tpInfo;

    public ClaimInfo(int size, String date, TpInfo teleportInfo, String RegionID) {
        Size = size;
        this.date = date;
        this.tpInfo = teleportInfo;
        this.regionId = RegionID;
    }

    public ClaimInfo(int size, String date, TpInfo teleportInfo, ProtectedRegion region) {
        this(size, date, teleportInfo, region.getId());
    }
    public ClaimInfo(int size, TpInfo teleportInfo, ProtectedRegion regionID){
        this(size, new Date().toString(), teleportInfo, regionID);
    }
    public ClaimInfo(){

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
        return null;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
}
