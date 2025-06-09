package data.hullmods.bi;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.AIHints;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.TimeoutTracker;
import data.scripts.everyframe.yna_BlockedHullmodDisplayScript;
import data.scripts.util.YNA_MD;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class yna_missile_loader extends BaseHullMod {

	public static class ReloadCapacityData {
		public HullSize size;
		public int minW, maxW;
		public int capacity;
		public ReloadCapacityData(HullSize size, int minW, int maxW, int capacity) {
			this.size = size;
			this.minW = minW;
			this.maxW = maxW;
			this.capacity = capacity;
		}
		
		public String getSizeStr() {
			return Misc.getHullSizeStr(size);
		}
		
		public String getWeaponsString() {
			if (maxW < 0) return "" + minW + "+";
			if (minW != maxW) return "" + minW + "-" + maxW;
			return "" + minW;
		}
	}
	
	private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
	static
	{
		// These hullmods will automatically be removed
		// This prevents unexplained hullmod blocking
		BLOCKED_HULLMODS.add("missile_autoloader");
	}
    
	public static List<ReloadCapacityData> SMALSILO = new ArrayList<>();
	static {
		SMALSILO.add(new ReloadCapacityData(HullSize.FRIGATE, 1, 1, 12));
		SMALSILO.add(new ReloadCapacityData(HullSize.FRIGATE, 2, -1, 8));
		
		SMALSILO.add(new ReloadCapacityData(HullSize.DESTROYER, 1, 1, 18));
		SMALSILO.add(new ReloadCapacityData(HullSize.DESTROYER, 2, -1, 12));
		
		SMALSILO.add(new ReloadCapacityData(HullSize.CRUISER, 1, 2, 24));
		SMALSILO.add(new ReloadCapacityData(HullSize.CRUISER, 3, 4, 16));
		SMALSILO.add(new ReloadCapacityData(HullSize.CRUISER, 5, -1, 12));
		
		SMALSILO.add(new ReloadCapacityData(HullSize.CAPITAL_SHIP, 1, 4, 30));
//		SMALSILO.add(new ReloadCapacityData(HullSize.CAPITAL_SHIP, 5, 8, 24));
		SMALSILO.add(new ReloadCapacityData(HullSize.CAPITAL_SHIP, 5, -1, 40));
	}
	
	public static float LOAD_CD = 5f;
	
	public static String MA_DATA_KEY = "core_ynadar_smalsilo_data_key";
	
	public static class MissileAutoloaderData {
		public IntervalUtil interval = new IntervalUtil(0.2f, 0.4f);
		public float opLeft = 0f;
		public float showExhaustedStatus = 5f;
		public TimeoutTracker<WeaponAPI> cooldown = new TimeoutTracker<WeaponAPI>();
	}
	
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
	}
	
        @Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
            for (String tmp : BLOCKED_HULLMODS) {
                if (ship.getVariant().getHullMods().contains(tmp)) {
                    ship.getVariant().removeMod(tmp);
                    yna_BlockedHullmodDisplayScript.showBlocked(ship);
                }
            }
            
		List weapons = ship.getAllWeapons();
		Iterator iter = weapons.iterator();
		while (iter.hasNext()) {
			WeaponAPI weapon = (WeaponAPI)iter.next();
			boolean smol = weapon.getSize() == WeaponSize.SMALL;
			boolean misil = weapon.getType() == WeaponType.MISSILE;
			int mamo = weapon.getSpec().getMaxAmmo();
				
                    if (weapon.getSlot().getWeaponType() == WeaponAPI.WeaponType.MISSILE){
			if (weapon.getId().equals("heatseeker")) {
                            weapon.getSpec().getAIHints().add(AIHints.DO_NOT_CONSERVE); 
                            weapon.getSpec().getAIHints().add(AIHints.FIRE_WHEN_INEFFICIENT); 
			} else if (smol && misil && mamo > 60) {
                            weapon.setMaxAmmo((int) (mamo * 2.5f));
                            weapon.setAmmo((int) (mamo * 2.5f));
			} else if (smol && misil && (mamo > 20 && mamo <= 60)) {
                            weapon.setMaxAmmo(mamo * 4);
                            weapon.setAmmo(mamo * 4);
                        }
                    }
                        
                        //Yeah, don't use this. It alters weapons on EVERY ship
                        /*
			if (smol && misil && !(weapon.hasAIHint(AIHints.CONSERVE_1) || 
                                weapon.hasAIHint(AIHints.CONSERVE_2) || weapon.hasAIHint(AIHints.CONSERVE_3) || 
                                weapon.hasAIHint(AIHints.CONSERVE_4) || weapon.hasAIHint(AIHints.CONSERVE_5) || 
                                weapon.hasAIHint(AIHints.CONSERVE_ALL) || weapon.hasAIHint(AIHints.CONSERVE_FOR_ANTI_ARMOR))) {
                            weapon.getSpec().getAIHints().add(AIHints.DO_NOT_CONSERVE);
			} else if (smol && misil && (weapon.hasAIHint(AIHints.CONSERVE_1) || 
                                weapon.hasAIHint(AIHints.CONSERVE_2) || weapon.hasAIHint(AIHints.CONSERVE_3))) {
                            weapon.getSpec().getAIHints().remove(AIHints.CONSERVE_1);
                            weapon.getSpec().getAIHints().remove(AIHints.CONSERVE_2);
                            weapon.getSpec().getAIHints().remove(AIHints.CONSERVE_3);
			} else if (smol && misil && (weapon.hasAIHint(AIHints.CONSERVE_4) || weapon.hasAIHint(AIHints.CONSERVE_5))) {
                            weapon.getSpec().getAIHints().remove(AIHints.CONSERVE_4);
                            weapon.getSpec().getAIHints().remove(AIHints.CONSERVE_5);
                            weapon.getSpec().getAIHints().add(AIHints.CONSERVE_1);
			} else if (smol && misil && (weapon.hasAIHint(AIHints.CONSERVE_ALL) || weapon.hasAIHint(AIHints.CONSERVE_FOR_ANTI_ARMOR))) {
                            weapon.getSpec().getAIHints().remove(AIHints.CONSERVE_ALL);
                            weapon.getSpec().getAIHints().remove(AIHints.CONSERVE_FOR_ANTI_ARMOR);
                            weapon.getSpec().getAIHints().add(AIHints.CONSERVE_2);
			}
                        */
		}
	}
		

	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		super.advanceInCombat(ship, amount);

		if (!ship.isAlive()) return;
		
		String key = MA_DATA_KEY;
		ship.getCustomData().get(key);
		MissileAutoloaderData data = (MissileAutoloaderData) ship.getCustomData().get(key);
		if (data == null) {
			data = new MissileAutoloaderData();
			ReloadCapacityData cap = getCapacityData(ship);
			//data.opLeft = spec.getCostFor(ship.getHullSize());
			if (cap != null) {
				data.opLeft = cap.capacity;
			} else {
				data.showExhaustedStatus = 0;
			}
			ship.setCustomData(key, data);
		}
		
		if (data.opLeft <= 0.05f) {
			data.opLeft = 0f;
			data.showExhaustedStatus -= amount;
			if (data.showExhaustedStatus <= 0) {
				return;
			}
		}
		
		boolean playerShip = Global.getCurrentState() == GameState.COMBAT &&
							 Global.getCombatEngine() != null && Global.getCombatEngine().getPlayerShip() == ship;
		
		float mult = ship.getMutableStats().getMissileRoFMult().getModifiedValue();
		data.cooldown.advance(amount * mult);
		for (WeaponAPI w : data.cooldown.getItems()) {
			w.setRemainingCooldownTo(w.getCooldown());
		}
		
		data.interval.advance(amount);
		if (data.interval.intervalElapsed()) {
			boolean playSound = false;
			for (WeaponAPI w : ship.getAllWeapons()) {
				if (!isAffected(w)) continue;
				if (data.cooldown.contains(w)) continue;
				
				if (w.usesAmmo() && w.getAmmo() <= 0) {
					float reloadSize = w.getSpec().getMaxAmmo();
					float reloadCost = getReloadCost(w, ship);
					float salvoSize = w.getSpec().getBurstSize();
					if (salvoSize < 1) salvoSize = 1;
					if (reloadCost > data.opLeft) {
						float f = data.opLeft / reloadCost;
						if (f <= 0f) continue;
						
						reloadSize *= f;
						reloadSize /= salvoSize;
						reloadSize = (float) Math.ceil(reloadSize);
						reloadSize *= salvoSize;
						reloadSize = (int) Math.round(reloadSize);
					}
					
					playSound = true;
					
					w.setAmmo((int) reloadSize);
					data.cooldown.set(w, LOAD_CD);
					
					data.opLeft -= reloadCost;
					
					if (data.opLeft < 0) data.opLeft = 0;
					if (data.opLeft <= 0) break;
				}
			}
			
			playSound = false; // better without the sound I think
			if (playerShip && playSound) {
				Global.getSoundPlayer().playSound("missile_weapon_reloaded", 1f, 1f, ship.getLocation(), ship.getVelocity());
			}
		}
		
		if (playerShip) {
			String status = "" + Misc.getRoundedValueOneAfterDecimalIfNotWhole(data.opLeft) + " CAPACITY REMAINING";
			if (data.opLeft <= 0) status = "CAPACITY EXHAUSTED";
			Global.getCombatEngine().maintainStatusForPlayerShip(data,
					Global.getSettings().getSpriteName("ui", "icon_tactical_missile_autoloader"),
					spec.getDisplayName(), 
					status, data.opLeft <= 0);
			
		}
	}
	
	public static ReloadCapacityData getCapacityData(ShipAPI ship) {
		if (ship == null) return null;
		int count = 0;
		for (WeaponSlotAPI slot : ship.getHullSpec().getAllWeaponSlotsCopy()) {
			if (slot.getSlotSize() == WeaponAPI.WeaponSize.SMALL && 
					slot.getWeaponType() == WeaponAPI.WeaponType.MISSILE) {
				count++;
			}
		}
		
		for (ReloadCapacityData data : SMALSILO) {
			if (data.size == ship.getHullSize()) {
				if (count >= data.minW && count <= data.maxW) return data; 
				if (count >= data.minW && data.maxW < 0) return data;
			}
		}
		return null;
	}
	
	public static boolean isAffected(WeaponAPI w) {
		if (w == null) return false;
		if (w.getType() != WeaponAPI.WeaponType.MISSILE) return false;
		if (w.getSize() != WeaponAPI.WeaponSize.SMALL) return false;
		
		if (w.getSlot().getWeaponType() != WeaponAPI.WeaponType.MISSILE) return false;
		if (w.getSlot().getSlotSize() != WeaponAPI.WeaponSize.SMALL) return false;
		
		if (w.getSpec().hasTag(Tags.NO_RELOAD)) return false;
		if (!w.usesAmmo() || w.getAmmoPerSecond() > 0) return false;
		if (w.getMaxAmmo() > 20) return false;
		if (w.isDecorative()) return false;
		if (w.getSlot() != null && w.getSlot().isSystemSlot()) return false;
		return true;
	}
	
	public static float getReloadCost(WeaponAPI w, ShipAPI ship) {
		int op = (int) Math.round(w.getSpec().getOrdnancePointCost(null, null));
		if (op == 1 || op == 2) return 1f;
		if (op == 3 || op == 4) return 2f;
		if (op == 5) return 3f;
		if (op == 6 || op == 7) return 4f;
		if (op == 8 || op == 9) return 5f;
		return 6f;
	}
	
	@Override
	public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return false;
	}

        protected static final float LOAD_OF_BULL = 3f;
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, final ShipAPI ship, float width, boolean isForModSpec) {
		float pad = 3f;
		float opad = 10f;
		Color h = Misc.getHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();
                Color gray = Misc.getGrayColor();
		
		
		tooltip.addPara("An advanced autoloader that provides a storage, out of a shared reload capacity, to "
				+ "missile weapons installed in small missile mounts.", opad, h, "missile weapons installed in small missile mounts");
		
		tooltip.addSectionHeading("Reload capacity", Alignment.MID, opad);
		tooltip.addPara("Does not affect weapons that do not use ammo or regenerate it, or are mounted in any other type of slot."
				+ " Reload size is not affected by skills or hullmods that "
				+ "increase missile ammo capacity.", opad);
		
		if (isForModSpec || ship == null) return;
		
		tooltip.setBgAlpha(0.9f);
		
		List<WeaponAPI> weapons = new ArrayList<WeaponAPI>();
		Set<String> seen = new LinkedHashSet<String>();
		for (WeaponAPI w : ship.getAllWeapons()) {
			if (!isAffected(w)) continue;
			String id = w.getId();
			if (seen.contains(id)) continue;
			seen.add(id);
			weapons.add(w);
		}
		
		float numW = 130f;
		float reloadW = 130f;
		float sizeW = width - numW - reloadW - 10f;
		tooltip.beginTable(Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(),
				   20f, true, true, 
				   new Object [] {"Ship size", sizeW, "Small missiles", numW, "Reload capacity", reloadW});
		
		ReloadCapacityData cap = getCapacityData(ship);
		
		List<ReloadCapacityData> sortedCap = new ArrayList<ReloadCapacityData>(SMALSILO);
		Collections.sort(sortedCap, new Comparator<ReloadCapacityData>() {
			public int compare(ReloadCapacityData o1, ReloadCapacityData o2) {
				//return (int) Math.signum(o1.capacity - o2.capacity);
				if (o1.size != o2.size) {
					return (int) Math.signum(o1.size.ordinal() - o2.size.ordinal());
				}
				return (int) Math.signum(o1.capacity - o2.capacity);
			}
		});
		//sortedCap = new ArrayList<ReloadCapacityData>(CAPACITY_DATA);
		
		HullSize prev = HullSize.FRIGATE;
		for (ReloadCapacityData curr : sortedCap) {
			Color c = Misc.getGrayColor();
			if (cap == curr) {
				c = Misc.getHighlightColor();
			}
			if (curr.size != hullSize) continue;
//			if (prev != curr.size) {
//				tooltip.addRow("", "", "");
//			}
			tooltip.addRow(Alignment.MID, c, curr.getSizeStr(),
						   Alignment.MID, c, curr.getWeaponsString(),
						   Alignment.MID, c, "" + curr.capacity);
			prev = curr.size;
		}
		tooltip.addTable("", 0, opad);
		
		
		Collections.sort(weapons, new Comparator<WeaponAPI>() {
			public int compare(WeaponAPI o1, WeaponAPI o2) {
				float c1 = getReloadCost(o1, ship);
				float c2 = getReloadCost(o2, ship);
				return (int) Math.signum(c1 - c2);
			}
		});
		
		
		tooltip.addSectionHeading("Reload cost", Alignment.MID, opad + 5f);
		
		float costW = 100f;
		float nameW = width - costW - 5f;
		tooltip.beginTable(Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(),
						   20f, true, true, 
						   new Object [] {"Affected weapon", nameW, "Reload cost", costW});
		int max = 10;
		int count = 0;
		for (WeaponAPI w : weapons) {
			count++;
			float cost = getReloadCost(w, ship);
			String name = tooltip.shortenString(w.getDisplayName(), nameW - 20f);
			tooltip.addRow(Alignment.LMID, Misc.getTextColor(), name,
						   Alignment.MID, h, Misc.getRoundedValueOneAfterDecimalIfNotWhole(cost));
			if (count >= max) break;
		}
		tooltip.addTable("No affected weapons mounted", weapons.size() - max, opad);

		tooltip.addPara("A partial reload is possible when running out of capacity.", opad);
		if (LOAD_CD > 0) {
			tooltip.addPara("After a reload, the weapon requires an extra %s seconds,"
					+ " in addition to its normal cooldown, before it can fire again.", opad,
					h, "" + (int) LOAD_CD);
		}
                
                LabelAPI bullet;
		tooltip.addSectionHeading(YNA_MD.base("comp"), Alignment.MID, opad);
                tooltip.setBulletedListMode(" • ");
                //bullet = tooltip.addPara(YNA_MD.mis_load("ai"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), gray,
                //    "");
                bullet = tooltip.addPara(YNA_MD.mis_load("ammo"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "20", "4x", "60", "2.5x");
                bullet = tooltip.addPara(YNA_MD.mis_load("comp"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), gray,
                    "");
		bullet.setHighlight(YNA_MD.base("hl_mis_auto"));
		bullet.setHighlightColors(h);
                tooltip.setBulletedListMode("");
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return getCapacityData(ship) != null;
	}

	@Override
	public String getUnapplicableReason(ShipAPI ship) {
		return "Ship does not have any small missile slots";
	}
}








