package data.scripts.campaign.customstart;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.CharacterCreationData;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.impl.campaign.rulecmd.newgame.NGCAddStartingShipsByFleetType;
import com.fs.starfarer.api.impl.campaign.rulecmd.newgame.Nex_NGCFinalize;
import data.scripts.campaign.intel.SWP_IBBIntel.FamousBountyStage;
import data.scripts.campaign.intel.SWP_IBBTracker;
import exerelin.campaign.ExerelinSetupData;
import exerelin.campaign.PlayerFactionStore;
import exerelin.campaign.customstart.CustomStart;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class II_YamatoStart extends CustomStart {

    protected List<String> ships = new ArrayList<>(Arrays.asList(new String[]{
        "ii_boss_dominus_starter"
    }));

    @Override
    public void execute(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        ExerelinSetupData.getInstance().freeStart = true;
        PlayerFactionStore.setPlayerFactionIdNGC(Factions.PLAYER);

        CharacterCreationData data = (CharacterCreationData) memoryMap.get(MemKeys.LOCAL).get("$characterData");

        NGCAddStartingShipsByFleetType.generateFleetFromVariantIds(dialog, data, null, ships);
        Nex_NGCFinalize.addStartingDModScript(memoryMap.get(MemKeys.LOCAL));

        FireBest.fire(null, dialog, memoryMap, "ExerelinNGCStep4");

        data.addScript(() -> {
            Global.getSector().addScript(new EveryFrameScript() {

                private boolean done = false;

                @Override
                public boolean isDone() {
                    return done;
                }

                @Override
                public boolean runWhilePaused() {
                    return true;
                }

                @Override
                public void advance(float amount) {
                    CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
                    for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
                        if (member.getShipName().contentEquals("Yamato")) {
                            done = true;
                            break;
                        }

                        member.setShipName("Yamato");
                    }
                }

            });

            SWP_IBBTracker.getTracker().reportStageCompleted(FamousBountyStage.STAGE_YAMATO);
            Global.getSector().getMemoryWithoutUpdate().set("$ii_yamatostart", true);
            Global.getSettings().getHullSpec("ii_boss_dominus").getTags().remove(Tags.INVISIBLE_IN_CODEX);
            Global.getSettings().getWeaponSpec("ii_boss_wavemotion").getTags().remove(Tags.INVISIBLE_IN_CODEX);
            Global.getSettings().getShipSystemSpec("ii_boss_modifiedbooster").getTags().remove(Tags.INVISIBLE_IN_CODEX);
        });
    }
}
