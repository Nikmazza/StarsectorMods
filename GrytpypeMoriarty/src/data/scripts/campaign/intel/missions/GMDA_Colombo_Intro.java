package data.campaign.intel.missions;

import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Voices;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.util.Misc;
import java.util.List;

public class GMDA_Colombo_Intro extends HubMissionWithBarEvent {
    @Override
    public boolean shouldShowAtMarket(MarketAPI market) {
        return market.getId().equals("agreus");
    }

	@Override
	protected boolean create(MarketAPI createdAt, boolean barEvent) {
            if (barEvent) {
                setGiverPortrait(Global.getSettings().getSpriteName("characters", "gmda_columbo"));
                setGiverFaction("gmda");
                setGiverPost(Ranks.POST_CITIZEN);
                setGiverVoice(Voices.VILLAIN);
                setGiverImportance(pickHighImportance());
                findOrCreateGiver(createdAt, false, false);
            }
            PersonAPI person = getPerson();
            if (person == null) return false;
            MarketAPI market = person.getMarket();
            if (market == null) return false;
            /*if (barEvent) {
                setGiverIsPotentialContactOnSuccess();
            } "there is no success here" so we use rule.csv AddPotentialContact instead/*
            /*if (!setPersonMissionRef(person, "$gmda_hireme_ref")) {
		return false;
            } idk if this is even.. used?*/
            setRepPersonChangesNone();
            setRepFactionChangesNone();
            return true;
        }
        
        protected void updateInteractionDataImpl() {
          set("$gmda_hireme_ref", this);
          set("$gmda_hireme_barEvent", isBarEvent());
          set("$gmda_hireme_manOrWoman", getPerson().getManOrWoman()); //im pretty sure this is important but w/e
          set("$gmda_hireme_hisOrHer", getPerson().getHisOrHer());
        }

	@Override
	public String getBaseName() {
		return "Screenshot to Alfonzo if seen"; // not used I don't think
	}
        
        protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
          /*if ("showPerson".equals(action)) {
            dialog.getVisualPanel().showPersonInfo(getPerson(), true);
            return true;
          }*/
          return false;
        }
	
	@Override
	public void accept(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
		currentStage = new Object(); // so that the abort() assumes the mission was successful
		abort();
	}
}





