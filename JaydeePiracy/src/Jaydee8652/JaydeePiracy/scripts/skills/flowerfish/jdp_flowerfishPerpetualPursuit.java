package Jaydee8652.JaydeePiracy.scripts.skills.flowerfish;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;

import java.util.List;

public class jdp_flowerfishPerpetualPursuit extends SCBaseSkillPlugin {
    @Override
    public String getAffectsString() {
        return "fleet";
    }

    @Override
    public void addTooltip(SCData data, TooltipMakerAPI tooltip) {
        tooltip.addPara("Ships deployed by the flanks gain 40 bonus speed until they are within range of an enemy.", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
        tooltip.addPara("If two or more combat objectives are captured, ships of all classes can be deployed from the flanks.", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
        tooltip.addPara("*Applies to all combat scenarios. Requirement is met by default in engagements without combat objectives", 0f, Misc.getGrayColor(), Misc.getHighlightColor());
        tooltip.addSpacer(10f);

        tooltip.addPara("\"It is so quiet, right before a battle is set into motion. A lot can be accomplished in that interstitial space.\"", 0f, Misc.getTextColor(), Misc.getHighlightColor());
        tooltip.addPara("  -Flowerfish First Wasabi", 0f, jdp_AptitudeFlowerfish.JDP_FLOWERFISH_COLOUR, Misc.getHighlightColor());
        tooltip.addSpacer(10f);
    }

    @Override
    public void applyEffectsAfterShipCreation(SCData data, ShipAPI ship, ShipVariantAPI variant, String id) {
        if ((ship.getLocation().getX() >= 9000) || (ship.getLocation().getX() <= -9000)) {
            ship.getMutableStats().getMaxSpeed().modifyFlat("jdp_flowerfishPerpetualPursuit", 40f);
            ship.getMutableStats().getAcceleration().modifyFlat("jdp_flowerfishPerpetualPursuit", 20f);
            ship.getMutableStats().getDeceleration().modifyFlat("jdp_flowerfishPerpetualPursuit", 20f);
        }
    }

    @Override
    public void advance(SCData data, Float amunt) {
        data.getCommander().getStats().getDynamic().getMod(Stats.CAN_DEPLOY_LEFT_RIGHT_MOD).unmodify("jdp_flowerfishPerpetualPursuit");
    }

    @Override
    public void advanceInCombat(SCData data, ShipAPI ship, Float amount) {
        if (ship.areAnyEnemiesInRange()) {
            ship.getMutableStats().getMaxSpeed().unmodify("jdp_flowerfishPerpetualPursuit");
            ship.getMutableStats().getAcceleration().unmodify("jdp_flowerfishPerpetualPursuit");
            ship.getMutableStats().getDeceleration().unmodify("jdp_flowerfishPerpetualPursuit");
        }
    }

    @Override
    public void onActivation(SCData data) {
        data.getCommander().addTag("jdp_flowerfishPerpetualPursuit");
    }
    @Override
    public void onDeactivation(SCData data) {
        data.getCommander().removeTag("jdp_flowerfishPerpetualPursuit");
    }
}