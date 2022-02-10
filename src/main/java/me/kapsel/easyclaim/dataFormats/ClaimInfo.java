package me.kapsel.easyclaim.dataFormats;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
//
// deserializacja?
//
@SerializableAs("ClaimInfo")
public class ClaimInfo implements ConfigurationSerializable {
    private String regionId;
    private int size;
    private String date;
    private TpInfo tpInfo;


    public ClaimInfo() {
    }

    public ClaimInfo(int size, String date, TpInfo teleportInfo, String RegionID) {
        this.size = size;
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

    public int getSize() {
        return size;
    }
    public String getDate() {
        return date;
    }
    public TpInfo getTpInfo() {
        return tpInfo;
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

    public void setSize(int size) {
        this.size = size;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTpInfo(TpInfo tpInfo) {
        this.tpInfo = tpInfo;
    }


    @NotNull
    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("size", this.getSize());
        result.put("date", this.getDate());
        result.put("tpInfo", this.getTpInfo());
        result.put("regionId", this.getRegionId());
        return result;
    }

    //deserialize()
    public ClaimInfo(Map<String, Object> input){
        this((int) input.get("size"), (String) input.get("date"), (TpInfo) input.get("tpInfo"), (String) input.get("regionId"));
    }
}
