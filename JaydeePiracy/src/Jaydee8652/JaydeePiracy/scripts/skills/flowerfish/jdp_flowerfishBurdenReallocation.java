package Jaydee8652.JaydeePiracy.scripts.skills.flowerfish;


import Jaydee8652.JaydeePiracy.scripts.systems.jdp_ModularJitterStats;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.fleet.FleetMember;
import com.fs.starfarer.rpg.Person;
import org.lazywizard.lazylib.MathUtils;
import org.magiclib.subsystems.MagicSubsystem;
import org.magiclib.subsystems.MagicSubsystemsManager;
import org.magiclib.util.MagicUI;
import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;
import org.magiclib.*;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.subsystems.MagicSubsystem;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class jdp_flowerfishBurdenReallocation extends SCBaseSkillPlugin{
    private static String jdp_skimmerIcon = "graphics/icons/hullsys/displacer.png";


    @Override
    public String getAffectsString() {
        return "all ships with human officers";
    }

    @Override
    public void addTooltip(SCData data, TooltipMakerAPI tooltip) {
        float pad = 3f;
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();
        Color good = Misc.getPositiveHighlightColor();
        float HEIGHT = 50f;
        float PAD = 10f;


        tooltip.addPara("All ships with human officers in your fleet receive the \"Maerulan Phase Skimmer\" subsystem", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
        tooltip.addSpacer(10f);

        TooltipMakerAPI relay = tooltip.beginImageWithText(jdp_skimmerIcon, HEIGHT);
        relay.addPara("Maerulan Phase Skimmer", 0f, h, "Maerulan Phase Skimmer");
        relay.addPara("Teleports the ship a short distance in the direction it's traveling and centers facing on its target.", 0f, h);
        relay.addPara(" *Has one charge by default, but is effected by bonuses that increase system charges", 0f, Misc.getGrayColor(), h);
        relay.addPara(" Maximum range scales inversely with ship mass (including modules) and is capped at 500", 0f, Misc.getGrayColor(), h);
        relay.addPara(" Recharge rate also scales inversely with ship mass (including modules) but has no cap", 0f, Misc.getGrayColor(), h);
        tooltip.addImageWithText(PAD);
        tooltip.addSpacer(10f);

        tooltip.addPara("\"Its- Its horrible! Whoever invented this, they're a monster.\"", 0f, Misc.getTextColor(), Misc.getHighlightColor());
        tooltip.addPara("  -Proving Grounds Transcript, Undisclosed Location", 0f, jdp_AptitudeFlowerfish.JDP_FLOWERFISH_COLOUR, Misc.getHighlightColor());
        tooltip.addSpacer(10f);
    }

    @Override
    public void applyEffectsToFighterSpawnedByShip(SCData data, ShipAPI fighter, ShipAPI ship, String id) {
        // No implementation needed
    }

    @Override
    public void applyEffectsBeforeShipCreation(SCData data, MutableShipStatsAPI stats, ShipVariantAPI variant, ShipAPI.HullSize hullSize, String id) {
        // No implementation needed
    }

    @Override
    public void applyEffectsAfterShipCreation(SCData data, ShipAPI ship, ShipVariantAPI variant, String id) {
        //Has Captain
        PersonAPI captain = ship.getCaptain();
        if (captain == null || captain.isDefault()) return;
    }

    @Override
    public void advance(SCData data, Float amount) {
        // No implementation needed
    }

    @Override
    public void onActivation(SCData data) {
        // No implementation needed
    }

    @Override
    public void onDeactivation(SCData data) {
        // No implementation needed
    }
}
