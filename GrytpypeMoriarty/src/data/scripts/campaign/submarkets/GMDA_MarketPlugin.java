package data.scripts.campaign.submarkets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.FactionAPI.ShipPickMode;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.impl.campaign.submarkets.BlackMarketPlugin;
import com.fs.starfarer.api.util.Highlights;
import com.fs.starfarer.api.util.Misc;

public class GMDA_MarketPlugin extends BlackMarketPlugin {

    private final RepLevel MIN_STANDING = RepLevel.VENGEFUL;

    private boolean playerPaidToUnlock = false;
    private float sinceLastUnlock = 0f;

    @Override
    public void advance(float amount) {
        super.advance(amount);
        float days = Global.getSector().getClock().convertToDays(amount);
        sinceLastUnlock += days;
        if (sinceLastUnlock > 7f) {
            playerPaidToUnlock = false;
        }
    }

    @Override
    public void init(SubmarketAPI submarket) {
        super.init(submarket);
    }

    @Override
    public DialogOption[] getDialogOptions(CoreUIAPI ui) {
        if (canPlayerAffordUnlock()) {
            return new DialogOption[]{
                    new DialogOption("Pay", new Script() {
                        @Override
                        public void run() {
                            CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
                            playerFleet.getCargo().getCredits().subtract(getUnlockCost());
                            playerPaidToUnlock = true;
                            sinceLastUnlock = 0f;
                        }
                    }),
                    new DialogOption("Never mind", null)
            };
        } else {
            return new DialogOption[]{
                    new DialogOption("Never mind", null)
            };
        }
    }

    @Override
    public String getDialogText(CoreUIAPI ui) {
        if (canPlayerAffordUnlock()) {
            return "\"If you're not interested in our amazing quaint and lovingly crafted souvenirs, perhaps you'd be interested in our...deluxe showroom. For a small "
                    + Misc.getWithDGS(getUnlockCost()) + "-credit consideration, of course.\"";
        } else {
            return "\"Unfortunately I don't know about this 'showroom' of which you speak. However, if you had " + Misc.getWithDGS(getUnlockCost()) + "-credits...then perhaps an arrangement could be made.\"";
        }
    }

    @Override
    public Highlights getDialogTextHighlights(CoreUIAPI ui) {
        Highlights h = new Highlights();
        h.setText("" + getUnlockCost());
        if (canPlayerAffordUnlock()) {
            h.setColors(Misc.getHighlightColor());
        } else {
            h.setColors(Misc.getNegativeHighlightColor());
        }
        return h;
    }

    @Override
    public OnClickAction getOnClickAction(CoreUIAPI ui) {
        if (playerPaidToUnlock || submarket.getFaction().getRelToPlayer().isAtWorst(RepLevel.FRIENDLY)) {
            return OnClickAction.OPEN_SUBMARKET;
        }
        return OnClickAction.SHOW_TEXT_DIALOG;
    }

    @Override

    public float getTariff() {
        float fudge;
        switch (submarket.getFaction().getRelToPlayer().getLevel()) {
            default:
            case VENGEFUL:
            case HOSTILE:
            case INHOSPITABLE:
                fudge = 2f;
                break;
            case SUSPICIOUS:
                fudge = 1.5f;
                break;
            case NEUTRAL:
                fudge = 1f;
                break;
            case FAVORABLE:
                fudge = 0.75f;
                break;
            case WELCOMING:
                fudge = 0.5f;
                break;
            case FRIENDLY:
            case COOPERATIVE:
                fudge = 0f;
                break;
        }

        MonthlyReport report = SharedData.getData().getCurrentReport();
        report.computeTotals();
        float profit = Math.max(0f, report.getRoot().totalIncome - report.getRoot().totalUpkeep);
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();

        return Math.round((float) Math.sqrt(Math.max(playerFleet.getCargo().getCredits().get() + profit, 100000f) / 100000f) * 10f * fudge) / 100f;
    }

    @Override
    public String getTooltipAppendix(CoreUIAPI ui) {
        RepLevel level = submarket.getFaction().getRelationshipLevel(Global.getSector().getFaction(Factions.PLAYER));
        if (!level.isAtWorst(MIN_STANDING)) {
            return "Requires: " + submarket.getFaction().getDisplayName() + " - "
                    + MIN_STANDING.getDisplayName().toLowerCase();
        }
        return super.getTooltipAppendix(ui);
    }

    @Override
    public boolean isEnabled(CoreUIAPI ui) {
        if (Global.getSector().getPlayerFleet().isTransponderOn()) {
            return true;
        }

        RepLevel level = submarket.getFaction().getRelationshipLevel(Global.getSector().getFaction(Factions.PLAYER));
        return level.isAtWorst(MIN_STANDING);
    }

    @Override
    public void updateCargoPrePlayerInteraction() {
        sinceLastCargoUpdate = 0f;

        if (okToUpdateShipsAndWeapons()) {
            sinceSWUpdate = 0f;

            pruneWeapons(0f);
            addWeapons(16, 20, 4, submarket.getFaction().getId());
            addFighters(8, 10, 3, submarket.getFaction().getId());

            getCargo().getMothballedShips().clear();

            FactionDoctrineAPI doctrineOverride = submarket.getFaction().getDoctrine().clone();
            doctrineOverride.setCombatFreighterProbability(1f);
            doctrineOverride.setShipSize(4);
            addShips(submarket.getFaction().getId(),
                    300f, // combat
                    0f, // freighter
                    0f, // tanker
                    0f, // transport
                    0f, // liner
                    0f, // utilityPts
                    1.5f, // qualityOverride
                    0f, // qualityMod
                    ShipPickMode.PRIORITY_THEN_ALL,
                    doctrineOverride);

            addHullMods(4, 4);
        }

        getCargo().sort();
    }


    @Override
    public boolean isIllegalOnSubmarket(CargoStackAPI stack, TransferAction action) {
        if (!playerPaidToUnlock) {
            return true;
        }
        return super.isIllegalOnSubmarket(stack, action);
    }

    @Override
    public boolean isIllegalOnSubmarket(FleetMemberAPI member, TransferAction action) {
        if (action == TransferAction.PLAYER_BUY) {
            MonthlyReport report = SharedData.getData().getCurrentReport();
            report.computeTotals();
            float profit = Math.max(0f, report.getRoot().totalIncome - report.getRoot().totalUpkeep);
            CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
            float assets = playerFleet.getCargo().getCredits().get() + profit;

            if (member.isFrigate()) {
                return assets < 50000f;
            } else if (member.isDestroyer()) {
                return assets < 100000f;
            } else if (member.isCruiser()) {
                return assets < 200000f;
            } else if (member.isCapital()) {
                return assets < 400000f;
            }
        }

        return false;
    }

    @Override
    public String getIllegalTransferText(CargoStackAPI stack, TransferAction action) {
        if (!playerPaidToUnlock) {
            return "Requires: paid access";
        }
        return super.getIllegalTransferText(stack, action);
    }

    @Override
    public String getIllegalTransferText(FleetMemberAPI member, TransferAction action) {
        float req = 0f;
        if (member.isFrigate()) {
            req = 50000f;
        } else if (member.isDestroyer()) {
            req = 100000f;
        } else if (member.isCruiser()) {
            req = 200000f;
        } else if (member.isCapital()) {
            req = 400000f;
        }
        return "Req: " + Misc.getDGSCredits(req) + " assets/income";
    }

    @Override
    protected Object writeReplace() {
        if (okToUpdateShipsAndWeapons()) {
            pruneWeapons(0f);
            getCargo().getMothballedShips().clear();
        }
        return this;
    }

    private boolean canPlayerAffordUnlock() {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        int credits = (int) playerFleet.getCargo().getCredits().get();
        return credits >= getUnlockCost();
    }

    private int getUnlockCost() {
        float fudge;
        switch (submarket.getFaction().getRelToPlayer().getLevel()) {
            default:
            case VENGEFUL:
            case HOSTILE:
            case INHOSPITABLE:
                fudge = 2f;
                break;
            case SUSPICIOUS:
                fudge = 1.5f;
                break;
            case NEUTRAL:
                fudge = 1f;
                break;
            case FAVORABLE:
                fudge = 0.75f;
                break;
            case WELCOMING:
                fudge = 0.5f;
                break;
            case FRIENDLY:
            case COOPERATIVE:
                fudge = 0f;
                break;
        }

        MonthlyReport report = SharedData.getData().getCurrentReport();
        report.computeTotals();
        float profit = Math.max(0f, report.getRoot().totalIncome - report.getRoot().totalUpkeep);
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();

        return Math.round((float) Math.sqrt(Math.max(playerFleet.getCargo().getCredits().get() + profit, 50000f) / 100000f) * 30f * fudge) * 1000;
    }
}