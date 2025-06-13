package Jaydee8652.JaydeePiracy.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
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
public class jdp_gen implements SectorGeneratorPlugin {
    public static Logger log = Global.getLogger(jdp_gen.class);

    //Generate Systems
    @Override
    public void generate(SectorAPI sector) {
        //Generate Hiroc
        new jdp_Hiroc().generate(sector);
        //Generate Grin
        new jdp_Grin().generate(sector);
        //Generate Dory
        new jdp_Dory().generate(sector);
    }

    //Add PMMM Buffalo Meme
    public static void addMadokaBuffalo(SectorAPI sector){
        Iterator<StarSystemAPI> stariter = sector.getStarSystems().iterator();
        ArrayList<StarSystemAPI> validstars = new ArrayList<StarSystemAPI>();
        while (stariter.hasNext()){
            StarSystemAPI star = stariter.next();
            if (star.isProcgen()){
                validstars.add(star);
            }
        }
        Collections.shuffle(validstars);
        StarSystemAPI targetstar = validstars.get(0);
        SectorEntityToken entity = targetstar.getPlanets().get(0);
        TTBlackSite.addDerelict(targetstar, entity, "jdp_buffalo_pmmm_Standard", "Wehihihi", "jdp_buffalo_pmmm", ShipRecoverySpecial.ShipCondition.BATTERED, entity.getRadius() +300, true);
        log.info("The Madoka Buffalo is in the " + targetstar.getName());
    }
}

