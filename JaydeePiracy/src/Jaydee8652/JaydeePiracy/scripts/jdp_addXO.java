package Jaydee8652.JaydeePiracy.scripts;

import Jaydee8652.JaydeePiracy.utils.jdp_Factions;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;

import java.util.HashMap;
import java.util.Random;

import com.fs.starfarer.api.characters.PersonAPI;

public class jdp_addXO implements EconomyTickListener {
    String prev;

    @Override
    public void reportEconomyTick(int iterIndex) {

    }

    @Override
    public void reportEconomyMonthEnd() {
        MarketAPI market = Global.getSector().getEconomy().getMarket("ailmar");
        if(market == null) return; // If the market doesn't exist, just quit

        if(prev != null)
            market.getCommDirectory().removeEntry(prev); // Clear last month's XO
        prev = null;

        // Make a random Flowerfish
        PersonAPI person = market.getFaction().createRandomPerson(); // Random gender
        person.setFaction(jdp_Factions.JDP_FLOWERFISH);
        person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "jdp_wasabi"));
        person.getMemoryWithoutUpdate().set("$sc_officer_aptitude","jdp_flowerfish");
        person.getMemoryWithoutUpdate().set("$sc_hireable", true);
        person.setPostId("executive_officer_jdp_flowerfish");

        prev = market.getCommDirectory().addPerson(person);
    }
}
