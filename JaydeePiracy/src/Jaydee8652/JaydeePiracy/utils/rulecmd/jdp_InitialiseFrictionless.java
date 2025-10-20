package Jaydee8652.JaydeePiracy.utils.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc.Token;

public class jdp_InitialiseFrictionless extends BaseCommandPlugin {
	public void updateFrictionless() {
		Global.getSettings().getSpecialItemSpec("jdp_frictionlessmetal").setName(
				"Frictionless NMC-" + getYear());
		Global.getSettings().getSpecialItemSpec("jdp_frictionlessmetal").setDesc(
				"An anomalous crystalline material acquired in extremely limited quantities during fringe mining operations on Maddie. Designation Frictionless NMC-" + getYear() + ", the substance was formed in the radiation of a red pulsar and is iridescent like the shell of an arthropod-analog. \n\n" +

				"Considered a chronohazard, it is capable of demonstrating novel temporal properties, inverting the effect of relativity within its sphere of influence with an intensity proportional to mass.");
	}

	public static String getYear() {
		Integer inventionCycle = (Integer) Global.getSector().getMemoryWithoutUpdate().get("$jdp_nmcInventionCycle");
		if (inventionCycle != null) {
			return inventionCycle.toString();
		} else {
			return "000";
		}
	}

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		Global.getSector().getMemoryWithoutUpdate().set("$jdp_nmcInventionCycle", Global.getSector().getClock().getCycle());
		updateFrictionless();
		return true;
	}
}








