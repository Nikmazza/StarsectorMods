package data.scripts.orr;

import com.fs.starfarer.api.BaseModPlugin;

import lunalib.lunaRefit.LunaRefitManager;
import lunalib.lunaSettings.LunaSettings;
import data.scripts.orr.luna.InstallOnslaughtButton;
import data.scripts.orr.luna.SalvageOnslaughtButton;

public class OrrModPlugin extends BaseModPlugin {
			
	@Override
	public void onApplicationLoad() {
		LunaRefitManager.addRefitButton(new InstallOnslaughtButton());
		LunaRefitManager.addRefitButton(new SalvageOnslaughtButton());
	}
	
	@Override
	public void onNewGameAfterTimePass() {
		//CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
		//CharacterDataAPI player = Global.getSector().getCharacterData();
		//cargo.addWeapons("orr_tpc_deco", 1);
	}
}