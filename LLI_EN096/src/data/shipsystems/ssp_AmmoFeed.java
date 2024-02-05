package data.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class ssp_AmmoFeed extends BaseShipSystemScript {
	public static float RoF_Mult=4;
	protected float AmmoFeed_Point=0f;
	protected boolean RunOnce=false;
	protected float FC=0f;
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship=null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return; }
		if(ship.getVariant().hasHullMod("ssp_LongerRange")){FC=2f;}
		stats.getBallisticWeaponFluxCostMod().modifyMult(id, 0.5f);
        if(!RunOnce) {
        	for (WeaponSlotAPI slot : ship.getHullSpec().getAllWeaponSlotsCopy()) {
        		if (slot.getWeaponType() == WeaponAPI.WeaponType.BALLISTIC || slot.getWeaponType() == WeaponAPI.WeaponType.HYBRID || slot.getWeaponType() == WeaponAPI.WeaponType.UNIVERSAL) {
        			if (slot.getSlotSize() == WeaponAPI.WeaponSize.LARGE) {
        				AmmoFeed_Point += 5+FC;
        			}
        			if (slot.getSlotSize() == WeaponAPI.WeaponSize.MEDIUM) {
        				AmmoFeed_Point += 5+FC;
        			}
        			if (slot.getSlotSize() == WeaponAPI.WeaponSize.SMALL) {
        				AmmoFeed_Point += 5+FC;
        			}
        		}
        		RunOnce=true;
        	}
        }
		if (state == State.IN) {
			for(WeaponAPI weapon:ship.getAllWeapons()){
				if (weapon.getType() == WeaponAPI.WeaponType.BALLISTIC || weapon.getSpec().getMountType() == WeaponAPI.WeaponType.HYBRID) {
					weapon.setRemainingCooldownTo(0.1f);
					if (weapon.isDisabled()) { weapon.repair(); }
			}
			}
		}
		if (state == State.ACTIVE) {
			stats.getBallisticRoFMult().modifyMult(id, RoF_Mult);
			for(WeaponAPI weapon:ship.getAllWeapons()) {
				if (!weapon.isBeam() && weapon.getType() == WeaponAPI.WeaponType.BALLISTIC || weapon.getSpec().getMountType() == WeaponAPI.WeaponType.HYBRID) {
					if (AmmoFeed_Point>0f){
						if(!weapon.hasAIHint(WeaponAPI.AIHints.PD) && weapon.getCooldownRemaining()>0.1f){
							AmmoFeed_Point -= weapon.getCooldownRemaining()+weapon.getSpec().getChargeTime()*(1-1/RoF_Mult);
							weapon.setRemainingCooldownTo(0.1f);}
						if(!weapon.hasAIHint(WeaponAPI.AIHints.PD) && weapon.getCooldown()== 0f && weapon.getSpec().getChargeTime()>0f){
							if(weapon.getChargeLevel()==1){AmmoFeed_Point -= weapon.getCooldownRemaining()+weapon.getSpec().getChargeTime()*(1-1/RoF_Mult);}
						}
						if (weapon.hasAIHint(WeaponAPI.AIHints.PD)){
							weapon.setForceNoFireOneFrame(true);
						}
					}else if (AmmoFeed_Point<=0f){
						stats.getBallisticRoFMult().unmodify(id);
						if(!weapon.hasAIHint(WeaponAPI.AIHints.PD)){
							weapon.setForceNoFireOneFrame(true); }
					}
				}
			}
		}
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getBallisticRoFMult().unmodify(id);
		stats.getBallisticWeaponFluxCostMod().unmodify(id);
		AmmoFeed_Point=0f;
		RunOnce=false;
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		float AFP=AmmoFeed_Point;
		MathUtils.clamp(AFP,0,100000);
		if (index == 0) {
			if(AFP>0){
			return new StatusData(SSPI18nUtil.getShipSystemString("ssp_AmmoFeed_AFP") + Math.round(AFP*10f)/10f +"", false);
			} else {
			return new StatusData(SSPI18nUtil.getShipSystemString("ssp_AmmoFeed_HoldFire"), true);
			}
		}
		if (index == 1) {
			return new StatusData(SSPI18nUtil.getShipSystemString("ssp_AmmoFeed_FluxCost"), false);
		}
		return null;
	}
	@Override
	public float getActiveOverride(ShipAPI ship) {
		if (ship != null) {
			if (ship.getVariant().hasHullMod("ssp_ShortRange")){return 8f;}
			if (ship.getVariant().hasHullMod("ssp_LongerRange")){return 18f;}
		}
		return -1;
	}
}
