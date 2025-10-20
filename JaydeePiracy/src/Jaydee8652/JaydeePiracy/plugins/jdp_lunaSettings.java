package Jaydee8652.JaydeePiracy.plugins;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;

import java.awt.*;

public class jdp_lunaSettings {

    //Colony Items Global
    public static Boolean jdp_campaignColonyItemsGlobal() {
        Boolean jdp_campaignColonyItemsGlobal = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            jdp_campaignColonyItemsGlobal = LunaSettings.getBoolean("jaydeepiracy", "jdp_campaignColonyItemsGlobal");
        }
        return jdp_campaignColonyItemsGlobal;
    }

    //Cryoarithmetic Engine
    public static Boolean jdp_campaignCryo() {
        Boolean jdp_campaignCryo = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            jdp_campaignCryo = LunaSettings.getBoolean("jaydeepiracy", "jdp_campaignCryo");
        }
        return jdp_campaignCryo;
    }
    //Biofactory Embryo
    public static Boolean jdp_campaignBiofactory() {
        Boolean jdp_campaignBiofactory = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            jdp_campaignBiofactory = LunaSettings.getBoolean("jaydeepiracy", "jdp_campaignBiofactory");
        }
        return jdp_campaignBiofactory;
    }
    //Soil Nanites
    public static Boolean jdp_campaignSoil() {
        Boolean jdp_campaignSoil = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            jdp_campaignSoil = LunaSettings.getBoolean("jaydeepiracy", "jdp_campaignSoil");
        }
        return jdp_campaignSoil;
    }
    //Mantle Bore
    public static Boolean jdp_campaignBore() {
        Boolean jdp_campaignBore = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            jdp_campaignBore = LunaSettings.getBoolean("jaydeepiracy", "jdp_campaignBore");
        }
        return jdp_campaignBore;
    }
    //Orbital Fusion Lamp
    public static Boolean jdp_campaignLamp() {
        Boolean jdp_campaignLamp = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            jdp_campaignLamp = LunaSettings.getBoolean("jaydeepiracy", "jdp_campaignLamp");
        }
        return jdp_campaignLamp;
    }
    //Combat Drone Replicator
    public static Boolean jdp_campaignReplicator() {
        Boolean jdp_campaignReplicator = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            jdp_campaignReplicator = LunaSettings.getBoolean("jaydeepiracy", "jdp_campaignReplicator");
        }
        return jdp_campaignReplicator;
    }
    //Dealmaker Holosuite
    public static Boolean jdp_campaignHolosuite() {
        Boolean jdp_campaignHolosuite = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            jdp_campaignHolosuite = LunaSettings.getBoolean("jaydeepiracy", "jdp_campaignHolosuite");
        }
        return jdp_campaignHolosuite;
    }
    //Hypershunt Tap
    public static Boolean jdp_campaignTap() {
        Boolean jdp_campaignTap = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            jdp_campaignTap = LunaSettings.getBoolean("jaydeepiracy", "jdp_campaignTap");
        }
        return jdp_campaignTap;
    }
    //Plasma Dynamo
    public static Boolean jdp_campaignDynamo() {
        Boolean jdp_campaignDynamo = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            jdp_campaignDynamo = LunaSettings.getBoolean("jaydeepiracy", "jdp_campaignDynamo");
        }
        return jdp_campaignDynamo;
    }
    //Fullerene Spool
    public static Boolean jdp_campaignFullerene() {
        Boolean jdp_campaignFullerene = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            jdp_campaignFullerene = LunaSettings.getBoolean("jaydeepiracy", "jdp_campaignFullerene");
        }
        return jdp_campaignFullerene;
    }
    //Madoka Buffalo
    public static Boolean jdp_campaignMadoka() {
        Boolean jdp_campaignMadoka = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            jdp_campaignMadoka = LunaSettings.getBoolean("jaydeepiracy", "jdp_campaignMadoka");
        }
        return jdp_campaignMadoka;
    }
    //Subsurface Ecosystem
    public static Boolean jdp_campaignSubsurface() {
        Boolean jdp_campaignSubsurface = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            jdp_campaignSubsurface = LunaSettings.getBoolean("jaydeepiracy", "jdp_campaignSubsurface");
        }
        return jdp_campaignSubsurface;
    }
    //Flowerfish XO
    public static Boolean jdp_experimentalFlowerfish() {
        Boolean jdp_experimentalFlowerfish = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            jdp_experimentalFlowerfish = LunaSettings.getBoolean("jaydeepiracy", "jdp_experimentalFlowerfish");
        }
        return jdp_experimentalFlowerfish;
    }

}
