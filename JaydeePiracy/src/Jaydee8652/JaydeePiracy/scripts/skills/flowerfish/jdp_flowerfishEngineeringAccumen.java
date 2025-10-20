package Jaydee8652.JaydeePiracy.scripts.skills.flowerfish;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.rpg.Person;
import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;
import org.magiclib.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class jdp_flowerfishEngineeringAccumen extends SCBaseSkillPlugin{

    @Override
    public String getAffectsString() {
        return "all ships with human officers";
    }

    @Override
    public void addTooltip(SCData scData, TooltipMakerAPI tooltipMakerAPI) {
        tooltipMakerAPI.addPara("If the shipsystem has charges: +1 charge", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
        tooltipMakerAPI.addPara("If the shipsystem regenerates charges: +40%% regeneration rate", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
        tooltipMakerAPI.addPara("If the shipsystem has a cooldown: -33%% cooldown", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
        tooltipMakerAPI.addSpacer(10f);

        tooltipMakerAPI.addPara("\"Here, I don't make mistakes. Here, I am absolute.\"", 0f, Misc.getTextColor(), Misc.getHighlightColor());
        tooltipMakerAPI.addPara("  -Maerula", 0f, jdp_AptitudeFlowerfish.JDP_FLOWERFISH_COLOUR, Misc.getHighlightColor());
        tooltipMakerAPI.addSpacer(10f);
    }

    @Override
    public void advance(SCData data, Float amount) {}

    @Override
    public void applyEffectsAfterShipCreation(SCData data, ShipAPI ship, ShipVariantAPI variant, String id) {
        PersonAPI captain = ship.getCaptain();
        if (captain == null || captain.isDefault()) return;
        if (ship.getCaptain().isAICore()) return;

        ship.getMutableStats().getSystemUsesBonus().modifyFlat(id, 1f);
        ship.getMutableStats().getSystemRegenBonus().modifyPercent(id, 40f);
        ship.getMutableStats().getSystemCooldownBonus().modifyMult(id, 0.666f);
    }

    @Override
    public void onActivation(SCData data) {}

    @Override
    public void onDeactivation(SCData data) {}
}
