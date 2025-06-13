package data.skills;

import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;

import java.util.HashMap;
import java.util.Map;

public class Strengthofthepackisthewolf extends SCBaseSkillPlugin {
    public static float FLUX_COST_MULT = .95f;
    private static Map mag = new HashMap();
    static {
        mag.put(ShipAPI.HullSize.FRIGATE, 2f);
        mag.put(ShipAPI.HullSize.DESTROYER, 3f);
    }
public static boolean isMercenary (PersonAPI person) {
        return person != null && person.getMemoryWithoutUpdate().is("$isMercenary", true);
}

    public static boolean isOfficer(MutableShipStatsAPI stats) {
        if (stats.getEntity() instanceof ShipAPI) {
            ShipAPI ship = (ShipAPI) stats.getEntity();
            return !ship.getCaptain().isDefault();
        } else {
            FleetMemberAPI member = stats.getFleetMember();
            if (member == null) return false;
            return !member.getCaptain().isDefault();
        }
    }
    @Override
    public String getAffectsString() {
        return "all frigates and destroyers";
    }

    @Override
    public void addTooltip(SCData data, TooltipMakerAPI tooltip) {
        tooltip.addPara("Increased peak operating time for frigates and destroyers by 30%%", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
        tooltip.addPara("+25%% ammunition capacity for non-missile weapons that use ammunition", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
        tooltip.addPara("Decreased energy and ballistic weapons flux cost by 5%%", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
        tooltip.addPara("Increases Nav rating in combat by 2%%/3%% on hullsize", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
    }

    @Override
    public void applyEffectsBeforeShipCreation(SCData data, MutableShipStatsAPI stats, ShipVariantAPI variant, ShipAPI.HullSize hullSize, String id) {
        if (hullSize == ShipAPI.HullSize.DESTROYER || hullSize == ShipAPI.HullSize.FRIGATE) {
            stats.getPeakCRDuration().modifyPercent(id, 30f);
            stats.getEnergyWeaponFluxCostMod().modifyMult(id, FLUX_COST_MULT);
            stats.getBallisticWeaponFluxCostMod().modifyMult(id, FLUX_COST_MULT);
            stats.getBallisticAmmoBonus().modifyPercent(id, 25f);
            stats.getEnergyAmmoBonus().modifyPercent(id, 25f);
            stats.getDynamic().getMod(Stats.COORDINATED_MANEUVERS_FLAT).modifyFlat(id, (Float) mag.get(hullSize));
        }
    }
}