package Jaydee8652.JaydeePiracy.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import Jaydee8652.JaydeePiracy.campaign.world.*;
import com.fs.starfarer.api.impl.campaign.world.TTBlackSite;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
public class jdp_retrogen implements SectorGeneratorPlugin {
    public static Logger log = Global.getLogger(jdp_retrogen.class);

    //RetroGenerate
    @Override
    public void generate(SectorAPI sector) {

        //Generate Hiroc
        if (sector.getCustomEntitiesWithTag("jdp_derelictmissiletag").isEmpty()) {
            new jdp_Hiroc().generate(sector);
            log.info("JDP_RETROGEN: Hiroc System did not exist. Has been generated retroactively");
        }
        else
            log.info("JDP_RETROGEN: Hiroc System already exists. No action taken");

        //Generate Grin
        if (sector.getStarSystem("jdp_Grin") == null) {
            new jdp_Grin().generate(sector);
            log.info("JDP_RETROGEN: Grin System did not exist. Has been generated retroactively");
        }
        else
            log.info("JDP_RETROGEN: Grin System already exists. No action taken");

        //Generate Dory
        if (sector.getStarSystem("jdp_Dory") == null) {
            new jdp_Dory().generate(sector);
            log.info("JDP_RETROGEN: Dory System did not exist. Has been generated retroactively");
        }
        else
            log.info("JDP_RETROGEN: Dory System already exists. No action taken");
    }
}