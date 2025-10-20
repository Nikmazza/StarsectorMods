package data.hullmods;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.HullModEffect;
import com.fs.starfarer.api.combat.HullModFleetEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GMDA_RepairGantry_Med implements HullModEffect, HullModFleetEffect {

    public static final String HULLMOD_ID = "GMDA_RepairGantry";
    public static final String STAT_MOD_ID = "GMDA_RepairGantry_repair_bonus";
	
    public static final float REPAIR_RATE_BONUS_SML = 5f;
	public static final float REPAIR_RATE_BONUS_MED = 10f;
	public static final float REPAIR_RATE_BONUS_LRG = 25f;

				
    public static float MIN_CR = 0.1f;

    public GMDA_RepairGantry_Med() {
    }

    @Override
    public void advanceInCampaign(FleetMemberAPI member, float amount) {
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
    }

    @Override
    public boolean affectsOPCosts() {
        return false;
    }

    @Override
    public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
        return true;
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();

        tooltip.addPara("Out of combat repair rate of all ships is increased by %s/%s/%s/%s." +
                        "Having multiple GMHS vessels in the fleet provides diminishing returns.",
                opad, h,
                "" + (int) (REPAIR_RATE_BONUS_SML)+ "%",
                "" + (int) (REPAIR_RATE_BONUS_MED)+ "%",
                "" + (int) (REPAIR_RATE_BONUS_LRG)+ "%",
                "" + (int) (REPAIR_RATE_BONUS_LRG)+ "%");
    }

    @Override
    public Color getBorderColor() {
        return null;
    }

    @Override
    public Color getNameColor() {
        return null;
    }

    @Override
    public int getDisplaySortOrder() {
        return 0;
    }

    @Override
    public int getDisplayCategoryIndex() {
        return 0;
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
    }

    @Override
    public void init(HullModSpecAPI spec) {

    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
//        if (index == 0) {return "" + (int) (REPAIR_RATE_BONUS_SML * 100f) + "%";}
//        if (index == 1) {return "" + (int) (REPAIR_RATE_BONUS_MED * 100f) + "%";}
//        if (index == 2) {return "" + (int) (REPAIR_RATE_BONUS_LRG * 100f) + "%";}
//        if (index == 3) {return "" + (int) (REPAIR_RATE_BONUS_LRG * 100f) + "%";}
        return null;
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        return null;
    }

    @Override
    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {

    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return false;
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        return null;
    }

    @Override
    public boolean canBeAddedOrRemovedNow(ShipAPI ship, MarketAPI marketOrNull, CampaignUIAPI.CoreUITradeMode mode) {
        return false;
    }

    @Override
    public String getCanNotBeInstalledNowReason(ShipAPI ship, MarketAPI marketOrNull, CampaignUIAPI.CoreUITradeMode mode) {
        return null;
    }

    @Override
    public void advanceInCampaign(CampaignFleetAPI fleet) {

    }

    @Override
    public boolean withAdvanceInCampaign() {
        return false;
    }

    @Override
    public boolean withOnFleetSync() {
        return false;
    }

    @Override
    public void onFleetSync(CampaignFleetAPI fleet) {

        List<FleetMemberAPI> rigs = new ArrayList<>();
        List<FleetMemberAPI> members = fleet.getFleetData().getMembersListCopy();
        for (FleetMemberAPI member : members) {
            if (member.isMothballed()) {
                continue;
            }
            if (member.getRepairTracker().getCR() < MIN_CR) {
                continue;
            }
            for (String modId : member.getVariant().getHullMods()) {
                if (HULLMOD_ID.equals(modId)) {
                    rigs.add(member);
                }
            }
        }

        int index = 0;
        for (FleetMemberAPI rig : rigs) {
            for (int i = index; i < members.size(); i++) {
                FleetMemberAPI member = members.get(i);
                if (i <= 0) {
                    member.getStats().getRepairRatePercentPerDay().unmodify(STAT_MOD_ID);
                }
                if (!member.canBeRepaired()) {
                    continue;
                }
                if (!member.needsRepairs()) {
                    continue;
                }
                member.getStats().getRepairRatePercentPerDay().modifyPercent(STAT_MOD_ID, REPAIR_RATE_BONUS_MED);
                index = i + 1;
                break;
            }
        }
    }
    public boolean hasSModEffectSection(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
        return false;
    }

    public void addSModSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width,
                               boolean isForModSpec) {

    }

    public void addSModEffectSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width,
                                     boolean isForModSpec, boolean isForBuildInList) {
        // TODO Auto-generated method stub

    }

    public void addSModSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width,
                               boolean isForModSpec, boolean isForBuildInList) {
        // TODO Auto-generated method stub

    }

    public boolean hasSModEffect() {
        // TODO Auto-generated method stub
        return false;
    }

    public String getSModDescriptionParam(int index, HullSize hullSize) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSModDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        // TODO Auto-generated method stub
        return null;
    }

    public float getTooltipWidth() {
        return 0;
    }

    public boolean isSModEffectAPenalty() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean showInRefitScreenModPickerFor(ShipAPI ship) {
        return true;
    }
}
